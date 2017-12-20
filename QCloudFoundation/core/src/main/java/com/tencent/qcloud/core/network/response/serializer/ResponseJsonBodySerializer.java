package com.tencent.qcloud.core.network.response.serializer;

import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.util.QCJsonUtils;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 * 不对后台返回的JSON字符串做任何处理，直接转换为对应的结果类
 *
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseJsonBodySerializer implements ResponseBodySerializer {

    private Class cls;

    public ResponseJsonBodySerializer(Class cls) {

        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudClientException {

        if (response == null) {
            return null;
        }
        ResponseBody responseBody = response.body();

        if (responseBody == null) {
            throw new QCloudClientException("response body is empty");
        }

        try {
            String jsonString = responseBody.string();
            return (QCloudResult) QCJsonUtils.deSerialize(jsonString, cls);
        } catch (IOException e) {
            throw new QCloudClientException("parse http json body error", e);
        }
    }


}
