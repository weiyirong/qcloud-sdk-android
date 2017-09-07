package com.tencent.qcloud.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;

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

public class QCloudRequestBuffer {

    private PriorityQueue<QCloudHttpRequest> cacheQueue;

    private Deque<QCloudHttpRequest> runningDeque;

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


    public QCloudRequestBuffer(int maxLowPriorityConcurrent, int maxNormalPriorityConcurrent, int maxHighPriorityConcurrent) {

        this.maxLowPriorityConcurrent = maxLowPriorityConcurrent;
        this.maxHighPriorityConcurrent = maxHighPriorityConcurrent;
        this.maxNormalPriorityConcurrent = maxNormalPriorityConcurrent;

        // 初始容量为5的PriorityQueue
        cacheQueue = new PriorityQueue<>(5, new QCloudRequest.QCloudRequestComparator());
        runningDeque = new ArrayDeque<>();

    }


    /**
     * 给Buffer添加一个Request
     * @param request
     */
    synchronized public void add(QCloudHttpRequest request) {

        cacheQueue.add(request);
    }

    /**
     * 获得输出的下一个请求
     *
     * 从缓存队列中取出一个请求，并试图放入到发送队列中
     *
     * @return 放入成功，返回该请求，否则返回空
     */
    synchronized public QCloudHttpRequest next() {

        QCloudHttpRequest request = cacheQueue.peek(); // 获取顶部的请求
        if (request != null) {
            if (canRunningNow(request)) {
                runningDeque.add(request);
                return cacheQueue.remove();
            }
        }
        return null;
    }

    /**
     * 请求发送成功，将running中的请求移除
     *
     * @param request
     * @return 该请求是否存在
     */
    synchronized public boolean remove(QCloudHttpRequest request) {

        return runningDeque.remove(request) || cacheQueue.remove(request);
    }

    synchronized public List<QCloudHttpRequest> list() {

        ArrayList<QCloudHttpRequest> requests = new ArrayList<>(runningDeque);
        requests.addAll(cacheQueue);
        return requests;
    }


    /**
     * 移除所有的请求
     */
    synchronized public void removeAll() {

        cacheQueue.clear();
        runningDeque.clear();
    }

    private boolean canRunningNow(QCloudHttpRequest request) {

        switch (request.getPriority()) {

            case Q_CLOUD_REQUEST_PRIORITY_LOW:
                if (runningDeque.size() < maxLowPriorityConcurrent) return true;
            case Q_CLOUD_REQUEST_PRIORITY_NORMAL:
                if (runningDeque.size() < maxNormalPriorityConcurrent) return true;
            case Q_CLOUD_REQUEST_PRIORITY_HIGH:
                if (runningDeque.size() < maxHighPriorityConcurrent) return true;
        }

        return false;
    }
}
