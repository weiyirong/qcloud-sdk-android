package com.tencent.qcloud.network.auth;

import android.net.Uri;

import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.HttpUrl;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class KeyValuesHelper {

    private static Logger logger = LoggerFactory.getLogger(KeyValuesHelper.class);

    public static String source(Map<String, String> keyValues) {

        if (keyValues == null) {
            return null;
        }
        StringBuilder source = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {

            if (!first) {
                source.append("&");
            }
            source.append(entry.getKey()+"="+entry.getValue());
            first = false;
        }
        return source.toString();
    }

    public static String queryStringForKeys(HttpUrl httpUrl, Set<String> keys, Set<String> realKeys) {

        StringBuilder out = new StringBuilder();
        boolean isFirst = true;

        // 1、将所有的key值转化为小写，并进行排序
        List<String> orderKeys = new LinkedList<>();
        for (String key : keys) {
            orderKeys.add(key.toLowerCase());
        }
        Collections.sort(orderKeys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });


        // 2、获得query所有的name，并进行小写映射
        Set<String> queryNames = httpUrl.queryParameterNames();
        if (queryNames == null) {
            return "";
        }
        Map<String, String> maps = new HashMap<>();
        for (String name : queryNames) {

            maps.put(name.toLowerCase(), name);
        }

        // 3、取出需要的参数
        for (String key : orderKeys) {

            List<String> values = httpUrl.queryParameterValues(maps.get(key));
            if (values != null) {

                for (String value : values) {
                    if (!isFirst) {
                        out.append('&');
                    }
                    isFirst = false;
                    realKeys.add(key.toLowerCase());
                    out.append(key.toLowerCase());
                    if (value != null) {
                        out.append('=');
                        out.append(value.toLowerCase());
                    }
                }
            }
        }
        return out.toString();
    }

    public static String headersStringForKeys(Headers headers, Set<String> keys, Set<String> realKeys) {

        StringBuilder out = new StringBuilder();
        boolean isFirst = true;

        // 1、将所有的key值转化为小写，并进行排序
        List<String> orderKeys = new LinkedList<>();
        for (String key : keys) {
            orderKeys.add(key.toLowerCase());
        }
        Collections.sort(orderKeys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        QCloudLogger.debug(logger, "order keys is" + orderKeys.toString());

        // 2、获得headers所有的name，并进行小写映射
        Set<String> headerNames = headers.names();
        if (headerNames == null) {
            return "";
        }
        Map<String, String> maps = new HashMap<>();
        for (String name : headerNames) {

            maps.put(name.toLowerCase(), name);
        }

        // 3、取出需要的参数
        for (String key : orderKeys) {

            List<String> values = headers.values(key);
            if (values != null) {

                for (String value : values) {
                    if (!isFirst) {
                        out.append('&');
                    }
                    isFirst = false;
                    out.append(key.toLowerCase());
                    realKeys.add(key);
                    if (value != null) {

                        out.append('=');
                        out.append(Uri.encode(value));
                    }
                }
            }
        }



        return out.toString();
    }

}
