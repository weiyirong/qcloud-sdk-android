package com.tencent.qcloud.core.http;

import android.util.Log;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.RetryStrategy;
import com.tencent.qcloud.core.task.TaskManager;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.qcloud.core.http.QCloudHttpClient.HTTP_LOG_TAG;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

class RetryAndTrafficControlInterceptor implements Interceptor {

    private TrafficStrategy uploadTrafficStrategy = new ModerateTrafficStrategy("UploadStrategy-", 2);
    private TrafficStrategy downloadTrafficStrategy = new AggressiveTrafficStrategy("DownloadStrategy-", 3);

    private RetryStrategy retryStrategy;

    private static class ResizableSemaphore extends Semaphore {

        ResizableSemaphore(int permit, boolean fair) {
            super(permit, fair);
        }

        @Override
        protected void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }

    /**
     * 流量控制策略
     */
    private abstract static class TrafficStrategy {

        private final int[] historySpeed = new int[5];
        private int current = 0;
        private final int maxConcurrent;
        private final String name;

        static final int MIN_FAST_SPEED = 300;
        // 当连续多次出现timeout异常时，切换为流量管控模式
        static final int MIN_TIMEOUT_COUNT = 2;

        private ResizableSemaphore controller;
        private AtomicInteger concurrent;
        private AtomicInteger historyConsecutiveTimeoutError = new AtomicInteger(0);

        TrafficStrategy(String name, int concurrent, int maxConcurrent) {
            this.name = name;
            this.maxConcurrent = maxConcurrent;
            controller = new ResizableSemaphore(concurrent, true);
            this.concurrent = new AtomicInteger(concurrent);
            QCloudLogger.d(HTTP_LOG_TAG, name + " init concurrent is " + concurrent);
        }

        void reportException(Request request, IOException exception) {
            controller.release();
        }

        void reportTimeOut(Request request) {
            if (historyConsecutiveTimeoutError.get() < 0) {
                historyConsecutiveTimeoutError.set(1);
            } else {
                historyConsecutiveTimeoutError.incrementAndGet();
            }
            if (historyConsecutiveTimeoutError.get() >= MIN_TIMEOUT_COUNT) {
                adjustConcurrentAndRelease(1);
            } else {
                controller.release();
            }
        }

        synchronized void reportSpeed(Request request, double averageSpeed) {
            historyConsecutiveTimeoutError.decrementAndGet();
            if (averageSpeed > 0) {
                QCloudLogger.d(HTTP_LOG_TAG, name + " %s streaming speed is %1.3f KBps", request, averageSpeed);
                int average = updateAverageSpeed(averageSpeed);

                // 根据最新的平均速度切换并行任务个数
                int concurrent = this.concurrent.get();
                if (average > (concurrent + 1) * MIN_FAST_SPEED && concurrent < maxConcurrent) {
                    adjustConcurrentAndRelease(concurrent + 1);
                } else if (average > 0 && average < (concurrent - 1) * MIN_FAST_SPEED && concurrent > 1) {
                    adjustConcurrentAndRelease(concurrent - 1);
                } else {
                    controller.release();
                }
            } else {
                controller.release();
            }
        }

