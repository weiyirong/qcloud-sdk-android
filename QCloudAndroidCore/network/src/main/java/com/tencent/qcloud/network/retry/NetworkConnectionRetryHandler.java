package com.tencent.qcloud.network.retry;

import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class NetworkConnectionRetryHandler implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(NetworkConnectionRetryHandler.class);

    private int maxRetryNumber;

    private int hasRetriedTimes;

    public NetworkConnectionRetryHandler(int maxRetryNumber) {
        this.maxRetryNumber = maxRetryNumber;
        hasRetriedTimes = 0;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = null;

        while (hasRetriedTimes < maxRetryNumber) {

            try {

                QCloudLogger.debug(logger, "has retry times = {}", hasRetriedTimes);
                response = chain.proceed(request);
                break;
            } catch (ProtocolException exception) {

                // OkHttp在Http code为204时，不允许body不为空，这里为了阻止抛出异常，对response进行修改
                if (exception.getMessage().contains("HTTP " + 204 + " had non-zero Content-Length: ")) {
                    response = new Response.Builder()
                            .request(request)
                            .message(exception.getMessage())
                            .code(204)
                            .protocol(Protocol.HTTP_1_1)
                            .build();
                    break;
                } else {
                    exception.printStackTrace();
                    hasRetriedTimes++;
                }

            }catch (IOException exception) {

                // 如果是因为用户取消报的异常，则直接抛出
                if (exception.getMessage() != null && exception.getMessage().toLowerCase().equals("canceled")) {
                    throw exception;

                } else {

                    exception.printStackTrace();
                    hasRetriedTimes++;
                }


            }
        }
        //return response;
        if (response == null) {
            response = chain.proceed(request);
        }
        return response;
    }
}
