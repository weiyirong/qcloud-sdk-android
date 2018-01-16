package com.tencent.qcloud.core.http;


import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.Task;
import com.tencent.qcloud.core.task.TaskManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    static final String HTTP_LOG_TAG = "QCloudHttp";

    private final OkHttpClient okHttpClient;
    private final TaskManager taskManager;
    private final HttpLoggingInterceptor logInterceptor;

    private final Set<String> verifiedHost;

    private static final QCloudHttpClient DEFAULT = new QCloudHttpClient.Builder().build();

    private HostnameVerifier mHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (verifiedHost.size() > 0) {
                for (String host : verifiedHost) {
                    if (HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session)) {
                        return true;
                    }
                }
            }
            return HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session);
        }
    };

    public static QCloudHttpClient getDefault() {
        return DEFAULT;
    }

    public void addVerifiedHost(String hostname) {
        if (hostname != null) {
            verifiedHost.add(hostname);
        }
    }

    public void setDebuggable(boolean debuggable) {
        logInterceptor.setLevel(debuggable || QCloudLogger.isTagLoggable(HTTP_LOG_TAG) ?
                HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
    }

    private QCloudHttpClient(Builder b) {
        this.verifiedHost = new HashSet<>(5);
        this.taskManager = TaskManager.getInstance();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .hostnameVerifier(mHostnameVerifier);

        builder.connectTimeout(b.connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(b.socketTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(b.socketTimeout, TimeUnit.MILLISECONDS);

        logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                QCloudLogger.i(HTTP_LOG_TAG, message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        builder.addInterceptor(logInterceptor);
        setDebuggable(false);

        builder.addInterceptor(new RetryAndTrafficControlInterceptor());

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
        request.addHeader(HttpConstants.Header.HOST, request.host());

        return new HttpTask<>(request, credentialProvider, this);
    }

    private final static class Builder {
        int connectionTimeout = 15 * 1000;  //in milliseconds
        int socketTimeout = 30 * 1000;  //in milliseconds

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

        public QCloudHttpClient build() {
            return new QCloudHttpClient(this);
        }
    }
}
