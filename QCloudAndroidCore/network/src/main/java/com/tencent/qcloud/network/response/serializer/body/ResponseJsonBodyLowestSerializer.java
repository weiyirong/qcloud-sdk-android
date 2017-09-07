package com.tencent.qcloud.network.response.serializer.body;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 * 将任意等级json字符串统一为一级json进行解析
 *
 *  如：{"code":0, "message":"ok", "data"{"id_card":"xxxxx"}}
 *  会转化为 {"code":0, "message":ok", "id_card":"xxxxx"} 后，再利用fast-json来进行转化
 *
 *  如果是 {"code":0, "message":"ok", "data_list"[{"id_card":"xxxxx"}, {"id_card":"xxxxx"}]}
 *  则不会进行转化，因为如果降级的话，id_card字段会产生冲突
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseJsonBodyLowestSerializer implements ResponseBodySerializer {

    private Logger logger = LoggerFactory.getLogger(ResponseJsonBodyLowestSerializer.class);

    private Class cls;

    public ResponseJsonBodyLowestSerializer(Class cls) {

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
            fastJsonResult = (QCloudResult) JSON.parseObject(getOneDegreeJson(jsonString), cls);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.HTTP_RESPONSE_PARSE_FAILED, "fast json parse json string to object failed");
        }
        if (fastJsonResult == null) {
            fastJsonResult = ResponseSerializerHelper.noBodyResult(cls, response);
        }
        return fastJsonResult;
    }

    /**
     * 将一个多级的json字符串统一为一个单级的json字符串
     * @param jsonStr
     * @return
     */
    private String getOneDegreeJson(String jsonStr){

        QCloudLogger.debug(logger, "before change : {}", jsonStr);

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if (jsonObject == null) {
            return null;
        }

        ShouldRecursionHolder should = new ShouldRecursionHolder(false);
        do {
            should.setShould(false);
            jsonObject = jsonDegreeDown(jsonObject, should);

        } while (should.isShould());


        QCloudLogger.debug(logger, "after change : {}", jsonObject.toString());
        return jsonObject.toString();
    }
    /**
     * 将JSON的等级降低一级
     * @return
     */
    private JSONObject jsonDegreeDown(JSONObject jsonObject, ShouldRecursionHolder should) {

        JSONObject downJson = new JSONObject();

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {

            QCloudLogger.debug(logger, "class type is "+entry.getValue().getClass().toString());

            if (entry.getValue() instanceof JSONObject) {

                JSONObject innerJson = (JSONObject) entry.getValue();
                for (Map.Entry<String, Object> innerEntry : innerJson.entrySet()) {
                    downJson.put(innerEntry.getKey(), innerEntry.getValue());
                }
                should.setShould(true);
            } else {
                downJson.put(entry.getKey(), entry.getValue());
            }
        }


        //QCloudLogger.debug(logger, "after change : {}", downJson.toString());

        return downJson;
    }

    private static class ShouldRecursionHolder {

        private boolean should;

        public ShouldRecursionHolder(boolean should) {
            this.should = should;
        }

        public boolean isShould() {
            return should;
        }

        public void setShould(boolean should) {
            this.should = should;
        }
    }
}
