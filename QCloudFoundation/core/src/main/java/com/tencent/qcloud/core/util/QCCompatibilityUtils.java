package com.tencent.qcloud.core.util;

/**
 * Created by wjielai on 2017/8/25.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class QCCompatibilityUtils {

    static boolean hasFastJsonClasspath() {
        try {
            Class.forName("com.alibaba.fastjson.JSON");
            return true;
        } catch (ClassNotFoundException e) { }
        return false;
    }

    static boolean hasGsonClasspath() {
        try {
            Class.forName("com.google.gson.Gson");
            return true;
        } catch (ClassNotFoundException e) { }
        return false;
    }

    static boolean hasXstreamClasspath() {
        try {
            Class.forName("com.thoughtworks.xstream.XStream");
            return true;
        } catch (ClassNotFoundException e) { }
        return false;
    }
}
