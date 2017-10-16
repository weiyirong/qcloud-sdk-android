package com.tencent.qcloud.core.network.response.serializer;

import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.util.Locale;

import okhttp3.Response;

/**
 * Created by wjielai on 2017/9/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class ResponseHelper {
    public static QCloudResult noBodyResult(Response response) {
        QCloudResult result = new QCloudResult(response.code(), response.message());
        result.setHeaders(response.headers().toMultimap());

        return result;
    }

    public static <T extends QCloudResult> T noBodyResult(Class<T> cls, Response response) throws QCloudClientException {
        T result;

        try {
            result = cls.newInstance();
        } catch (InstantiationException e) {
            throw new QCloudClientException("instant no body result failed", e);
        } catch (IllegalAccessException e) {
            throw new QCloudClientException("instant no body result failed", e);
        }
        if (response != null) {
            result.setHttpCode(response.code());
            result.setHttpMessage(response.message());
            result.setHeaders(response.headers().toMultimap());
        }

        return result;

    }
}
