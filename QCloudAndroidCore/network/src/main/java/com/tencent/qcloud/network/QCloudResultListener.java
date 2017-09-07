package com.tencent.qcloud.network;

import com.tencent.qcloud.network.exception.QCloudException;

/**
 *
 * 异步发送任务时，网络请求结果监听
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface QCloudResultListener {

    /**
     * 网络请求成功回调
     *
     * @param request
     * @param result
     */
    void onSuccess(QCloudRequest request, QCloudResult result);

    /**
     * 网络请求失败回调
     *
     * @param request
     * @param exception
     */
    void onFailed(QCloudRequest request, QCloudException exception);



}
