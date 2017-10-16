package com.tencent.qcloud.core.network.request.serializer;

import com.tencent.qcloud.core.network.exception.QCloudClientException;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestStringSerializer implements RequestBodySerializer {


    private String content;

    private String mimeType;

    public RequestStringSerializer(String content, String mimeType) {

        this.content = content;
        this.mimeType = mimeType;
    }

    @Override
    public RequestBody serialize() throws QCloudClientException {

        return RequestBody.create(MediaType.parse(mimeType), content);
    }
}
