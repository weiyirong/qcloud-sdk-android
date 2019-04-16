package com.tencent.qcloud.core.http;

import com.tencent.qcloud.core.task.RetryStrategy;
import okhttp3.Dns;

import javax.net.ssl.HostnameVerifier;

public abstract class NetworkClient {

    protected RetryStrategy retryStrategy;
    protected HttpLogger httpLogger;
    protected boolean enableDebugLog;

    public void init(QCloudHttpClient.Builder b, HostnameVerifier hostnameVerifier, Dns dns, HttpLogger httpLogger){
        this.retryStrategy = b.retryStrategy;
        this.httpLogger = httpLogger;
        this.enableDebugLog = b.enableDebugLog;
    }

    public abstract NetworkProxy getNetworkProxy();

}
