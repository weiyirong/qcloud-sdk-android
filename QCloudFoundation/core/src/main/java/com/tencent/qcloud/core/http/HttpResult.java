package com.tencent.qcloud.core.http;

import java.util.List;
import java.util.Map;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class HttpResult<T> {

    private final int code;
    private final String message;
    private final Map<String, List<String>> headers;

    private final T content;

    HttpResult(HttpResponse response, T content) {
        this.code = response.code();
        this.message = response.message();
        this.headers = response.response.headers().toMultimap();
        this.content = content;
    }

    public T content() {
        return content;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public String header(String name) {
        List<String> values = headers.get(name);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }

        return null;
    }
}
