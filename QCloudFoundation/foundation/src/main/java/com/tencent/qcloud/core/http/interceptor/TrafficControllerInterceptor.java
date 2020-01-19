package com.tencent.qcloud.core.http.interceptor;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.TaskManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
                    strategy.reportSpeed(request, getAverageStreamingSpeed(task, networkMillsTook));
                } else {
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
            if (e instanceof SocketTimeoutException) {
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
