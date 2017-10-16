package com.tencent.qcloud.core.network;

import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.exception.QCloudServiceException;

/**
 *
 * 异步发送任务时，网络请求结果监听
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface QCloudResultListener<R extends QCloudRequest, T extends QCloudResult> {

    /**
     * 网络请求成功回调
     *
     * @param request
     * @param result
     */
    void onSuccess(R request, T result);

    /**
     * 网络请求失败回调
     *
     * @param request
     * @param clientException
     * @param serviceException
     */
    void onFailed(R request, QCloudClientException clientException, QCloudServiceException serviceException);



}
