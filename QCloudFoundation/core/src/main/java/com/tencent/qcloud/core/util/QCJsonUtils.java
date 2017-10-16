package com.tencent.qcloud.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tencent.qcloud.core.util.QCCompatibilityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCJsonUtils {

    /**
     * 将Map转化为一个JSON字符串
     * @param keyValues 键值集合
     * @return json字符串
     */
    public static String Map2JsonString(Map<String, String> keyValues) {

        Map<String, Object> maps = new HashMap<>();
        maps.putAll(keyValues);
        JSONObject jsonObject = new JSONObject(maps);
        return jsonObject.toString();
    }

    public static String serialize(Object jsonObject) {
        if (QCCompatibilityUtils.hasFastJsonClasspath()) {
            return JSON.toJSONString(jsonObject);
        } else if (QCCompatibilityUtils.hasGsonClasspath()) {
            return new Gson().toJson(jsonObject);
        }  else {
            throw new RuntimeException("could not find gson or fastjson in classpath");
        }
    }

    public static Object deSerialize(String json, Class cls) {
        if (QCCompatibilityUtils.hasFastJsonClasspath()) {
            return JSON.parseObject(json, cls);
        } else if (QCCompatibilityUtils.hasGsonClasspath()) {
            return new Gson().fromJson(json, cls);
        }  else {
            throw new RuntimeException("could not find gson or fastjson in classpath");
        }
    }
}
