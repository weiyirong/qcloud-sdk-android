package com.tencent.qcloud.mtaUtils;

import android.content.Context;
import android.util.Log;

import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by bradyxiao on 2018/9/26.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class MTAServer {
    private static final String TAG = "MTAServer";
    private String mtaAppKey = "A3D1DC4YFR8E";
    private Context applicationContext;
    private ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<>();
    private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public String fmtTime(long secondMillis){
        DateFormat dateFormat = dateFormatThreadLocal.get();
        if(dateFormat == null){
            dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
            dateFormatThreadLocal.set(dateFormat);
        }
        return dateFormat.format(new Date(secondMillis));
    }

    public MTAServer(Context applicationContext){
        this.applicationContext = applicationContext;
        StatConfig.setAppKey(this.applicationContext, mtaAppKey);
        StatService.setContext(this.applicationContext);
        Log.d(TAG, "MTAServer instance success");
    }

    /** key - exception message*/
    public void reportCosXmlClientException(String key, String exceptionMessage){
        Properties properties = new Properties();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(key).append("-").append(exceptionMessage);
        String value = stringBuffer.toString();
        properties.setProperty("ClientException", value);
        StatService.trackCustomKVEvent(applicationContext, "cos_xml_android_error", properties);
        Log.d(TAG, "report client exception : " + value);
    }

    /** time- exception message*/
    public void reportCosXmlClientException(String exceptionMessage){
        Properties properties = new Properties();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(fmtTime(System.currentTimeMillis())).append("-").append(exceptionMessage);
        String value = stringBuffer.toString();
        properties.setProperty("ClientException", value);
        StatService.trackCustomKVEvent(applicationContext, "cos_xml_android_error", properties);
        Log.d(TAG, "report client exception : " + value);
    }

    /** key - requestId*/
    public void reportCosXmlServerException(String key, String requestId){
        Properties properties = new Properties();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(key).append("-").append(requestId);
        String value = stringBuffer.toString();
        properties.setProperty("ServerException", value);
        StatService.trackCustomKVEvent(applicationContext, "cos_xml_android_error", properties);
        Log.d(TAG, "report server exception : " + value);
    }

    /** key - requestId*/
    public void reportCosXmlServerException(String requestId){
        Properties properties = new Properties();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(fmtTime(System.currentTimeMillis())).append("-").append(requestId);
        String value = stringBuffer.toString();
        properties.setProperty("ServerException", value);
        StatService.trackCustomKVEvent(applicationContext, "cos_xml_android_error", properties);
        Log.d(TAG, "report server exception : " + value);
    }

}
