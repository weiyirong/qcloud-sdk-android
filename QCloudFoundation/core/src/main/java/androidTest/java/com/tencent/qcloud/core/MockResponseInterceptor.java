package com.tencent.qcloud.core;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by wjielai on 2017/10/13.
 */

public class MockResponseInterceptor implements Interceptor {

    private Response mockResponse;
    private IOException mockException;

    public void setMockResponse(Response response) {
        mockResponse = response;
    }

    public void setMockException(IOException exception) {
        mockException = exception;
    }

    public void reset() {
        mockResponse = null;
        mockException = null;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response =  chain.proceed(chain.request());
        if (mockResponse != null) {
            return mockResponse.newBuilder().request(chain.request()).build();
        } else if (mockException != null) {
            throw  mockException;
        }

        return response;
    }
}
