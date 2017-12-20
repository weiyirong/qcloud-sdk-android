package com.tencent.qcloud.core.network.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

//public class TransferToStandardResponseInterceptor implements Interceptor {
//
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//
//        Request request = chain.request();
//
//        Response response = chain.proceed(request);
//
//        if (response.code() == 204 && response.body().contentLength() > 0) {
//
//            Response.Builder builder = response.newBuilder();
//            builder.body(new RealResponseBody(null, null));
//            response = builder.build();
//        }
//
//        return response;
//    }
//}