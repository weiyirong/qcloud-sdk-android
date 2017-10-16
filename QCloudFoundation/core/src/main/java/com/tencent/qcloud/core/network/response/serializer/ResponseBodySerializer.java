package com.tencent.qcloud.core.network.response.serializer;

import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import okhttp3.Response;

/**
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface ResponseBodySerializer<T extends QCloudResult> {

    /**
     *
     * @param response  需要解析的ResponseBody
     *
     * @throws QCloudClientException
     */
    T serialize(Response response) throws QCloudClientException;
}
