package com.tencent.qcloud.network.response.serializer.body;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.QCloudResult;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface ResponseBodySerializer {

    /**
     *
     * @param responseBody  需要解析的ResponseBody
     *
     * @throws QCloudException
     */
    QCloudResult serialize(Response response) throws QCloudException;
}
