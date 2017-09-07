package com.tencent.qcloud.network.interceptors;

import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class TransferToStandardResponseInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        long t1 = System.nanoTime();



        QCloudLogger.debug(logger, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        if (response.code() == 204 && response.body().contentLength() > 0) {

            QCloudLogger.info(logger, "http code is 204");

            Response.Builder builder = response.newBuilder();
            builder.body(new RealResponseBody(null, null));
            response = builder.build();
        }

        return response;
    }
}