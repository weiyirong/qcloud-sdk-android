package com.tencent.qcloud.core.http.interceptors;

import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.TaskManager;

import java.io.IOException;
import java.net.ProtocolException;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class HttpRetryInterceptor implements Interceptor {

    private final int maxRetry;

    public HttpRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpTask task = (HttpTask) TaskManager.getInstance().get((String) request.tag());

        Response response = null;
        IOException e = null;
        int hasRetriedTimes = 0;

        while (hasRetriedTimes < maxRetry) {
            if (task == null || task.isCanceled()) {
                throw new IOException("CANCELED");
            }

            if (hasRetriedTimes > 0) {
                QCloudLogger.i(QCloudLogger.TAG_CORE, "%s retry for %d times", request, hasRetriedTimes);
            }

            try {
                response = chain.proceed(request);
                e = null;
                break;
            } catch (ProtocolException exception) {
                // OkHttp在Http code为204时，不允许body不为空，这里为了阻止抛出异常，对response进行修改
                if (exception.getMessage() != null && exception.getMessage().contains(
                        "HTTP " + 204 + " had non-zero Content-Length: ")) {
                    response = new Response.Builder()
                            .request(request)
                            .message(exception.getMessage())
                            .code(204)
                            .protocol(Protocol.HTTP_1_1)
                            .build();
                    break;
                } else {
                    exception.printStackTrace();
                    QCloudLogger.i(QCloudLogger.TAG_CORE, "%s failed for %s", request,
                            exception.getMessage());
                    e = exception;
                    hasRetriedTimes++;
                }

            } catch (IOException exception) {
                // user has canceled request
                if (exception.getMessage() != null && exception.getMessage().toLowerCase()
                        .equals("canceled")) {
                    throw exception;

                } else {
                    exception.printStackTrace();
                    QCloudLogger.i(QCloudLogger.TAG_CORE, "%s failed for %s", request,
                            exception.getMessage());
                    e = exception;
                    hasRetriedTimes++;
                }


            }
        }

        if (e != null) {
            if (response != null) {
                response.close();
            }
            throw e;
        }
        return response;
    }
}
