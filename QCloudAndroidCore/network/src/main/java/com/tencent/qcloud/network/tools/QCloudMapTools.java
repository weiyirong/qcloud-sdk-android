package com.tencent.qcloud.network.tools;

import android.net.Uri;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudMapTools {


    // 先url encode编码，然后转化为小写，最后进行排序
    public static Map<String, String> letterDownSort(Map<String, String> keyValues, Set<String> keys)  {

        Map<String, String> sortedMap = new TreeMap<>();
        for (String key : keys) {
            String value = keyValues.get(key);
            if (!TextUtils.isEmpty(value)) { // 如果不为空
                try {
                    key = URLEncoder.encode(key, "utf-8");
                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                key = key.toLowerCase();
                value = value.toLowerCase();
                sortedMap.put(key, value);

            }
        }
        return sortedMap;
    }

    public static String map2String(Map<String, String> keyValues) {

        Uri.Builder builder = new Uri.Builder();
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build().getQuery();
    }
}
