package com.tencent.qcloud.network.request.serializer.body;

import com.alibaba.fastjson.JSON;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.response.serializer.body.ResponseJsonBodyLowestSerializer;
import com.tencent.qcloud.network.tools.QCloudJsonTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.tencent.qcloud.network.common.QCloudNetWorkConst.HTTP_CONTENT_TYPE_JSON;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestJsonBodySerializer implements RequestBodySerializer {

    private Logger logger = LoggerFactory.getLogger(ResponseJsonBodyLowestSerializer.class);

    private Map<String, String> keyValues;

    private Object jsonObject;

    public RequestJsonBodySerializer(Object json) {

        this.jsonObject = json;
    }

    @Deprecated
    public RequestJsonBodySerializer() {

        this.keyValues = new HashMap<>();
    }


    @Deprecated
    public void bodyKeyValue(String key, String value) {

        keyValues.put(key, value);
    }

    @Deprecated
    public void bodyKeyValue(String key, Map<String, String> keyValues) {

        keyValues.put(key, QCloudJsonTools.Map2JsonString(keyValues));
    }

    /**
     * 将builder中的bodyKeyValues或者jsonHttpBody构建为json格式的RequestBody
     *
     * @return
     */
    @Override
    public RequestBody serialize() {

        String jsonBody;
        if (jsonObject != null) {
            jsonBody = JSON.toJSONString(jsonObject);
        } else {
            jsonBody = JSON.toJSONString(keyValues);
        }
        QCloudLogger.info(logger, jsonBody);

        if (jsonBody == null) {
            QCloudLogger.warn(logger, "json body is null");
            return null;
        }

        return RequestBody.create(MediaType.parse(HTTP_CONTENT_TYPE_JSON), jsonBody);
    }
}
