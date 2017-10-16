package com.tencent.qcloud.core.network;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudServiceConfig {

    final String httpHost;

    final String userAgent;

    //超时设置
    final int connectionTimeout;
    final int socketTimeout;

    //重试次数设置
    final int maxRetryCount;

    final int maxLowPriorityRequestConcurrent;

    // 低优先级和普通优先级请求并发数之和不超过maxNormalPriorityRequestConcurrent
    final int maxNormalPriorityRequestConcurrent;

    // 低优先级、普通优先级和高优先级并发数之和不超过maxRequestConcurrentNumber
    final int maxRequestConcurrentNumber;

    // okhttp调试日志
    final boolean debuggable;

    // okhttp interceptors
    final List<Interceptor> interceptors;

    // okhttp network interceptors
    final List<Interceptor> networkInterceptors;

    protected QCloudServiceConfig(Builder builder) {
        connectionTimeout = builder.connectionTimeout;
        socketTimeout = builder.socketTimeout;
        maxRetryCount = builder.maxRetryCount;
        maxLowPriorityRequestConcurrent = builder.maxLowPriorityRequestConcurrent;
        maxNormalPriorityRequestConcurrent = builder.maxNormalPriorityRequestConcurrent;
        maxRequestConcurrentNumber = builder.maxRequestConcurrentNumber;
        httpHost = builder.host;
        userAgent = builder.userAgent;
        debuggable = builder.debuggable;
        interceptors = builder.interceptors;
        networkInterceptors = builder.networkInterceptors;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public static class Builder<T> {

        String host;
        String userAgent;

        boolean debuggable;

        int connectionTimeout = 15 * 1000;  //in milliseconds
        int socketTimeout = 15 * 1000;  //in milliseconds
        int maxRetryCount = 3;

        int maxLowPriorityRequestConcurrent = 4;
        int maxNormalPriorityRequestConcurrent = 5;
        int maxRequestConcurrentNumber = 6;

        // okhttp interceptors
        List<Interceptor> interceptors;

        // okhttp network interceptors
        List<Interceptor> networkInterceptors;

        public Builder() {
            interceptors = new ArrayList<Interceptor>();
            networkInterceptors = new ArrayList<Interceptor>();
        }

        public T addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return (T) this;
        }

        public T addNetworkInterceptor(Interceptor interceptor) {
            networkInterceptors.add(interceptor);
            return (T) this;
        }

        public T setConnectionTimeout(int connectionTimeout) {
            if (connectionTimeout < 10 * 1000) {
                throw new IllegalArgumentException("connection timeout must be larger than 10 seconds.");
            }
            this.connectionTimeout = connectionTimeout;
            return (T) this;
        }

        public T setSocketTimeout(int socketTimeout) {
            if (socketTimeout < 10 * 1000) {
                throw new IllegalArgumentException("socket timeout must be larger than 10 seconds.");
            }
            this.socketTimeout = socketTimeout;
            return (T) this;
        }

        public T setMaxRetryCount(int maxRetryCount) {
            if (maxRetryCount < 2) {
                throw new IllegalArgumentException("the system must have retry strategy");
            }
            this.maxRetryCount = maxRetryCount;
            return (T) this;
        }

        public T setDebuggable(boolean debuggable) {
            this.debuggable = debuggable;
            return (T) this;
        }

        protected void setMaxLowPriorityRequestConcurrent(int maxLowPriorityRequestConcurrent) {
            this.maxLowPriorityRequestConcurrent = maxLowPriorityRequestConcurrent;
        }

        protected void setMaxNormalPriorityRequestConcurrent(int maxNormalPriorityRequestConcurrent) {
            this.maxNormalPriorityRequestConcurrent = maxNormalPriorityRequestConcurrent;
        }

        protected void setMaxRequestConcurrentNumber(int maxRequestConcurrentNumber) {
            this.maxRequestConcurrentNumber = maxRequestConcurrentNumber;
        }

        protected void setHost(String host) {
            this.host = host;
        }

        protected void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public QCloudServiceConfig build() {
            return new QCloudServiceConfig(this);
        }
    }
}
