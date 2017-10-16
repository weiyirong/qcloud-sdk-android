package com.tencent.qcloud.core.network.action;

import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 *
 * 计算Request Body的MD5值，并用放在Request的header中
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudBodyMd5Action implements QCloudRequestAction {

    @Override
    public Request execute(QCloudRealCall request) throws QCloudClientException {
        Request okRequest = request.getHttpRequest();
        RequestBody requestBody = okRequest.body();
        if (requestBody == null) {
            throw new QCloudClientException("get md5 canceled, request body is null.");
        }
        Buffer sink = new Buffer();
        try {
            requestBody.writeTo(sink);
        } catch (IOException e) {
            throw new QCloudClientException("calculate md5 error", e);
        }

        String md5 = sink.md5().base64();

        Request.Builder builder = okRequest.newBuilder();
        builder.addHeader(QCloudNetWorkConstants.HttpHeader.MD5, md5);
        sink.close();

        return builder.build();
    }
}
