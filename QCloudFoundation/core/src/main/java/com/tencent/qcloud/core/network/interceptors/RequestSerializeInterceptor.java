package com.tencent.qcloud.core.network.interceptors;

import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.QCloudRequestBuffer;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wjielai on 2017/8/17.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class RequestSerializeInterceptor implements Interceptor {

    private QCloudRequestBuffer mBuffer;

    public RequestSerializeInterceptor(QCloudRequestBuffer buffer) {
        mBuffer = buffer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        QCloudRealCall realCall = mBuffer.getRunningQCloudRealCall(request);

        if (realCall != null && !realCall.isCanceled()) {
            try {
                request = realCall.serializeBody(request);
                request = realCall.signRequest(request);
            } catch (QCloudClientException e) {
                throw new IOException(e.getMessage(), e);
            }
        } else {
            throw new IOException("CANCELED");
        }

        return chain.proceed(request);
    }
}
