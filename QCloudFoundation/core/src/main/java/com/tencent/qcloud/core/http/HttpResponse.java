package com.tencent.qcloud.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Response;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/29.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class HttpResponse {

    final HttpRequest request;
    final Response response;

    HttpResponse(HttpRequest request, Response response) {
        this.request = request;
        this.response = response;
    }

    public HttpRequest request() {
        return request;
    }

    public int code() {
        return response.code();
    }

    public String message() {
        return response.message();
    }

    public String header(String name) {
        return response.header(name);
    }

    public Map<String, List<String>> headers() {
        return response.headers().toMultimap();
    }

    public final long contentLength() {
        return response.body() == null ? 0 : response.body().contentLength();
    }

    public final InputStream byteStream() {
        return response.body() == null ? null : response.body().byteStream();
    }

    public final byte[] bytes() throws IOException {
        return response.body() == null ? null : response.body().bytes();
    }

    public final String string() throws IOException {
        return response.body() == null ? null : response.body().string();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "http code = %d, http message = %s %nheader is %s",
                code(), message(), response.headers().toMultimap());
    }
}
