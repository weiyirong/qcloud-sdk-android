package com.tencent.qcloud.network.tools;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudJsonTools {

    /**
     * 将Map转化为一个JSON字符串
     * @param keyValues
     * @return
     */
    public static String Map2JsonString(Map<String, String> keyValues) {

        Map<String, Object> maps = new HashMap<>();
        maps.putAll(keyValues);
        JSONObject jsonObject = new JSONObject(maps);
        return jsonObject.toString();
    }
}
