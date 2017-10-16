package com.tencent.qcloud.core.network.request.serializer;

import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.util.QCJsonUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.tencent.qcloud.core.network.QCloudNetWorkConstants.ContentType.JSON;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestJsonBodySerializer implements RequestBodySerializer {

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

        keyValues.put(key, QCJsonUtils.Map2JsonString(keyValues));
    }

    /**
     * 将builder中的bodyKeyValues或者jsonHttpBody构建为json格式的RequestBody
     *
     * @return RequestBody
     */
    @Override
    public RequestBody serialize() throws QCloudClientException {

        String jsonBody;
        if (jsonObject != null) {
            jsonBody = QCJsonUtils.serialize(jsonObject);
        } else if (keyValues != null) {
            jsonBody = QCJsonUtils.serialize(keyValues);
        } else {
            throw new QCloudClientException("request json body is empty");
        }

        return RequestBody.create(MediaType.parse(JSON), jsonBody);
    }
}
