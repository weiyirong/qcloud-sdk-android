package com.tencent.qcloud.core.common;

/**
 *
 * 异步发送任务时，网络请求结果监听
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface QCloudResultListener<T> {

    /**
     * 网络请求成功回调
     *
     * @param result
     */
    void onSuccess(T result);

    /**
     * 网络请求失败回调
     *
     * @param clientException
     * @param serviceException
     */
    void onFailure(QCloudClientException clientException, QCloudServiceException serviceException);



}
