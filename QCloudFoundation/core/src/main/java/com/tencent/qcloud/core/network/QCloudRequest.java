package com.tencent.qcloud.core.network;


/**
 *
 * 主要工作：
 *
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudRequest<T extends QCloudResult> {

    /**
     * 请求的优先级
     */
    protected QCloudRequestPriority priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

    /**
     * 请求结果监听器
     */
    private QCloudResultListener<? extends QCloudRequest<T>, T> resultListener;

    QCloudRequest setResultListener(QCloudResultListener<? extends QCloudRequest<T>, T> resultListener) {
        this.resultListener = resultListener;
        return this;
    }

    QCloudResultListener<? extends QCloudRequest<T>, T> getResultListener() {
        return resultListener;
    }
}
