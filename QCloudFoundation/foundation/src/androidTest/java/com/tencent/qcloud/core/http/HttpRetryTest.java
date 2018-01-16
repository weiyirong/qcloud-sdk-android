package com.tencent.qcloud.core.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/12/15.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class HttpRetryTest {

    class HttpFakeRequestRetry extends RetryAndTrafficControlInterceptor {
        IOException ioException;
        HttpFakeRequestRetry(IOException ioException) {
            super();
            this.ioException = ioException;
        }

        @Override
        Response processSingleRequest(Chain chain, Request request) throws IOException {
            throw ioException;
        }
    }

    @Test
    public void testRetry() {
        IOException exception = new SocketTimeoutException();
        try {
            HttpFakeRequestRetry requestRetry = new HttpFakeRequestRetry(exception);
            HttpRequest<String> request = new HttpRequest.Builder<String>()
                    .scheme("http")
                    .method("POST")
                    .body(RequestBodySerializer.bytes(null, new byte[20]))
                    .host("www.qcloud.com").build();
            HttpTask<String> httpTask = new HttpTask<>(request, null, null);
            requestRetry.processRequest(null, null, httpTask);
        } catch (IOException e) {
            Assert.assertEquals(exception, e);
        }
    }
}
