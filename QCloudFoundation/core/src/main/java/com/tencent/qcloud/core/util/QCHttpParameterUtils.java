package com.tencent.qcloud.core.util;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
 * Created by wjielai on 2017/9/22.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class QCHttpParameterUtils {
    // 对非 '/' 字符进行URLEncoder编码
    public static String urlEncodeWithSlash(String fileId) {

        if (fileId != null && fileId.length() > 0 && !fileId.equals("/")) {
            String[] paras = fileId.split("/");
            for (int i = 0; i < paras.length; i++) {
                paras[i] = urlEncodeString(paras[i]);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < paras.length; i++) {
                stringBuilder.append(paras[i]);
                stringBuilder.append("/");
            }
            if (!fileId.endsWith("/")) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            fileId = stringBuilder.toString();
        }

        return fileId;
    }

    public static String urlEncodeString(String source) {
        try {
            return URLEncoder.encode(source, QCStandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String urlDecodeString(String source) {
        try {
            return URLDecoder.decode(source, QCStandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

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
