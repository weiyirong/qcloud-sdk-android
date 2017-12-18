package com.tencent.qcloud.core.http;


import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.http.interceptors.HttpLoggingInterceptor;
import com.tencent.qcloud.core.http.interceptors.HttpRetryInterceptor;
import com.tencent.qcloud.core.task.Task;
import com.tencent.qcloud.core.task.TaskManager;
import com.tencent.qcloud.core.util.QCloudStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudHttpClient {

    private final OkHttpClient okHttpClient;
    private final String host;
    private final String userAgent;
    private final TaskManager taskManager;

    private QCloudHttpClient(Builder b) {
        this.host = b.host;
        this.userAgent = b.userAgent;
        this.taskManager = TaskManager.getInstance();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                    }
                });

        if (!b.cacheEnabled) {
            builder.cache(null);
        }
        builder.connectTimeout(b.connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(b.socketTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(b.socketTimeout, TimeUnit.MILLISECONDS);

        if (b.debuggable) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logInterceptor);
        }

        if (b.maxRetryCount > 0) {
            builder.addInterceptor(new HttpRetryInterceptor(b.maxRetryCount));
        }

        okHttpClient = builder.build();
    }

    public List<HttpTask> getTasksByTag(String tag) {
        List<HttpTask> tasks = new ArrayList<>();
        if (tag == null) {
            return tasks;
        }

        List<Task> taskManagerSnapshot = taskManager.snapshot();
        for (Task task : taskManagerSnapshot) {
            if (task instanceof HttpTask && tag.equals(task.getTag())) {
                tasks.add((HttpTask) task);
            }
        }

        return tasks;
    }

    public <T> HttpTask<T> resolveRequest(HttpRequest<T> request) {
        return handleRequest(request, null);
    }

    public <T> HttpTask<T> resolveRequest(QCloudHttpRequest<T> request,
                                          QCloudCredentialProvider credentialProvider) {
        return handleRequest(request, credentialProvider);
    }

    Call getOkHttpCall(Request okHttpRequest) {
        return okHttpClient.newCall(okHttpRequest);
    }

    private <T> HttpTask<T> handleRequest(HttpRequest<T> request,
                                            QCloudCredentialProvider credentialProvider) {
        if (!QCloudStringUtils.isEmpty(host)) {
            request.addHeader(HttpConstants.Header.HOST, request.host());
        }
        if (!QCloudStringUtils.isEmpty(userAgent)) {
            request.addHeader(HttpConstants.Header.USER_AGENT, userAgent);
        }

        return new HttpTask<>(request, credentialProvider, this);
    }

    public final static class Builder {
        String host;
        String userAgent;

        boolean debuggable;

        int connectionTimeout = 15 * 1000;  //in milliseconds
        int socketTimeout = 15 * 1000;  //in milliseconds
        int maxRetryCount = 3;
        boolean cacheEnabled = true;

        public Builder() {
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            if (connectionTimeout < 10 * 1000) {
                throw new IllegalArgumentException("connection timeout must be larger than 10 seconds.");
            }
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setSocketTimeout(int socketTimeout) {
            if (socketTimeout < 10 * 1000) {
                throw new IllegalArgumentException("socket timeout must be larger than 10 seconds.");
            }
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder setMaxRetryCount(int maxRetryCount) {
            if (maxRetryCount < 2) {
                throw new IllegalArgumentException("the system must have retry strategy");
            }
            this.maxRetryCount = maxRetryCount;
            return this;
        }

        public Builder setDebuggable(boolean debuggable) {
            this.debuggable = debuggable;
            return this;
        }

        public Builder setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public QCloudHttpClient build() {
            return new QCloudHttpClient(this);
        }
    }
}
