package com.tencent.cos.xml.common;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public class VersionInfo {

    public static final String version = "1.3.0";
    public static final String platform = "cos-android-xml-sdk";

    public static String getUserAgent(){
        return platform + "-" + version;
    }
}
