package com.tencent.qcloud.network.action;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudBodySha1Action extends QCloudRequestAction {

    public QCloudBodySha1Action(QCloudHttpRequest httpRequest) {
        super(httpRequest);
    }

    private Logger logger = LoggerFactory.getLogger(QCloudBodySha1Action.class);

    @Override
    public void execute() throws Exception {

        RequestBody requestBody = httpRequest.getHttpRequest().body();
        if (requestBody == null) {

            QCloudLogger.warn(logger, "calculate sha1 failed, request body is null.");
            return;
        }
        Buffer sink = new Buffer();
        try {
            requestBody.writeTo(sink);

            String sha1 = sink.sha1().hex();
            QCloudLogger.debug(logger, "sha1 string is {}", sha1);

            Request.Builder builder = httpRequest.getHttpRequest().newBuilder();
            builder.addHeader(QCloudNetWorkConst.HTTP_HEADER_CONTENT_SHA1, sha1);
            httpRequest.setHttpRequest(builder.build());
        } finally {
            sink.close();
        }

    }

    @Override
    public String toString() {
        return "SHA1 Action";
    }
}