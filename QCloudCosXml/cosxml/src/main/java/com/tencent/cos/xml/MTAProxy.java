package com.tencent.cos.xml;

import android.content.Context;
import android.util.Log;

import com.tencent.qcloud.core.logger.QCloudLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by bradyxiao on 2018/9/26.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class MTAProxy {
    private static final String TAG = "MTAProxy";
    private final String className = "com.tencent.qcloud.mtaUtils.MTAServer";
    private Object mtaServer;
    private Method reportCosXmlClientException, reportCosXmlClientExceptionWithKey;
    private Method reportCosXmlServerException, reportCosXmlServerExceptionWithKey;
    private Context applicationContext;
    private static MTAProxy instance;

    private MTAProxy(Context applicationContext){
        this.applicationContext = applicationContext;
        try {
            Class cls = Class.forName(className);
            Constructor constructor = cls.getConstructor(android.content.Context.class);
            if(constructor != null){
                mtaServer = constructor.newInstance(this.applicationContext);
            }
            reportCosXmlClientExceptionWithKey = cls.getDeclaredMethod("reportCosXmlClientException", java.lang.String.class, java.lang.String.class);
            if(reportCosXmlClientExceptionWithKey != null){
                reportCosXmlClientExceptionWithKey.setAccessible(true);
            }
            reportCosXmlClientException = cls.getDeclaredMethod("reportCosXmlClientException", java.lang.String.class);
            if(reportCosXmlClientException != null){
                reportCosXmlClientException.setAccessible(true);
            }
            reportCosXmlServerExceptionWithKey = cls.getDeclaredMethod("reportCosXmlServerException", java.lang.String.class, java.lang.String.class);
            if(reportCosXmlServerExceptionWithKey != null){
                reportCosXmlServerExceptionWithKey.setAccessible(true);
            }
            reportCosXmlServerException = cls.getDeclaredMethod("reportCosXmlServerException", java.lang.String.class);
            if(reportCosXmlServerException != null){
                reportCosXmlServerException.setAccessible(true);
            }
        } catch (ClassNotFoundException e) {
            QCloudLogger.e(TAG, className + " : not found");
        } catch (NoSuchMethodException e) {
            QCloudLogger.e(TAG, e.getMessage() + " : not found");
        } catch (InstantiationException e) {
            QCloudLogger.e(TAG, e.getMessage() + " : not found");
        } catch (IllegalAccessException e) {
            QCloudLogger.e(TAG, e.getMessage() + " : not found");
        } catch (InvocationTargetException e) {
            QCloudLogger.e(TAG, e.getMessage() + " : not found");
        }
    }

    public static void init(Context applicationContext){
        synchronized (MTAProxy.class){
            if(instance == null){
                instance = new MTAProxy(applicationContext);
            }
        }
    }

    public static MTAProxy getInstance(){
        return instance;
    }

    /** key - exception message*/
    public void reportCosXmlClientException(String key, String exceptionMessage){
        if(mtaServer != null && reportCosXmlClientExceptionWithKey != null){
            try {
                reportCosXmlClientExceptionWithKey.invoke(mtaServer, key, exceptionMessage);
            } catch (IllegalAccessException e) {
                QCloudLogger.e(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.e(TAG, e.getMessage());
            }
        }
    }

    /** time- exception message*/
    public void reportCosXmlClientException(String exceptionMessage){
        if(mtaServer != null && reportCosXmlClientException != null){
            try {
                reportCosXmlClientException.invoke(mtaServer, exceptionMessage);
            } catch (IllegalAccessException e) {
                QCloudLogger.e(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.e(TAG, e.getMessage());
            }
        }
    }

    /** key - requestId*/
    public void reportCosXmlServerException(String key, String requestId){
        if(mtaServer != null && reportCosXmlServerExceptionWithKey != null){
            try {
                reportCosXmlServerExceptionWithKey.invoke(mtaServer, key, requestId);
            } catch (IllegalAccessException e) {
                QCloudLogger.e(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.e(TAG, e.getMessage());
            }
        }
    }

    /** key - requestId*/
    public void reportCosXmlServerException(String requestId){
        if(mtaServer != null && reportCosXmlServerException != null){
            try {
                reportCosXmlServerException.invoke(mtaServer,requestId);
            } catch (IllegalAccessException e) {
                QCloudLogger.e(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.e(TAG, e.getMessage());
            }
        }
    }


}
