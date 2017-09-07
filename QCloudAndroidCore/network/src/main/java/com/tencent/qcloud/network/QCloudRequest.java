package com.tencent.qcloud.network;


import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

/**
 *
 * 主要工作：
 *
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudRequest {

    /**
     * Guard by QCloudRequest.class
     */
    private static long requestIdCounter = 1;

    private Logger logger = LoggerFactory.getLogger(QCloudRequest.class);

    /**
     * 请求的ID号
     */
    final private long requestId;

    /**
     * 请求的优先级
     */
    protected QCloudRequestPriority priority;

    /**
     * 请求结果监听器
     */
    private QCloudResultListener resultListener;

    /**
     * 请求进度监听器
     */
    //private QCloudProgressListener progressListener;

    /**
     * 将异步请求转为同步
     */
    private CountDownLatch countDownLatch;

    /**
     * 该请求是否已经调用了发送
     *
     * Guard by this
     */
    private boolean started;

    /**
     * 该请求是否已经调用了取消
     *
     * Guard by this
     */
    private boolean cancelled;


    public QCloudRequest() {

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;
        cancelled = false;
        synchronized (QCloudRequest.class) {
            requestId = requestIdCounter++;
        }
    }

    /**
     * 开始阻塞
     *
     * @throws InterruptedException 在阻塞期间被打断
     */
    void startBlock() throws InterruptedException{

        countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    /**
     * 停止阻塞。
     *
     * 该方法可以多次调用，但有效的终止阻塞只有一次
     */
    void finishBlock() {

        countDownLatch.countDown();
    }

    /**
     * 设置请求为已经调用过执行状态了，无法再次调用
     *
     * @return true 请求没有执行过、也没有取消过
     *         false 请求已经执行过了，或者已经取消过了
     */
    synchronized boolean start() {


        if (!started && !cancelled) {
            started = true;
            return true;
        }
        return false;
    }

    /**
     * 设置请求为已经取消状态
     *
     * @return true 请求没有取消过
     *         false 请求已经取消过了
     */
    synchronized boolean cancel() {
        QCloudLogger.debug(logger, "cancel request {}, cancel status is {}", requestId, cancelled);
        if (cancelled) {
            return false;
        }
        cancelled = true;
        return true;
    }

    synchronized public boolean isCancelled() {
        return cancelled;
    }

    public void setResultListener(QCloudResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public long getRequestId() {
        return requestId;
    }

    public QCloudRequestPriority getPriority() {
        return priority;
    }

    public QCloudResultListener getResultListener() {
        return resultListener;
    }

    static class QCloudRequestComparator implements Comparator<QCloudRequest> {

        @Override
        public int compare(QCloudRequest o1, QCloudRequest o2) {

            return o1.priority.getValue() - o2.priority.getValue();
        }
    }

    public enum QCloudRequestPriority {

        Q_CLOUD_REQUEST_PRIORITY_HIGH(2),

        Q_CLOUD_REQUEST_PRIORITY_NORMAL(1),

        Q_CLOUD_REQUEST_PRIORITY_LOW(0);

        int value;

        QCloudRequestPriority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