        void waitForPermit() {
            try {
                controller.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private int updateAverageSpeed(double averageSpeed) {
            synchronized (historySpeed) {
                historySpeed[current] = (int) Math.floor(averageSpeed);
                current = (current + 1) % historySpeed.length;

                int sum = 0;
                boolean notEnoughData = false;
                for (int speed : historySpeed) {
                    if (speed == 0) {
                        notEnoughData = true;
                        break;
                    }
                    sum += speed;
                }
                return notEnoughData ? 0 : sum / historySpeed.length;
            }
        }

        private void clearAverageSpeed() {
            synchronized (historySpeed) {
                for (int i = 0; i < historySpeed.length; i++) {
                    historySpeed[i] = 0;
                }
            }
        }

        private synchronized void adjustConcurrentAndRelease(int expect) {
            int current = concurrent.get();
            int delta = expect - current;
            if (delta == 0) {
                controller.release();
            } else {
                concurrent.set(expect);
                if (delta > 0) {
                    controller.release(1 + delta);
                    clearAverageSpeed();
                } else {
                    delta *= -1;
                    controller.reducePermits(delta);
                    controller.release();
                    clearAverageSpeed();
                }
                QCloudLogger.i(HTTP_LOG_TAG, name + " adjust concurrent to " + expect);
            }
        }
    }

    /**
     * 激进的流量控制策略
     */
    private static class AggressiveTrafficStrategy extends TrafficStrategy {
        AggressiveTrafficStrategy(String name, int maxConcurrent) {
            super(name, maxConcurrent, maxConcurrent);
        }
    }

    /**
     * 保守的流量控制策略
     */
    private static class ModerateTrafficStrategy extends TrafficStrategy {
        ModerateTrafficStrategy(String name, int maxConcurrent) {
            super(name, 1, maxConcurrent);
        }
    }

    RetryAndTrafficControlInterceptor(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpTask task = (HttpTask) TaskManager.getInstance().get((String) request.tag());
        return processRequest(chain, request, task);
    }

    Response processRequest(Chain chain, Request request, HttpTask task) throws IOException {
        Response response = null;
        IOException e = null;

        if (task == null || task.isCanceled()) {
            throw new IOException("CANCELED");
        }

        int attempts = 0;
        long startTime = System.currentTimeMillis();
        TrafficStrategy strategy = getSuitableStrategy(task);

        while (attempts < 1 || retryStrategy.shouldRetry(attempts, System.currentTimeMillis() - startTime)) {
            long waitTook = 0;
            if (strategy != null) {
                long before = System.currentTimeMillis();
                strategy.waitForPermit();
                waitTook = System.currentTimeMillis() - before;
            }

            if (attempts > 0) {
                long delay = retryStrategy.getNextDelay(attempts);
                try {
                    if (delay > waitTook + 500) {
                        TimeUnit.MILLISECONDS.sleep(delay - waitTook);
                    }
                } catch (InterruptedException ex) {
                }
            }
            QCloudLogger.i(HTTP_LOG_TAG, "%s start to execute, attempts is %d", request, attempts);

            attempts++;

            long startNs = System.nanoTime();
            int statusCode = -1;
            try {
                response = executeTaskOnce(chain, request, task);
                if (task.isDownloadTask()) {
                    task.convertResponse(response);
                }
                e = null;
            } catch (IOException exception) {
                e = exception;
            } catch (QCloudClientException e1) {
                e = e1.getCause() instanceof IOException ? (IOException) e1.getCause() : new IOException(e1);
            } catch (QCloudServiceException e2) {
                e = e2.getCause() instanceof IOException ? (IOException) e2.getCause() : new IOException(e2);
                statusCode = e2.getStatusCode();
            }
            long networkMillsTook = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            if (e == null) {
                if (strategy != null) {
                    strategy.reportSpeed(request, task.getAverageStreamingSpeed(networkMillsTook));
                }
                break;
            } else if (!isUserCancelled(e) && isRecoverable(e) && isRecoverable(statusCode)) {
                QCloudLogger.i(HTTP_LOG_TAG, "%s failed for %s", request, e);
                if (strategy != null) {
                    if (e instanceof SocketTimeoutException) {
                        strategy.reportTimeOut(request);
                    } else {
                        strategy.reportException(request, e);
                    }
                }
            } else {
                QCloudLogger.i(HTTP_LOG_TAG, "%s failed for %s, and is not recoverable", request, e);
                if (strategy != null) {
                    strategy.reportException(request, e);
                }
                break;
            }
        }

        if (e != null) {
            QCloudLogger.i(HTTP_LOG_TAG, "%s ends with error, %s", request, e);
            throw e;
        }
        return response;
    }

    private TrafficStrategy getSuitableStrategy(HttpTask task) {
        return task.isDownloadTask() ? downloadTrafficStrategy : task.isUploadTask() ? uploadTrafficStrategy : null;
    }

    private Response executeTaskOnce(Chain chain, Request request, HttpTask task) throws IOException {
        try {
            if (task.isCanceled()) {
                throw new IOException("CANCELED");
            } else {
                return processSingleRequest(chain, request);
            }
        } catch (ProtocolException exception) {
            // OkHttp在Http code为204时，不允许body不为空，这里为了阻止抛出异常，对response进行修改
            if (exception.getMessage() != null && exception.getMessage().contains(
                    "HTTP " + 204 + " had non-zero Content-Length: ")) {
                return new Response.Builder()
                        .request(request)
                        .message(exception.toString())
                        .code(204)
                        .protocol(Protocol.HTTP_1_1)
                        .build();
            } else {
                exception.printStackTrace();
                throw exception;
            }

        } catch (IOException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    private boolean isUserCancelled(IOException exception) {
        return exception.getMessage() != null && exception.getMessage().toLowerCase()
                .equals("canceled");
    }

    Response processSingleRequest(Chain chain, Request request) throws IOException {
        return chain.proceed(request);
    }

    private boolean isRecoverable(int statusCode) {
        return statusCode != java.net.HttpURLConnection.HTTP_UNAUTHORIZED &&
                statusCode != HttpURLConnection.HTTP_NOT_FOUND;
    }

    private boolean isRecoverable(IOException e) {
        // If there was a protocol problem, don't recover.
        if (e instanceof ProtocolException) {
            return false;
        }

        // If there was an interruption don't recover, but if there was a timeout connecting to a route
        // we should try the next route (if there is one).
        if (e instanceof InterruptedIOException) {
            return e instanceof SocketTimeoutException;
        }

        // Look for known client-side or negotiation errors that are unlikely to be fixed by trying
        // again with a different route.
        if (e instanceof SSLHandshakeException) {
            // If the problem was a CertificateException from the X509TrustManager,
            // do not retry.
            if (e.getCause() instanceof CertificateException) {
                return false;
            }
        }
        if (e instanceof SSLPeerUnverifiedException) {
            // e.g. a certificate pinning error.
            return false;
        }

        // An example of one we might want to retry with a different route is a problem connecting to a
        // proxy and would manifest as a standard IOException. Unless it is one we know we should not
        // retry, we return true and try a new route.
        return true;
    }
}
