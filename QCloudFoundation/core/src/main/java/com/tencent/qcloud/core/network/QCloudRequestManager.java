package com.tencent.qcloud.core.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.interceptors.HttpLoggingInterceptor;
import com.tencent.qcloud.core.network.interceptors.NetworkConnectionRetryInterceptor;
import com.tencent.qcloud.core.network.interceptors.RequestSerializeInterceptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudRequestManager {

    final QCloudRequestBuffer requestBuffer;

    final OkHttpClient okHttpClient;

    final String verifyHost;

    final private Handler mExecuteHandler;

    public QCloudRequestManager(QCloudServiceConfig config) {
        verifyHost = config.httpHost;

        requestBuffer = new QCloudRequestBuffer(config.maxLowPriorityRequestConcurrent,
                config.maxNormalPriorityRequestConcurrent,
                config.maxRequestConcurrentNumber);

        int maxOkHttpRequestConcurrentNumber = config.maxRequestConcurrentNumber;

        int httpConnectTimeout = config.connectionTimeout;
        int httpReadTimeout = config.socketTimeout;
        int httpWriteTimeout = config.socketTimeout;

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cache(null)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return HttpsURLConnection.getDefaultHostnameVerifier().verify(verifyHost, session);
                    }
                });
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(maxOkHttpRequestConcurrentNumber);
        dispatcher.setMaxRequests(maxOkHttpRequestConcurrentNumber);

        builder.connectTimeout(httpConnectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(httpReadTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(httpWriteTimeout, TimeUnit.MILLISECONDS)
                .dispatcher(dispatcher);

        builder.addInterceptor(new RequestSerializeInterceptor(requestBuffer));

        if (config.debuggable) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logInterceptor);
        }

        if (config.maxRetryCount > 0) {
            builder.addInterceptor(new NetworkConnectionRetryInterceptor(config.maxRetryCount,
                    requestBuffer));
        }

        if (config.interceptors.size() > 0) {
            for (Interceptor interceptor : config.interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        if (config.networkInterceptors.size() > 0) {
            for (Interceptor interceptor : config.networkInterceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }

        okHttpClient = builder.build();

        mExecuteHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                promoteSend();
            }
        };
    }


    /**
     * 异步发送网络请求
     *
     * @param request
     * @return
     */
    public <T extends QCloudResult> QCloudCall send(QCloudHttpRequest<T> request,
                                              QCloudResultListener<? extends QCloudHttpRequest<T>, T> resultListener) {
        // 缓存请求
        request.setResultListener(resultListener);
        QCloudRealCall realCall = new QCloudRealCall(request, this);
        requestBuffer.add(realCall);

        mExecuteHandler.removeCallbacksAndMessages(null);
        mExecuteHandler.sendEmptyMessage(0);

        return realCall;
    }

    /**
     * 同步发送网络请求
     *
     * @param request
     * @return
     * @throws QCloudClientException
     *
     */
    public <T extends QCloudResult> T send(QCloudHttpRequest<T> request) throws QCloudClientException {
        QCloudRealCall realCall = new QCloudRealCall(request, this);
        requestBuffer.addRunner(realCall);
        Call call = okHttpClient.newCall(realCall.getHttpRequest());
        return realCall.execute(call);
    }


    /**
     *
     * 尝试发送请求
     *
     */
    void promoteSend() {
        while (true) {
            QCloudRealCall request = requestBuffer.next();
            if (request != null) {
                if (request.isCanceled()) {
                    // drop CANCELED request
                    clearCall(request);
                } else if (!request.isExecuted()) {
                    Call call = okHttpClient.newCall(request.getHttpRequest());
                    request.enqueue(call);
                }
            } else {
                break;
            }
        }
    }

    void clearCall(QCloudRealCall call) {
        requestBuffer.remove(call);
    }

    void cancel(QCloudHttpRequest request) {
        QCloudRealCall call = requestBuffer.getCall(request);
        if (call != null) {
            call.cancel();
        }
    }

    void cancelAll() {
        List<QCloudRealCall> requests = requestBuffer.list();
        // 先把所有的任务都从队列中删掉
        requestBuffer.removeAll();
        for (QCloudRealCall request : requests) {
            request.cancel();
        }
    }

    void release() {
        cancelAll();
    }

}
