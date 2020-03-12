package com.tencent.qcloud.core.http.interceptor;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.RetryStrategy;
import com.tencent.qcloud.core.task.TaskManager;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.qcloud.core.http.QCloudHttpClient.HTTP_LOG_TAG;

/**
 * <p>
 * </p>
 * Created by wjielai on 2020-01-15.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class TrafficControllerInterceptor implements Interceptor {

    private TrafficStrategy uploadTrafficStrategy = new ModerateTrafficStrategy("UploadStrategy-", 2);
    private TrafficStrategy downloadTrafficStrategy = new AggressiveTrafficStrategy("DownloadStrategy-", 3);

    private static class ResizableSemaphore extends Semaphore {

        ResizableSemaphore(int permit, boolean fair) {
            super(permit, fair);
        }

        @Override
        protected void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }

    private RetryStrategy retryStrategy;

    public TrafficControllerInterceptor(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    /**
     * 流量控制策略
     */
    private abstract static class TrafficStrategy {

        private final int maxConcurrent;
        private final String name;

        static final int SINGLE_THREAD_SAFE_SPEED = 100; // 单请求安全速度 100KB/S
        static final long BOOST_MODE_DURATION = TimeUnit.SECONDS.toNanos(3);

        private ResizableSemaphore controller;
        private AtomicInteger concurrent;
        private long boostModeExhaustedTime; // 多线程模式结束时间

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
            // 出现超时直接降到单线程
            adjustConcurrent(1, true);
        }

        void reportSpeed(Request request, double averageSpeed) {
            if (averageSpeed > 0) {
                QCloudLogger.d(HTTP_LOG_TAG, name + " %s streaming speed is %1.3f KBps", request, averageSpeed);

                // 根据最新的平均速度切换并行任务个数
                int concurrent = this.concurrent.get();
                if (averageSpeed > 2.4 * SINGLE_THREAD_SAFE_SPEED && concurrent < maxConcurrent) {
                    // 达到安全速度2.4倍时，增加线程数
                    boostModeExhaustedTime = System.nanoTime() + BOOST_MODE_DURATION;
                    adjustConcurrent(concurrent + 1, true);
                } else if (averageSpeed > 1.2 * SINGLE_THREAD_SAFE_SPEED && boostModeExhaustedTime > 0) {
                    // 延续多线程模式
                    boostModeExhaustedTime = System.nanoTime() + BOOST_MODE_DURATION;
                    controller.release();
                } else if (averageSpeed > 0 && concurrent > 1 && averageSpeed < 0.7 * SINGLE_THREAD_SAFE_SPEED) {
                    // 低于安全速度的 70% 时，降低线程数
                    adjustConcurrent(concurrent - 1, true);
                } else {
                    controller.release();
                }
            } else {
                controller.release();
            }
        }

        void waitForPermit() {
            try {
                if (concurrent.get() > 1 && System.nanoTime() > boostModeExhaustedTime) {
                    adjustConcurrent(1, false);
                }
                controller.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private synchronized void adjustConcurrent(int expect, boolean release) {
            int current = concurrent.get();
            int delta = expect - current;
            if (delta == 0) {
                if (release) {
                    controller.release();
                }
            } else {
                concurrent.set(expect);
                if (delta > 0) {
                    if (release) {
                        controller.release(1 + delta);
                    }
                } else {
                    delta *= -1;
                    controller.reducePermits(delta);
                    if (release) {
                        controller.release();
                    }
                }
                QCloudLogger.i(HTTP_LOG_TAG, name + "set concurrent to " + expect);
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

    private TrafficStrategy getSuitableStrategy(HttpTask task) {
        return task.isDownloadTask() ? downloadTrafficStrategy : task.isUploadTask() ? uploadTrafficStrategy : null;
    }

    private double getAverageStreamingSpeed(HttpTask task, long networkMillsTook) {
        // unit: KB/s
        return ((double) task.getTransferBodySize() / 1024) / ((double) networkMillsTook / 1000);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpTask task = (HttpTask) TaskManager.getInstance().get((String) request.tag());
        TrafficStrategy strategy = getSuitableStrategy(task);

        // wait for traffic control if necessary
        if (strategy != null) {
            strategy.waitForPermit();
        }
        // wait for retry
        int delay = retryStrategy.getSleepDelay();
        if (delay > 0) {
            QCloudLogger.i(HTTP_LOG_TAG, "%s sleep %d(ms) for env recovery", request, delay);
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException ex) {
            }
        }

        QCloudLogger.i(HTTP_LOG_TAG, " %s begin to execute", request);
        IOException e;
        try {
            long startNs = System.nanoTime();
            Response response = processRequest(chain, request);
            // because we want to calculate the whole task duration, including downloading procedure,
            // we put download operation here
            if (task.isDownloadTask()) {
                task.convertResponse(response);
            }
            if (strategy != null) {
                if (response.isSuccessful()) {
                    long networkMillsTook = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                    retryStrategy.resetDelay();
                    strategy.reportSpeed(request, getAverageStreamingSpeed(task, networkMillsTook));
                } else {
                    if (response.code() >= 500) {
                        retryStrategy.addMoreDelay();
                    } else {
                        retryStrategy.resetDelay();
                    }
                    strategy.reportException(request, null);
                }
            }
            return response;
        }  catch (QCloudClientException e1) {
            e = e1.getCause() instanceof IOException ? (IOException) e1.getCause() : new IOException(e1);
        } catch (QCloudServiceException e2) {
            e = e2.getCause() instanceof IOException ? (IOException) e2.getCause() : new IOException(e2);
        } catch (IOException exception) {
            e = exception;
        }

        if (strategy != null) {
            if (e instanceof InterruptedIOException ||
                    e instanceof UnknownHostException ||
                    e instanceof SSLException ||
                    e instanceof SocketException) {
                retryStrategy.addMoreDelay();
                strategy.reportTimeOut(request);
            } else {
                strategy.reportException(request, e);
            }
        }
        throw e;
    }

    private Response processRequest(Chain chain, Request request) throws IOException {
        // for test
//        HttpTask task = (HttpTask) TaskManager.getInstance().get((String) request.tag());
//        if ((task.isUploadTask() || task.isDownloadTask()) && new Random().nextInt(100) % 3 == 0) {
//            throw new SocketTimeoutException("timeout, code is -1");
//        }
        return chain.proceed(request);
    }
}
