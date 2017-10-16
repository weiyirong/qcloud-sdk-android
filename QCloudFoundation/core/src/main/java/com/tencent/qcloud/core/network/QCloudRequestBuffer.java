package com.tencent.qcloud.core.network;

import com.tencent.qcloud.core.logger.QCloudLogger;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import okhttp3.Request;

/**
 *
 * 网络请求缓存类
 *
 * 1、输入为网络请求，输出也为网络请求
 *
 * 2、输入为PriorityQueue、无阻塞，
 *
 * 3、输出为ArrayPriorityQueue、无阻塞、有并发控制
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudRequestBuffer {

    private PriorityQueue<QCloudRealCall> cacheQueue;

    private Queue<QCloudRealCall> runningQueue;

    /**
     * low优先级请求最大并发数
     */
    private int maxLowPriorityConcurrent;

    /**
     * normal及以下优先级请求的最大并发数
     */
    private int maxNormalPriorityConcurrent;

    /**
     * high以及以下优先级请求的最大并发数
     */
    private int maxHighPriorityConcurrent;


    QCloudRequestBuffer(int maxLowPriorityConcurrent, int maxNormalPriorityConcurrent, int maxHighPriorityConcurrent) {
        this.maxLowPriorityConcurrent = maxLowPriorityConcurrent;
        this.maxHighPriorityConcurrent = maxHighPriorityConcurrent;
        this.maxNormalPriorityConcurrent = maxNormalPriorityConcurrent;

        cacheQueue = new PriorityQueue<>(10, new QCloudRequestComparator());
        runningQueue = new ArrayDeque<>();

    }


    /**
     * 给Buffer添加一个Request
     * @param request
     */
    synchronized void add(QCloudRealCall request) {
        cacheQueue.add(request);
        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] ADD %s, %d cached", request.toString(), cacheQueue.size());
    }

    synchronized void addRunner(QCloudRealCall request) {
        runningQueue.add(request);
        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] ADDR %s, %d running", request.toString(), runningQueue.size());
    }

    /**
     * 获得输出的下一个请求
     *
     * 从缓存队列中取出一个请求，并试图放入到发送队列中
     *
     * @return 放入成功，返回该请求，否则返回空
     */
    synchronized QCloudRealCall next() {
        QCloudRealCall request = cacheQueue.peek(); // 获取顶部的请求
        if (request != null) {
            if (canRunningNow(request)) {
                runningQueue.add(request);
                request = cacheQueue.remove();
                QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] PICK %s, %d running, %d cached",
                        request.toString(), runningQueue.size(), cacheQueue.size());

                return request;
            }
        }
        return null;
    }

    synchronized QCloudRealCall getCall(QCloudHttpRequest request) {
        for (QCloudRealCall runningRequest : runningQueue) {
            if (runningRequest.request() == request) {
                return runningRequest;
            }
        }
        for (QCloudRealCall cacheRequest : cacheQueue) {
            if (cacheRequest.request() == request) {
                return cacheRequest;
            }
        }

        return null;
    }

    synchronized void remove(QCloudRealCall request) {
        boolean removed = false;
        if (runningQueue.size() > 0) {
            removed = true;
            runningQueue.remove(request);
        }
        if (cacheQueue.size() > 0) {
            removed = true;
            cacheQueue.remove(request);
        }
        if (removed) {
            QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] REMOVE %s, %d running, %d cached",
                    request.toString(), runningQueue.size(), cacheQueue.size());
        }
    }

    synchronized List<QCloudRealCall> list() {
        ArrayList<QCloudRealCall> requests = new ArrayList<>(runningQueue);
        requests.addAll(cacheQueue);
        return requests;
    }

    synchronized public QCloudRealCall getRunningQCloudRealCall(Request httpRequest) {
        for (QCloudRealCall runningRequest : runningQueue) {
            if (runningRequest.getHttpRequest() == httpRequest) {
                return runningRequest;
            }
        }

        return null;
    }


    /**
     * 移除所有的请求
     */
    synchronized void removeAll() {
        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] CLEAR %d running, %d cached",
                runningQueue.size(), cacheQueue.size());
        cacheQueue.clear();
        runningQueue.clear();
    }

    private boolean canRunningNow(QCloudRealCall request) {
        switch (request.priority) {
            case Q_CLOUD_REQUEST_PRIORITY_LOW:
                return runningQueue.size() < maxLowPriorityConcurrent;
            case Q_CLOUD_REQUEST_PRIORITY_NORMAL:
                return runningQueue.size() < maxNormalPriorityConcurrent;
            case Q_CLOUD_REQUEST_PRIORITY_HIGH:
                return runningQueue.size() < maxHighPriorityConcurrent;
            default:
                return false;
        }
    }

    static class QCloudRequestComparator implements Comparator<QCloudRealCall>, Serializable {

        @Override
        public int compare(QCloudRealCall o1, QCloudRealCall o2) {
            int priority = o2.priority.getValue() - o1.priority.getValue();
            if (priority != 0) {
                return priority;
            }
            return (int) (o1.requestId - o2.requestId);
        }
    }
}
