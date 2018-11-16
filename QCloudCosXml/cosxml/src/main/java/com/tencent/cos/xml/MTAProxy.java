package com.tencent.cos.xml;

import android.content.Context;

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
    private Method reportFailedEvent, reportSendEvent;
    private Context applicationContext;
    private static MTAProxy instance;

    private MTAProxy(Context applicationContext){
        this.applicationContext = applicationContext;
        try {
            Class cls = Class.forName(className);
            Constructor constructor = cls.getConstructor(android.content.Context.class, java.lang.String.class);
            if(constructor != null){
                mtaServer = constructor.newInstance(this.applicationContext, BuildConfig.VERSION_NAME);
            }
            reportFailedEvent = cls.getDeclaredMethod("reportFailedEvent", java.lang.String.class, java.lang.String.class);
            if(reportFailedEvent != null){
                reportFailedEvent.setAccessible(true);
            }
            reportSendEvent = cls.getDeclaredMethod("reportSendEvent", java.lang.String.class);
            if(reportSendEvent != null){
                reportSendEvent.setAccessible(true);
            }
        } catch (ClassNotFoundException e) {
            QCloudLogger.d(TAG, className + " : not found");
        } catch (NoSuchMethodException e) {
            QCloudLogger.d(TAG, e.getMessage() + " : not found");
        } catch (InstantiationException e) {
            QCloudLogger.d(TAG, e.getMessage() + " : not found");
        } catch (IllegalAccessException e) {
            QCloudLogger.d(TAG, e.getMessage() + " : not found");
        } catch (InvocationTargetException e) {
            QCloudLogger.d(TAG, e.getMessage() + " : not found");
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

    /**
     * key - exception message
     * key: class name
     * exceptionMessage: exception
     **/
    public void reportCosXmlClientException(String requestClassName, String exceptionMessage){
        if(mtaServer != null && reportFailedEvent != null){
            try {
                reportFailedEvent.invoke(mtaServer, requestClassName, exceptionMessage);
            } catch (IllegalAccessException e) {
                QCloudLogger.d(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.d(TAG, e.getMessage());
            }
        }
    }

    /**
     * key - exception message
     * key: class name
     * errorMsg: error code and error msg
     **/
    public void reportCosXmlServerException(String requestClassName, String errorMsg){
        if(mtaServer != null && reportFailedEvent != null){
            try {
                reportFailedEvent.invoke(mtaServer, requestClassName, errorMsg);
            } catch (IllegalAccessException e) {
                QCloudLogger.d(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.d(TAG, e.getMessage());
            }
        }
    }

    /**
     * report send action
     * @param requestClassName class name
     */
    public void reportSendAction(String requestClassName){
        if(mtaServer != null && reportSendEvent != null){
            try {
                reportSendEvent.invoke(mtaServer, requestClassName);
            } catch (IllegalAccessException e) {
                QCloudLogger.e(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                QCloudLogger.e(TAG, e.getMessage());
            }
        }
    }

}
