package com.tencent.qcloud.core.network.interceptors;

import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.QCloudRequestBuffer;

import java.io.IOException;
import java.net.ProtocolException;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 只针对网络链接错误进行重试
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class NetworkConnectionRetryInterceptor implements Interceptor {

    private int maxRetryNumber;

    private QCloudRequestBuffer mBuffer;

    public NetworkConnectionRetryInterceptor(int maxRetryNumber, QCloudRequestBuffer buffer) {
        this.maxRetryNumber = maxRetryNumber;
        this.mBuffer = buffer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        QCloudRealCall realCall = mBuffer.getRunningQCloudRealCall(request);

        Response response = null;
        IOException e = null;
        int hasRetriedTimes = 0;

        while (hasRetriedTimes < maxRetryNumber) {
            if (realCall == null || realCall.isCanceled()) {
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
                if (response != null) {
                    response.close();
                }

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
                if (response != null) {
                    response.close();
                }

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
