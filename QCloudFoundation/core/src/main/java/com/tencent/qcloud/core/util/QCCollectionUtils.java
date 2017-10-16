package com.tencent.qcloud.core.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCCollectionUtils {

    public static String joinSemicolon(Set<String> values) {

        if (values == null) {
            return "";
        }

        // 这里也需要先按字典顺序进行排序
        Set<String> set = new TreeSet<>(values);

        Log.i("TAG", set.toString());
        StringBuilder str  = new StringBuilder();
        for (String value : set) {
            if (!TextUtils.isEmpty(str.toString())) {
                str.append(";");
            }
            str.append(value);
        }

        return str.toString();
    }

    /**
     * 将set中所有的值转化为小写
     *
     * @param set 原始集合
     * @return 小写的集合
     */
    public static Set<String> getLowerCase(Set<String> set) {


        if (set != null && set.size() > 0) {

            Set<String> lowerSet = new HashSet<>();

            for (String key : set) {
                if (key != null) {
                    lowerSet.add(key.toLowerCase());

                }
            }

            return lowerSet;
        }

        return null;
    }


    // 先url encode编码，然后转化为小写，最后进行排序
    public static Map<String, String> letterDownSort(Map<String, String> keyValues, Set<String> keys)  {
        Map<String, String> sortedMap = new TreeMap<>();
        for (String key : keys) {
            String value = keyValues.get(key);
            if (!TextUtils.isEmpty(value)) { // 如果不为空
                key = QCHttpParameterUtils.urlEncodeString(key).toLowerCase();
                value = QCHttpParameterUtils.urlEncodeString(value).toLowerCase();
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
