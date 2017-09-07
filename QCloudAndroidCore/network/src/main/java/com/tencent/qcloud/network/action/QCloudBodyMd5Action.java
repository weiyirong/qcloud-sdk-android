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
 *
 * 计算Request Body的MD5值，并用放在Request的header中
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudBodyMd5Action extends QCloudRequestAction {


    private Logger logger = LoggerFactory.getLogger(QCloudBodyMd5Action.class);

    public QCloudBodyMd5Action(QCloudHttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public void execute() throws Exception {

        RequestBody requestBody = httpRequest.getHttpRequest().body();
        if (requestBody == null) {

            QCloudLogger.warn(logger, "calculate md5 failed, request body is null.");
            return ;
        }
        Buffer sink = new Buffer();
        try {
            requestBody.writeTo(sink);

            String md5 = sink.md5().base64();
            QCloudLogger.debug(logger, "md5 string is {}", md5);

            Request.Builder builder = httpRequest.getHttpRequest().newBuilder();
            builder.addHeader(QCloudNetWorkConst.HTTP_HEADER_MD5, md5);
            httpRequest.setHttpRequest(builder.build());
        } finally {
            sink.close();
        }
    }

    @Override
    public String toString() {
        return "MD5 Action";
    }
}
