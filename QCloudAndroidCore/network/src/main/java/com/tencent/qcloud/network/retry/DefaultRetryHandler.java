package com.tencent.qcloud.network.retry;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class DefaultRetryHandler implements Interceptor {

    private int maxRetryNumber;

    private int hasRetriedTimes;

    public DefaultRetryHandler(int maxRetryNumber) {
        this.maxRetryNumber = maxRetryNumber;
        hasRetriedTimes = 0;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);

        while (hasRetriedTimes < maxRetryNumber && (response==null || !response.isSuccessful())) {

            hasRetriedTimes++;
            response = chain.proceed(request);
        }
        return response;
    }
}
