package com.tencent.qcloud.core.network.request.serializer;

import com.tencent.qcloud.core.network.exception.QCloudClientException;

import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface RequestBodySerializer {

    /**
     * 将请求体转换成 ResponseBody
     *
     * @return ResponseBody
     * @throws QCloudClientException
     */
    RequestBody serialize() throws QCloudClientException;
}
