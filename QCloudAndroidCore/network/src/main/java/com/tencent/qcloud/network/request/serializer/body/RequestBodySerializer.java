package com.tencent.qcloud.network.request.serializer.body;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.request.serializer.QCloudHttpRequestOrigin;

import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface RequestBodySerializer {

    /**
     *
     *
     * @return
     */
    RequestBody serialize();
}
