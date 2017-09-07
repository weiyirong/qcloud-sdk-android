package com.tencent.qcloud.network.response.serializer.http;

import com.tencent.qcloud.network.QCloudResult;

import okhttp3.Response;

/**
 * 用于解析HTTP响应
 *
 * 不解析返回的HTTP Body的具体内容
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface ResponseSerializer {

    /**
     *
     *
     * @param response
     * @return  返回是否需要继续解析Response Body
     */
    boolean serialize(Response response);
}
