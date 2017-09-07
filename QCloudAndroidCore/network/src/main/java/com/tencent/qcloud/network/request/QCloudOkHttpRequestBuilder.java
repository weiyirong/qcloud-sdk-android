package com.tencent.qcloud.network.request;

import android.net.Uri;
import android.text.TextUtils;

import com.tencent.qcloud.network.request.serializer.QCloudHttpRequestOrigin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

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

public class QCloudOkHttpRequestBuilder extends Request.Builder {


    private String qCloudHttpMethod;

    private RequestBody qCloudRequestBody;

    private HttpUrl.Builder httpUrlBuilder;


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

    public QCloudOkHttpRequestBuilder() {

        httpUrlBuilder = new HttpUrl.Builder();
        host = new StringBuilder();
        path = new StringBuilder();
        fullHost = null;
    }


    public QCloudOkHttpRequestBuilder scheme(String scheme) {

        httpUrlBuilder.scheme(scheme);
        return this;
    }

    public QCloudOkHttpRequestBuilder host(String host) {

        fullHost = host;
        return this;
    }

    public QCloudOkHttpRequestBuilder fullUrl(String url) {
        fullUrl = url;
        return this;
    }

    public QCloudOkHttpRequestBuilder hostAddFront(String part) {

        host.insert(0, part);
        return this;
    }

    public QCloudOkHttpRequestBuilder hostAddRear(String part) {

        host.append(part);
        return this;
    }

    /**
     * 在每次拼接path时，如果串不以'/'开始，会自动在串前添加 '/' ，对于尾部不做处理
     * @param part
     * @return
     */
    public QCloudOkHttpRequestBuilder pathAddFront(String part) {

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
    public QCloudOkHttpRequestBuilder pathAddRear(String part) {

        if (!part.startsWith("/")) { // 如果part没有以 / 开始，则自动添加 /
            path.append("/");
        }
        path.append(part);
        return this;
    }


    public QCloudOkHttpRequestBuilder method(String method) {

        qCloudHttpMethod = method;
        return this;
    }

    public QCloudOkHttpRequestBuilder query(String key, String value) {

        httpUrlBuilder.addQueryParameter(key, value);
        return this;
    }

    public QCloudOkHttpRequestBuilder body(RequestBody requestBody) {

        qCloudRequestBody = requestBody;
        return this;
    }


    @Override
    public Request build() {

        method(qCloudHttpMethod, qCloudRequestBody);

        if (path.toString().startsWith("/")) {
            if (path.toString().length() >= 2) {
                path = new StringBuilder(path.toString().substring(1, path.toString().length()));
            } else {
                path = new StringBuilder();
            }
        }

        if (!TextUtils.isEmpty(fullHost)) {
            httpUrlBuilder.host(fullHost);
        } else if (!TextUtils.isEmpty(host.toString())) {
            httpUrlBuilder.host(host.toString());
        }

        httpUrlBuilder.addPathSegments(path.toString());

        if (TextUtils.isEmpty(fullUrl)) {
            url(httpUrlBuilder.build());
        } else {
            url(fullUrl);
        }

        return super.build();
    }
}
