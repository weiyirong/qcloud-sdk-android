package com.tencent.qcloud.network.interceptors;

import android.util.Log;

import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class LoggerInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {


        Request request = chain.request();

        long t1 = System.nanoTime();
        QCloudLogger.debug(logger, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        QCloudLogger.debug(logger, String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}
