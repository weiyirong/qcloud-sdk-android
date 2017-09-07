package com.tencent.qcloud.network.response.serializer.body;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger logger = LoggerFactory.getLogger(ResponseJsonBodySerializer.class);

    private Class cls;

    public ResponseJsonBodySerializer(Class cls) {

        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudException{

        if (response == null) {
            return null;
        }

        ResponseBody responseBody = response.body();

        if (responseBody == null) {
            return null;
        }

        String jsonString = null;
        try {
            jsonString = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.HTTP_RESPONSE_PARSE_FAILED, "get response to string failed");
        } finally {
            responseBody.close();
        }
        QCloudLogger.debug(logger, jsonString);
        QCloudResult fastJsonResult = null;
        try {
            fastJsonResult = (QCloudResult) JSON.parseObject(jsonString, cls);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.HTTP_RESPONSE_PARSE_FAILED, "fast json parse json string to object failed");
        }
        if (fastJsonResult == null) {
            fastJsonResult = ResponseSerializerHelper.noBodyResult(cls, response);
        }
        return fastJsonResult;
    }


}
