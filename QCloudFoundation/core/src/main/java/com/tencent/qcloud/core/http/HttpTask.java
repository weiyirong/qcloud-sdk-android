package com.tencent.qcloud.core.http;


import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudCredentials;
import com.tencent.qcloud.core.auth.QCloudSigner;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.task.CancelableTask;
import com.tencent.qcloud.core.task.TaskExecutors;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.Buffer;

/**
 *
 *
 * Created by wjielai on 2017/11/27.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public final class HttpTask<T> extends CancelableTask<HttpResult<T>> {
    private static AtomicInteger increments = new AtomicInteger(1);

    private final HttpRequest<T> httpRequest;
    private final QCloudCredentialProvider credentialProvider;
    private final QCloudHttpClient httpClient;
    private Call httpCall;
    private HttpResponse httpResponse;
    private HttpResult<T> httpResult;

    private QCloudProgressListener mProgressListener = new QCloudProgressListener() {
        @Override
        public void onProgress(long complete, long target) {
            HttpTask.this.onProgress(complete, target);
        }
    };

    HttpTask(HttpRequest<T> httpRequest, QCloudCredentialProvider credentialProvider,
                    QCloudHttpClient httpClient) {
        super("HttpTask#" + increments.getAndIncrement(), httpRequest.tag());
        this.httpRequest = httpRequest;
        this.httpClient = httpClient;
        this.credentialProvider = credentialProvider;
    }

    public HttpTask<T> schedule() {
        if (httpRequest.getRequestBody() instanceof StreamingRequestBody) {
            scheduleOn(TaskExecutors.STREAMING_EXECUTOR);
        } else {
            scheduleOn(TaskExecutors.COMMAND_EXECUTOR);
        }
        return this;
    }

    @Override
    public HttpResult<T> getResult() {
        return httpResult;
    }

    @Override
    protected HttpResult<T> onCommand() throws QCloudClientException, QCloudServiceException {
        if (httpRequest.header(HttpConstants.Header.MD5) != null) {
            calculateContentMD5();
        }

        QCloudSigner signer = httpRequest.getQCloudSigner();
        if (signer != null) {
            signRequest(signer, httpRequest);
        }
        if (httpRequest.getRequestBody() instanceof StreamingRequestBody) {
            ((StreamingRequestBody) httpRequest.getRequestBody()).setProgressListener(mProgressListener);
        }

        try {
            httpRequest.setOkHttpRequestTag(getIdentifier());
            Request okHttpRequest = httpRequest.buildRealRequest();
            httpCall = httpClient.getOkHttpCall(okHttpRequest);

            Response response = httpCall.execute();

            if (response != null) {
                httpResult = convertResponse(response);
            } else {
                throw new QCloudServiceException("http response is null");
            }
        } catch (IOException e) {
            throw new QCloudClientException(e);
        }

        return httpResult;
    }

    @Override
    protected boolean onCancel() {
        if (httpCall != null) {
            httpCall.cancel();
        }
        return true;
    }

    private void signRequest(QCloudSigner signer, HttpRequest request) throws QCloudClientException {
        if (credentialProvider == null) {
            throw new QCloudClientException("no credentials provider");
        }

        QCloudCredentials credentials = credentialProvider.getCredentials();

        if (credentials == null) {
            throw new QCloudClientException("can't get credentials for provider : " + credentialProvider);
        }
        if (request.getSignProvider() == null) {
            throw new QCloudClientException("no source to sign");
        }

        signer.sign((QCloudHttpRequest) request, credentials);
    }

    private void calculateContentMD5() throws QCloudClientException {
        RequestBody requestBody = httpRequest.getRequestBody();
        if (requestBody == null) {
            throw new QCloudClientException("get md5 canceled, request body is null.");
        }
        Buffer sink = new Buffer();
        try {
            requestBody.writeTo(sink);
        } catch (IOException e) {
            throw new QCloudClientException("calculate md5 error", e);
        }

        String md5 = sink.md5().base64();
        httpRequest.addHeader(HttpConstants.Header.MD5, md5);
        sink.close();
    }

    private HttpResult<T> convertResponse(Response response) throws QCloudClientException, QCloudServiceException {
        try {
            httpResponse = new HttpResponse(httpRequest, response);
            ResponseBodyConverter<T> converter = httpRequest.getResponseBodyConverter();
            if (converter instanceof ResponseFileConverter) {
                ((ResponseFileConverter) converter).setProgressListener(mProgressListener);
            }
            T content = converter.convert(httpResponse);
            return new HttpResult<>(httpResponse, content);
        } finally {
            Util.closeQuietly(response.body());
        }
    }
}
