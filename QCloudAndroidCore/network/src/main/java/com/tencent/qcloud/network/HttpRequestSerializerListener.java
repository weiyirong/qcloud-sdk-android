package com.tencent.qcloud.network;

import com.tencent.qcloud.network.exception.QCloudException;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface HttpRequestSerializerListener {

    /**
     * 业务请求序列化成功
     *
     * @param request
     */
    void onSuccess(QCloudHttpRequest request) ;

    /**
     * 业务请求序列化失败
     *
     * @param request
     * @param exception
     */
    void onFailed(QCloudHttpRequest request, QCloudException exception);

}
