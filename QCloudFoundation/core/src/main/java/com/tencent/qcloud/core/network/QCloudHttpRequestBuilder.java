package com.tencent.qcloud.core.network;

import com.tencent.qcloud.core.network.request.serializer.RequestBodySerializer;
import com.tencent.qcloud.core.util.QCStringUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *
 *   自己封装的OkHttp3 Request构建类
 *
 *   增加了一些额外的能力：
 *
 *   1、在OkHttp3中，method和body是一起设置的，这里支持分开设置
 *
 *   2、支持逐步构造HttpUrl对象
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudHttpRequestBuilder extends Request.Builder {


    private String qCloudHttpMethod;

    private RequestBody qCloudRequestBody;

    private HttpUrl.Builder httpUrlBuilder;

    /**
     * 序列化Request请求Body的步骤
     */
    RequestBodySerializer requestBodySerializer;


    /**
     * 用于分步构造host
     */
    private StringBuilder host;

    /**
     *
     */
    private String fullHost;

    private String fullUrl;

    /**
     * 用于分步构造path
     */
    private StringBuilder path;

    public QCloudHttpRequestBuilder() {

        httpUrlBuilder = new HttpUrl.Builder();
        host = new StringBuilder();
        path = new StringBuilder();
        fullHost = null;
    }


    public QCloudHttpRequestBuilder scheme(String scheme) {

        httpUrlBuilder.scheme(scheme);
        return this;
    }

    public QCloudHttpRequestBuilder host(String host) {

        fullHost = host;
        return this;
    }

    public QCloudHttpRequestBuilder fullUrl(String url) {
        fullUrl = url;
        return this;
    }

    public QCloudHttpRequestBuilder hostAddFront(String part) {

        host.insert(0, part);
        return this;
    }

    public QCloudHttpRequestBuilder hostAddRear(String part) {

        host.append(part);
        return this;
    }

    /**
     * 在每次拼接path时，如果串不以'/'开始，会自动在串前添加 '/' ，对于尾部不做处理
     * @param part
     * @return
     */
    public QCloudHttpRequestBuilder pathAddFront(String part) {

        path.insert(0, part);
        if (!part.startsWith("/")) { // 如果part没有以 / 开始，则自动添加 /
            path.insert(0, "/");
        }
        return this;
    }

    /**
     * 在每次拼接path时，如果串不以'/'开始，会自动在串前添加 '/' ，对于尾部不做处理
     * @param part
     * @return
     */
    public QCloudHttpRequestBuilder pathAddRear(String part) {

        if (!part.startsWith("/")) { // 如果part没有以 / 开始，则自动添加 /
            path.append("/");
        }
        path.append(part);
        return this;
    }


    public QCloudHttpRequestBuilder method(String method) {

        qCloudHttpMethod = method;
        return this;
    }

    public QCloudHttpRequestBuilder query(String key, String value) {

        httpUrlBuilder.addQueryParameter(key, value);
        return this;
    }

    public QCloudHttpRequestBuilder body(RequestBody requestBody) {

        qCloudRequestBody = requestBody;
        return this;
    }

    public QCloudHttpRequestBuilder body (RequestBodySerializer bodySerializer) {
        requestBodySerializer = bodySerializer;
        return this;
    }

    private static final RequestBody EMPTY_BODY = new RequestBody() {
        @Override
        public MediaType contentType() {
            return null;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {

        }
    };

    String getHost() {
        if (!QCStringUtils.isEmpty(fullHost)) {
            return fullHost;
        } else if (host.length() > 0) {
            return host.toString();
        }

        return null;
    }

    @Override
    public Request build() {

        if (requestBodySerializer != null) {
            method(qCloudHttpMethod, EMPTY_BODY);
        } else {
            method(qCloudHttpMethod, qCloudRequestBody);
        }

        if (path.toString().startsWith("/")) {
            if (path.toString().length() >= 2) {
                path = new StringBuilder(path.toString().substring(1, path.toString().length()));
            } else {
                path = new StringBuilder();
            }
        }

        httpUrlBuilder.host(getHost());

        httpUrlBuilder.addPathSegments(path.toString());

        if (QCStringUtils.isEmpty(fullUrl)) {
            url(httpUrlBuilder.build());
        } else {
            url(fullUrl);
        }

        return super.build();
    }
}
