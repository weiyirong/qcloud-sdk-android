package com.tencent.qcloud.core.http;

import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.auth.QCloudSigner;
import com.tencent.qcloud.core.common.QCloudClientException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/29.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class HttpRequest<T> {

    private final Request.Builder requestBuilder;
    private final Map<String, List<String>> headers;
    private final RequestBody requestBody;
    private final String method;
    private final Object tag;
    private final URL url;
    private final ResponseBodyConverter<T> responseBodyConverter;

    HttpRequest(Builder<T> builder) {
        requestBuilder = builder.requestBuilder;
        responseBodyConverter = builder.responseBodyConverter;
        headers = builder.headers;
        method = builder.method;
        if (builder.tag == null) {
            tag = toString();
        } else {
            tag = builder.tag;
        }
        url = builder.httpUrlBuilder.build().url();

        if (builder.requestBodySerializer != null) {
            requestBody = builder.requestBodySerializer.body();
        } else {
            requestBody = null;
        }
        requestBuilder.method(builder.method, requestBody);
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public String header(String name) {
        List<String> values = headers.get(name);
        return values != null ? values.get(0) : null;
    }

    public Object tag() {
        return tag;
    }

    void setOkHttpRequestTag(String tag) {
        requestBuilder.tag(tag);
    }

    public void addHeader(String name, String value) {
        requestBuilder.addHeader(name, value);
        addHeaderNameValue(headers, name, value);
    }

    public void removeHeader(String name) {
        requestBuilder.removeHeader(name);
        headers.remove(name);
    }

    public String method() {
        return method;
    }

    public String host() {
        return url.getHost();
    }

    public String contentType() {
        return requestBody.contentType().toString();
    }

    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    public URL url() {
        return url;
    }

    ResponseBodyConverter<T> getResponseBodyConverter() {
        return responseBodyConverter;
    }

    RequestBody getRequestBody() {
        return requestBody;
    }

    Request buildRealRequest() {
        return requestBuilder.build();
    }

    QCloudSignSourceProvider getSignProvider() {
        return null;
    }

    QCloudSigner getQCloudSigner() throws QCloudClientException {
        return null;
    }

    private static void addHeaderNameValue(Map<String, List<String>> headers, String name, String value) {
        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<>(2);
            headers.put(name, values);
        }
        values.add(value.trim());
    }

    public static class Builder<T> {
        Object tag;

        String method;

        Request.Builder requestBuilder;
        HttpUrl.Builder httpUrlBuilder;

        Map<String, List<String>> headers = new HashMap<>(10);

        RequestBodySerializer requestBodySerializer;
        ResponseBodyConverter<T> responseBodyConverter;

        Builder() {
            httpUrlBuilder = new HttpUrl.Builder();
            requestBuilder = new Request.Builder();
        }

        public Builder<T> scheme(String scheme) {
            httpUrlBuilder.scheme(scheme);
            return this;
        }

        public Builder<T> tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder<T> host(String host) {
            httpUrlBuilder.host(host);
            return this;
        }

        public Builder<T> path(String path) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            if (path.length() > 0) {
                httpUrlBuilder.addPathSegments(path);
            }
            return this;
        }

        public Builder<T> method(String method) {
            this.method = method;
            return this;
        }

        public Builder<T> query(String key, String value) {
            if (key != null && value != null) {
                httpUrlBuilder.addQueryParameter(key, value);
            }
            return this;
        }

        public Builder<T> encodedQuery(String key, String value) {
            if (key != null && value != null) {
                httpUrlBuilder.addEncodedQueryParameter(key, value);
            }
            return this;
        }

        public Builder<T> query(Map<String, String> nameValues) {
            if (nameValues != null) {
                for (String name: nameValues.keySet()) {
                    String value = nameValues.get(name);
                    if (name != null && value != null) {
                        httpUrlBuilder.addQueryParameter(name, value);
                    }
                }
            }
            return this;
        }

        public Builder<T> encodedQuery(Map<String, String> nameValues) {
            if (nameValues != null) {
                for (String name: nameValues.keySet()) {
                    String value = nameValues.get(name);
                    if (name != null && value != null) {
                        httpUrlBuilder.addEncodedQueryParameter(name, value);
                    }
                }
            }
            return this;
        }

        public Builder<T> contentMD5() {
            addHeader(HttpConstants.Header.MD5, "");
            return this;
        }

        public Builder<T> addHeader(String name, String value) {
            if (name != null && value != null) {
                requestBuilder.addHeader(name, value);
                addHeaderNameValue(headers, name, value);
            }
            return this;
        }

        public Builder<T> addHeaders(Map<String, List<String>> headers) {
            if (headers != null) {
                for (String name : headers.keySet()) {
                    for (String value : headers.get(name)) {
                        if (name != null && value != null) {
                            requestBuilder.addHeader(name, value);
                            addHeaderNameValue(headers, name, value);
                        }
                    }
                }
            }
            return this;
        }

        public Builder<T> removeHeader(String name) {
            requestBuilder.removeHeader(name);
            headers.remove(name);
            return this;
        }

        public Builder<T> body(RequestBodySerializer bodySerializer) {
            requestBodySerializer = bodySerializer;
            return this;
        }

        public Builder<T> converter(ResponseBodyConverter<T> responseBodyConverter) {
            this.responseBodyConverter = responseBodyConverter;
            return this;
        }

        protected void prepareBuild() {
            requestBuilder.url(httpUrlBuilder.build());
            if (responseBodyConverter == null) {
                // default use string converter
                responseBodyConverter = (ResponseBodyConverter<T>) ResponseBodyConverter.string();
            }
        }

        public HttpRequest build() {
            prepareBuild();
            return new HttpRequest<>(this);
        }
    }
}
