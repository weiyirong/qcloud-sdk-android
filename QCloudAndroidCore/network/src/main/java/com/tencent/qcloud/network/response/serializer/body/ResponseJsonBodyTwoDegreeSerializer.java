package com.tencent.qcloud.network.response.serializer.body;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 * 将body json字符串降低一级json进行解析
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseJsonBodyTwoDegreeSerializer implements ResponseBodySerializer {

    private Logger logger = LoggerFactory.getLogger(ResponseJsonBodyTwoDegreeSerializer.class);

    private Class cls;

    public ResponseJsonBodyTwoDegreeSerializer(Class cls) {

        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudException{

        if (response == null) {
            return null;
        }
        ResponseBody responseBody = response.body();
        String jsonString = null;
        try {
            jsonString = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.HTTP_RESPONSE_BODY_PARSE_JSON_FAILED);
        } finally {
            responseBody.close();
        }
        QCloudLogger.debug(logger, jsonString);
        QCloudResult result = null;
        try {
            result = (QCloudResult) JSON.parseObject(getOneDegreeJson(jsonString), cls);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.HTTP_RESPONSE_JSON_PARSE_OBJECT_FAILED);
        }
        if (result == null) {
            result = ResponseSerializerHelper.noBodyResult(cls, response);
        }
        return result;
    }

    /**
     * 将一个单级或者两级的json字符串统一为一个单级的json字符串
     * @param jsonStr
     * @return
     */
    private String getOneDegreeJson(String jsonStr){

        QCloudLogger.debug(logger, "before change : {}", jsonStr);

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONObject json = new JSONObject();

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {

            if(entry.getValue() instanceof JSONObject){
                JSONObject json2 = (JSONObject) entry.getValue();
                for (Map.Entry<String, Object> innerEntry : json2.entrySet()) {
                    json.put(innerEntry.getKey(), innerEntry.getValue());
                }
            }else{
                json.put(entry.getKey(), entry.getValue());
            }
        }
        QCloudLogger.debug(logger, "after change : {}", json.toString());
        return json.toString();
    }

}
