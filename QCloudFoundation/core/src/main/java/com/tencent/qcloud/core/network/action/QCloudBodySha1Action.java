package com.tencent.qcloud.core.network.action;

import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudBodySha1Action implements QCloudRequestAction {

    private String headerKey;

    public QCloudBodySha1Action(String headerKey) {
        this.headerKey = headerKey;
    }

    @Override
    public Request execute(QCloudRealCall request) throws QCloudClientException {
        Request okRequest = request.getHttpRequest();
        RequestBody requestBody = okRequest.body();
        if (requestBody == null) {
            throw new QCloudClientException("get sha1 canceled: request body is null.");
        }
        Buffer sink = new Buffer();
        try {
            requestBody.writeTo(sink);
        } catch (IOException e) {
            throw new QCloudClientException("calculate sha1 error", e);
        }

        String sha1 = sink.sha1().hex();

        Request.Builder builder = okRequest.newBuilder();
        builder.addHeader(headerKey, sha1);
        sink.close();

        return builder.build();

    }

}