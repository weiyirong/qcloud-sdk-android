package com.tencent.qcloud.core.http;


import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudCredentials;
import com.tencent.qcloud.core.auth.QCloudSigner;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.task.QCloudTask;
import com.tencent.qcloud.core.task.TaskExecutors;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import bolts.CancellationTokenSource;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;

/**
 * Created by wjielai on 2017/11/27.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public final class HttpTask<T> extends QCloudTask<HttpResult<T>> {
    private static AtomicInteger increments = new AtomicInteger(1);

    private final HttpRequest<T> httpRequest;
    private final QCloudCredentialProvider credentialProvider;
    private final QCloudHttpClient httpClient;
    private Call httpCall;
    private HttpResponse<T> httpResponse;
    private HttpResult<T> httpResult;
    private HttpMetric httpMetric;

    private QCloudProgressListener mProgressListener = new QCloudProgressListener() {
        @Override
        public void onProgress(long complete, long target) {
            HttpTask.this.onProgress(complete, target);
        }
    };

    HttpTask(HttpRequest<T> httpRequest, QCloudCredentialProvider credentialProvider,
             QCloudHttpClient httpClient) {
        super("HttpTask-" + httpRequest.tag() + "-" + increments.getAndIncrement(), httpRequest.tag());
        this.httpRequest = httpRequest;
        this.httpClient = httpClient;
        this.credentialProvider = credentialProvider;
    }

    public HttpTask<T> schedule() {
        if (httpRequest.getRequestBody() instanceof ProgressBody) {
            scheduleOn(TaskExecutors.UPLOAD_EXECUTOR, new CancellationTokenSource());
        } else if (httpRequest.getResponseBodyConverter() instanceof ProgressBody) {
            scheduleOn(TaskExecutors.DOWNLOAD_EXECUTOR, new CancellationTokenSource());
        } else {
            scheduleOn(TaskExecutors.COMMAND_EXECUTOR, new CancellationTokenSource());
        }
        return this;
    }

    public boolean isSuccessful() {
        return httpResult != null && httpResult.isSuccessful();
    }

    @Override
    public HttpResult<T> getResult() {
        return httpResult;
    }

    public HttpTask<T> attachMetric(HttpMetric httpMetric) {
        this.httpMetric = httpMetric;
        return this;
    }

    boolean isUploadTask() {
        if (httpRequest.getRequestBody() instanceof StreamingRequestBody) {
            return ((StreamingRequestBody) httpRequest.getRequestBody()).isLargeData();
        }
        return false;
    }

    boolean isDownloadTask() {
        return httpRequest.getResponseBodyConverter() instanceof ProgressBody;
    }

    double getAverageStreamingSpeed(long networkMillsTook) {
        ProgressBody body = null;

        if (httpRequest.getRequestBody() instanceof ProgressBody) {
            body = (ProgressBody) httpRequest.getRequestBody();
        } else if (httpRequest.getResponseBodyConverter() instanceof ProgressBody) {
            body = (ProgressBody) httpRequest.getResponseBodyConverter();
        }
        if (body != null) {
            return ((double) body.getBytesTransferred() / 1024) / ((double) networkMillsTook / 1000);
        }
        return 0;
    }

    @Override
    protected HttpResult<T> execute() throws QCloudClientException, QCloudServiceException {
        if (httpMetric != null) {
            httpMetric.start();
        }
        if (httpRequest.shouldCalculateContentMD5()) {
            calculateContentMD5();
        }

        QCloudSigner signer = httpRequest.getQCloudSigner();
        if (signer != null) {
            signRequest(signer, httpRequest);
        }
        if (httpRequest.getRequestBody() instanceof ProgressBody) {
            ((ProgressBody) httpRequest.getRequestBody()).setProgressListener(mProgressListener);
        }

        QCloudClientException clientException = null;
        QCloudServiceException serviceException = null;
        Response response = null;

        try {
            httpRequest.setOkHttpRequestTag(getIdentifier());
            Request okHttpRequest = httpRequest.buildRealRequest();
            httpCall = httpClient.getOkHttpCall(okHttpRequest);

            if (httpMetric != null) {
                httpMetric.setRequestUrl(okHttpRequest.url().toString());
                httpMetric.setRequestMethod(okHttpRequest.method());
                RequestBody requestBody = okHttpRequest.body();
                if (requestBody != null) {
                    httpMetric.setRequestPayloadSize(requestBody.contentLength());
                    MediaType mediaType = requestBody.contentType();
                    if (mediaType != null) {
                        httpMetric.setRequestContentType(mediaType.type());
                    }
                }
            }
            response = httpCall.execute();

            if (response != null) {
                if (httpResult == null) {
                    convertResponse(response);
                }
            } else {
                serviceException = new QCloudServiceException("http response is null");
            }

        } catch (IOException e) {
            if (e.getCause() instanceof QCloudClientException) {
                clientException = (QCloudClientException) e.getCause();
            } else if (e.getCause() instanceof QCloudServiceException) {
                serviceException = (QCloudServiceException) e.getCause();
            } else {
                clientException = new QCloudClientException(e);
            }
        } finally {
            Util.closeQuietly(response);
        }

        if (httpMetric != null) {
            httpMetric.stop();
        }

        if (clientException != null) {
            if (httpMetric != null) {
                httpMetric.traceException(clientException);
            }
            throw clientException;
        } else if (serviceException != null) {
            if (httpMetric != null) {
                httpMetric.traceException(serviceException);
            }
            throw serviceException;
        } else {
            return httpResult;
        }
    }

    @Override
    public void cancel() {
        if (httpCall != null) {
            httpCall.cancel();
        }
        super.cancel();
    }

    private void signRequest(QCloudSigner signer, HttpRequest request) throws QCloudClientException {
        if (credentialProvider == null) {
            throw new QCloudClientException("no credentials provider");
        }

        QCloudCredentials credentials = credentialProvider.getCredentials();
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

    void convertResponse(Response response) throws QCloudClientException, QCloudServiceException {
        if (httpMetric != null) {
            httpMetric.setStatusCode(response.code());
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                httpMetric.setResponsePayloadSize(responseBody.contentLength());
                MediaType mediaType = responseBody.contentType();
                if (mediaType != null) {
                    httpMetric.setResponseContentType(mediaType.type());
                }
            }
        }
        httpResponse = new HttpResponse<>(httpRequest, response);
        ResponseBodyConverter<T> converter = httpRequest.getResponseBodyConverter();
        if (converter instanceof ProgressBody) {
            ((ProgressBody) converter).setProgressListener(mProgressListener);
        }
        T content = converter.convert(httpResponse);
        httpResult = new HttpResult<>(httpResponse, content);
    }
}
