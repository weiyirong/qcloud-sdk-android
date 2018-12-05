package com.tencent.qcloud.core.http;


import android.support.annotation.NonNull;

import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.QCloudTask;
import com.tencent.qcloud.core.task.RetryStrategy;
import com.tencent.qcloud.core.task.TaskManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudHttpClient {

    static final String HTTP_LOG_TAG = "QCloudHttp";

    private final OkHttpClient okHttpClient;
    private final TaskManager taskManager;
    private final HttpLogger httpLogger;

    private final Set<String> verifiedHost;
    private final Map<String, List<InetAddress>> dnsMap;

    private static volatile QCloudHttpClient gDefault;

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

    private Dns mDns = new Dns() {

        @Override
        public List<InetAddress> lookup(String hostname) throws UnknownHostException {
            if (dnsMap.containsKey(hostname)) {
                return dnsMap.get(hostname);
            }
            return Dns.SYSTEM.lookup(hostname);
        }
    };

    private okhttp3.EventListener.Factory mEventListenerFactory = new okhttp3.EventListener.Factory() {
        @Override
        public okhttp3.EventListener create(Call call) {
            return new CallMetricsListener(call);
        }
    };

    public static QCloudHttpClient getDefault() {
        if (gDefault == null) {
            synchronized (QCloudHttpClient.class) {
                if (gDefault == null) {
                    gDefault = new QCloudHttpClient.Builder().build();
                }
            }
        }

        return gDefault;
    }

    public void addVerifiedHost(String hostname) {
        if (hostname != null) {
            verifiedHost.add(hostname);
        }
    }

    public void addDnsRecord(@NonNull String hostName, @NonNull String[] ipAddress) throws UnknownHostException {
        if (ipAddress.length > 0) {
            List<InetAddress> addresses = new ArrayList<>(ipAddress.length);
            for (String ip : ipAddress) {
                addresses.add(InetAddress.getByName(ip));
            }
            dnsMap.put(hostName, addresses);
        }
    }

    public void setDebuggable(boolean debuggable) {
        httpLogger.setDebug(debuggable || QCloudLogger.isLoggableOnLogcat(QCloudLogger.DEBUG, HTTP_LOG_TAG));
    }

    private QCloudHttpClient(Builder b) {
        this.verifiedHost = new HashSet<>(5);
        this.dnsMap = new HashMap<>(3);
        this.taskManager = TaskManager.getInstance();
        httpLogger = new HttpLogger(false);
        setDebuggable(false);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(httpLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = b.mBuilder
                .followRedirects(true)
                .followSslRedirects(true)
                .hostnameVerifier(mHostnameVerifier)
                .dns(mDns)
                .connectTimeout(b.connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(b.socketTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(b.socketTimeout, TimeUnit.MILLISECONDS)
                .eventListenerFactory(mEventListenerFactory)
                .addInterceptor(logInterceptor)
                .addInterceptor(new RetryAndTrafficControlInterceptor(b.retryStrategy))
                .build();
    }

    public List<HttpTask> getTasksByTag(String tag) {
        List<HttpTask> tasks = new ArrayList<>();
        if (tag == null) {
            return tasks;
        }

        List<QCloudTask> taskManagerSnapshot = taskManager.snapshot();
        for (QCloudTask task : taskManagerSnapshot) {
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

    public final static class Builder {
        int connectionTimeout = 15 * 1000;  //in milliseconds
        int socketTimeout = 30 * 1000;  //in milliseconds
        RetryStrategy retryStrategy;
        OkHttpClient.Builder mBuilder;

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

        public Builder setRetryStrategy(RetryStrategy retryStrategy) {
            this.retryStrategy = retryStrategy;
            return this;
        }

        public Builder setInheritBuilder(OkHttpClient.Builder builder) {
            mBuilder = builder;
            return this;
        }

        public QCloudHttpClient build() {
            if (retryStrategy == null) {
                retryStrategy = RetryStrategy.DEFAULT;
            }
            if (mBuilder == null) {
                mBuilder = new OkHttpClient.Builder();
            }
            return new QCloudHttpClient(this);
        }
    }
}
