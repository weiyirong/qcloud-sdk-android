package com.tencent.qcloud.network;

import android.content.Context;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.action.QCloudActionManager;
import com.tencent.qcloud.network.interceptors.LoggerInterceptor;
import com.tencent.qcloud.network.interceptors.TransferToStandardResponseInterceptor;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.response.serializer.body.ResponseSerializerHelper;
import com.tencent.qcloud.network.retry.DefaultRetryHandler;
import com.tencent.qcloud.network.retry.NetworkConnectionRetryHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudRequestManager {

    private static final Logger logger = LoggerFactory.getLogger(QCloudRequestManager.class);

    final QCloudRequestBuffer requestBuffer;

    final private QCloudActionManager actionManager;

    /**
     * OkHttp的最大并发数
     */
    final private int maxOkHttpRequestConcurrentNumber;

    private int httpConnectTimeout;

    private int httpReadTimeout;

    private int httpWriteTimeout;

    private OkHttpClient okHttpClient;

    private String verifyHost;

    final private Context context;

    final private QCloudServiceConfig serviceConfig;

    public QCloudRequestManager(Context context, QCloudServiceConfig config) {

        this.context = context;
        verifyHost = config.getHttpHost();

        requestBuffer = new QCloudRequestBuffer(config.getMaxLowPriorityRequestConcurrent(),
                config.getMaxNormalPriorityRequestConcurrent(),
                config.getMaxRequestConcurrentNumber());

        maxOkHttpRequestConcurrentNumber = config.getMaxRequestConcurrentNumber();

        httpConnectTimeout = config.getConnectionTimeout();
        httpReadTimeout = config.getSocketTimeout();
        httpWriteTimeout = config.getSocketTimeout();

        this.serviceConfig = config;

        actionManager = new QCloudActionManager(config.getMaxActionConcurrent());

        initOkHttpClient();
    }

    private void initOkHttpClient() {


        if (okHttpClient==null) {

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .retryOnConnectionFailure(false)
                    .cache(null)
                    //.addNetworkInterceptor(new TransferToStandardResponseInterceptor())
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

            if (serviceConfig.getMaxRetryCount() > 0) {
                //builder.addInterceptor(new DefaultRetryHandler(serviceConfig.getMaxRetryCount()));
                builder.addInterceptor(new NetworkConnectionRetryHandler(serviceConfig.getMaxRetryCount()));
            }

            okHttpClient = builder.build();
        }
    }


    /**
     * 异步发送网络请求
     *
     * @param request
     * @return
     */
    public void send(final QCloudHttpRequest request, QCloudResultListener resultListener) {

        // 缓存请求
        request.setResultListener(resultListener);
        requestBuffer.add(request);

        promoteSend();
    }


    /**
     *
     * 尝试发送请求
     *
     */
    private void promoteSend() {



        while (true) {
            // 1、获取请求
            final QCloudHttpRequest sendRequest = requestBuffer.next();
            if (sendRequest != null && !sendRequest.isCancelled()) { // 找到了可以发送的请求

                // 1、判断该请求是否可以发送
                if (!sendRequest.httpStart()) {

                    QCloudLogger.warn(logger, "your request can't be execute anymore.");
                    return;
                }

                // 2、处理发送前的耗时操作
                QCloudLogger.debug(logger, "your request is prepare to send.");
                final QCloudResultListener resultListener = sendRequest.getResultListener();
                sendRequest.serialize(actionManager, serviceConfig, new HttpRequestSerializerListener() {
                    @Override
                    public void onSuccess(QCloudHttpRequest request) {
                        QCloudLogger.info(logger, "block task success, will go to real send.");
                        // 真正开始发送请求
                        if (!sendRequest.isCancelled()) { // 保证在setCall之前的cancel有效，在setCall之后的cancel由call.cancel方法保证
                            realSend(request);
                        } else {
                            QCloudLogger.info(logger, String.format(Locale.ENGLISH, "the request %d has cancelled.", request.getRequestId()));
                            if (resultListener != null) {
                                resultListener.onFailed(sendRequest, new QCloudException(QCloudExceptionType.CANCELED, ""));
                            }
                        }
                    }

                    @Override
                    public void onFailed(QCloudHttpRequest request, QCloudException exception) {
                        QCloudLogger.error(logger, "block task failed");
                        if (resultListener != null) {
                            resultListener.onFailed(sendRequest, exception);
                        }
                    }
                });
            } else {
                break;
            }
        }
    }

    private void realSend(final QCloudHttpRequest request) {

        Call call = null;
        if (request.httpRequest != null) {
            call = okHttpClient.newCall(request.httpRequest);
        }
        if (call == null) {
            QCloudLogger.info(logger, "your request can't to send.");
            return ;
        }
        request.setCall(call);

        QCloudLogger.debug(logger, request.httpRequest.url().toString());
        QCloudLogger.debug(logger, request.httpRequest.headers().toString());

        QCloudLogger.debug(logger, "request enqueue");
        request.getCall().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
                QCloudResultListener resultListener = request.getResultListener();
                if (resultListener != null) {

                    //resultListener.onFailed(request, new QCloudException(QCloudExceptionType.REQUEST_EXECUTE_FAILED, e.getMessage()));
                    resultListener.onFailed(request, new QCloudException(QCloudExceptionType.REQUEST_EXECUTE_FAILED, e.toString()));
                }
                requestBuffer.remove(request);
                promoteSend();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                QCloudResultListener resultListener = request.getResultListener();

                try {
                    String headers = response.request().headers().toString();
                    System.out.println(headers);

                    if (resultListener != null) {

                        QCloudResult result = null;
                        boolean next = request.getResponseSerializer().serialize(response);

                        if (next) {
                            result = request.getResponseBodySerializer().serialize(response);
                            if (result != null) {
                                result.setHttpCode(response.code());
                                result.setHttpMessage(response.message());
                                result.setHeaders(response.headers().toMultimap());
                            }
                        }
                        resultListener.onSuccess(request, result);
                        requestBuffer.remove(request);
                        promoteSend();
                    }
                } catch (QCloudException e) {
                    e.printStackTrace();
                    if (resultListener != null) {
                        resultListener.onFailed(request, new QCloudException(QCloudExceptionType.HTTP_RESPONSE_PARSE_FAILED));
                    }
                    throw new IOException(e.getDetailMessage(), e);
                } finally {
                    if (response.body()!=null) {
                        response.body().close();
                    }
                }
            }
        });

    }


    /**
     * 同步发送网络请求
     *
     * @param request
     * @return
     * @throws QCloudException
     *
     * 不再用Call.execute()来执行同步任务，所有同步任务作为异步任务执行，在返回结果时停止阻塞
     */
    public QCloudResult send(QCloudHttpRequest request) throws  QCloudException {

        final ResponseHolder responseHolder = new ResponseHolder();
        send(request, new QCloudResultListener() {
            @Override
            synchronized public void onSuccess(QCloudRequest request, QCloudResult result) {
                //QCloudLogger.debug(logger, "同步发送成功");
                responseHolder.setResponse(result);
                request.finishBlock();
            }

            @Override
            synchronized public void onFailed(QCloudRequest request, QCloudException exception) {
                //QCloudLogger.debug(logger, "同步发送失败"+" : "+exception.toString());
                responseHolder.setQCloudException(exception);
                request.finishBlock();
            }
        });
        try {
            request.startBlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (responseHolder.getQCloudException()!=null) {

            throw responseHolder.getQCloudException();
        }
        return responseHolder.getResponse();
    }


    /**
     * 取消网络请求
     *
     *
     *
     */
    public boolean cancel(QCloudHttpRequest request) {

        if (request == null) {

            QCloudLogger.warn(logger, "you can't cancel a null request.");
            return false;
        }

        //request.finishBlock(); // 如果是同步发送任务，则先取消阻塞

        if (!requestBuffer.remove(request)) { // 先将该Request从Buffer中移除

            QCloudLogger.warn(logger, String.format(Locale.ENGLISH, "the request %d no exist in buffer.", request.getRequestId()));
            return false;
        }

        if (!request.httpCancel()) { // 然后取消任务，这个任务

            QCloudLogger.warn(logger, String.format(Locale.ENGLISH, "the request %d has cancelled before.", request.getRequestId()));
            return false;
        }
        QCloudLogger.debug(logger, String.format(Locale.ENGLISH, "the request %d has cancel success.", request.getRequestId()));
        return true;
    }

    /**
     * 取消所有任务
     */
    public void cancelAll() {


        List<QCloudHttpRequest> requests = requestBuffer.list();
        for (QCloudHttpRequest request : requests) {
            cancel(request);
        }
    }

    /**
     * 释放资源
     */
    public void release() {

        cancelAll();
        actionManager.release();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }


    private static class ResponseHolder {

        QCloudResult response;

        QCloudException qCloudException;

        public void setResponse(QCloudResult response) {
            this.response = response;
        }

        public QCloudResult getResponse() {
            return response;
        }

        synchronized public void setQCloudException(QCloudException qCloudException) {
            this.qCloudException = qCloudException;
        }

        synchronized public QCloudException getQCloudException() {
            return qCloudException;
        }
    }

}
