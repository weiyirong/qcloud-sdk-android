package com.tencent.qcloud.network.response.serializer.body;

import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;

import java.util.Locale;

import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseSerializerHelper {


    public static QCloudResult noBodyResult(Class cls, Response response) throws QCloudException {

        QCloudResult result = null;
        try {
            result = (QCloudResult) cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.CLASS_TRANSFORM_FAILED,
                    String.format(Locale.ENGLISH, "%s transform to QCloudResult failed", cls.toString()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.CLASS_TRANSFORM_FAILED,
                    String.format(Locale.ENGLISH, "%s transform to QCloudResult failed", cls.toString()));
        }
        if (response != null) {
            result.setHttpCode(response.code());
            result.setHttpMessage(response.message());
            result.setHeaders(response.headers().toMultimap());
        }
        return result;

    }
}
