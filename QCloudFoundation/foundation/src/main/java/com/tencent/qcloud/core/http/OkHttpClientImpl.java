package com.tencent.qcloud.core.http;

import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;

import java.util.concurrent.TimeUnit;

public class OkHttpClientImpl extends NetworkClient {

    private okhttp3.EventListener.Factory mEventListenerFactory = new okhttp3.EventListener.Factory() {
        @Override
        public okhttp3.EventListener create(Call call) {
            return new CallMetricsListener(call);
        }
    };

    private OkHttpClient okHttpClient;

    @Override
    public void init(QCloudHttpClient.Builder b, HostnameVerifier hostnameVerifier, Dns dns, HttpLogger httpLogger) {
        super.init(b, hostnameVerifier, dns, httpLogger);
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(httpLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient = b.mBuilder
                .followRedirects(true)
                .followSslRedirects(true)
                .hostnameVerifier(hostnameVerifier)
                .dns(dns)
                .connectTimeout(b.connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(b.socketTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(b.socketTimeout, TimeUnit.MILLISECONDS)
                .eventListenerFactory(mEventListenerFactory)
                .addInterceptor(logInterceptor)
                .addInterceptor(new RetryAndTrafficControlInterceptor(b.retryStrategy))
                .build();
    }

    @Override
    public NetworkProxy getNetworkProxy() {
        OkHttpProxy okHttpProxy = new OkHttpProxy(okHttpClient);
        return okHttpProxy;
    }
}
