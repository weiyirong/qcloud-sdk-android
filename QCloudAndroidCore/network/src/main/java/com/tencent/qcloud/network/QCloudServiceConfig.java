package com.tencent.qcloud.network;


import com.tencent.qcloud.network.common.QCloudNetWorkConst;

import static com.tencent.qcloud.network.common.QCloudNetWorkConst.SCHEME_HTTP;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudServiceConfig {

    //服务器地址
    private String httpProtocol = SCHEME_HTTP;

    private String httpHost = "";

    private String userAgent= "";

    //超时设置
    private int connectionTimeout = 10 * 1000;
    private int socketTimeout = 10 * 1000;

    private int maxLowPriorityRequestConcurrent = 4;

    /**
     *  低优先级和普通优先级请求并发数之和不超过maxNormalPriorityRequestConcurrent
     */
    private int maxNormalPriorityRequestConcurrent = 5;

    /**
     * 低优先级、普通优先级和高优先级并发数之和不超过maxRequestConcurrentNumber
     */
    private int maxRequestConcurrentNumber = 6;

    /**
     * 阻塞任务最大并发数
     */
    private int maxActionConcurrent = 4;

    //重试次数设置
    private int maxRetryCount = 3;


    public int getMaxLowPriorityRequestConcurrent() {
        return maxLowPriorityRequestConcurrent;
    }


    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getMaxNormalPriorityRequestConcurrent() {
        return maxNormalPriorityRequestConcurrent;
    }

    public void setMaxLowPriorityRequestConcurrent(int maxLowPriorityRequestConcurrent) {
        this.maxLowPriorityRequestConcurrent = maxLowPriorityRequestConcurrent;
    }

    public void setMaxNormalPriorityRequestConcurrent(int maxNormalPriorityRequestConcurrent) {
        this.maxNormalPriorityRequestConcurrent = maxNormalPriorityRequestConcurrent;
    }


    public void setMaxRequestConcurrentNumber(int maxRequestConcurrentNumber) {
        this.maxRequestConcurrentNumber = maxRequestConcurrentNumber;
    }

    public int getMaxActionConcurrent() {
        return maxActionConcurrent;
    }

    public int getMaxRequestConcurrentNumber() {
        return maxRequestConcurrentNumber;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public QCloudServiceConfig() {

        //this.httpHost = httpHost;
        //this.httpCommonMethod = httpCommonMethod;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public void setHttpProtocol(boolean isHttps) {
        httpProtocol = isHttps ? QCloudNetWorkConst.SCHEME_HTTPS : QCloudNetWorkConst.SCHEME_HTTP;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    protected String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
