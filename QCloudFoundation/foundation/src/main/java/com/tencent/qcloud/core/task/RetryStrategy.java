package com.tencent.qcloud.core.task;

import com.tencent.qcloud.core.http.QCloudHttpClient;
import com.tencent.qcloud.core.http.QCloudHttpRetryHandler;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * </p>
 * Created by wjielai on 2018/4/27.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class RetryStrategy {

    // 重试的指数退避
    private static final int BACKOFF_MULTIPLIER = 2;

    // 重试的初始间隔
    private static final int DEFAULT_INIT_BACKOFF = 1000;

    // 普通任务最少重试次数
    private static final int DEFAULT_ATTEMPTS = 3;

    // 普通任务最长重试间隔
    private static final int DEFAULT_MAX_BACKOFF = 4000;

    private final int initBackoff;
    private final int maxBackoff;
    private final int maxAttempts;

    private AtomicInteger nextDelayFactor = new AtomicInteger(0);

    public static RetryStrategy DEFAULT = new RetryStrategy(DEFAULT_INIT_BACKOFF,
            DEFAULT_MAX_BACKOFF, DEFAULT_ATTEMPTS);

    public static RetryStrategy FAIL_FAST = new RetryStrategy(0,
            0, 0);
    private QCloudHttpRetryHandler qCloudHttpRetryHandler = QCloudHttpRetryHandler.DEFAULT;

    public RetryStrategy(int initBackoff, int maxBackoff, int maxAttempts) {
        this.initBackoff = initBackoff;
        this.maxBackoff = maxBackoff;
        this.maxAttempts = maxAttempts;
    }

    public boolean shouldRetry(int attempts, long millstook) {
        return attempts < maxAttempts;
    }

    public int getSleepDelay() {
        int fac = nextDelayFactor.get();
        if (fac > 0) {
            return Math.min(maxBackoff, initBackoff * (int) Math.pow(BACKOFF_MULTIPLIER, fac));
        }
        return 0;
    }

    public void resetDelay() {
        nextDelayFactor.set(0);
    }

    public void addMoreDelay() {
        nextDelayFactor.addAndGet(1);
    }

    public void setRetryHandler(QCloudHttpRetryHandler qCloudHttpRetryHandler){
        this.qCloudHttpRetryHandler = qCloudHttpRetryHandler;
    }

    public QCloudHttpRetryHandler getQCloudHttpRetryHandler(){
        return qCloudHttpRetryHandler;
    }

}
