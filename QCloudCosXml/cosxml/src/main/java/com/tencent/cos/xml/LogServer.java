package com.tencent.cos.xml;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by bradyxiao on 2018/10/22.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class LogServer implements Application.ActivityLifecycleCallbacks{

    private int activeActivityCount = 0;
    private boolean isAppForeground = false;
    private Application application;
    private ClipboardManager clipboardManager;
    private static LogServer instance;

    private LogServer(){}

    public static LogServer instance(){
        synchronized (LogServer.class){
            if(instance == null){
                instance = new LogServer();
            }
        }
        return instance;
    }

    public void init(Context context){
        application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(this);
        clipboardManager = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);

    }

    public void clear(){
        if(application != null){
            application.unregisterActivityLifecycleCallbacks(this);
        }
    }

    private void showLog(Activity activity){
        Intent intent = new Intent();
        intent.setClassName("com.tencent.cos.xml", "com.tencent.cos.xml.LogActivity");
        activity.startActivity(intent);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activeActivityCount ++;
        if(!isAppForeground){
            isAppForeground = true; // background switch foreground
            if(clipboardManager != null && clipboardManager.hasPrimaryClip()){
                ClipData clipData = clipboardManager.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String content = item.getText().toString().trim();
                if("COS-SDK-LOG".equals(content)){
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
                    showLog(activity);
                }
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeActivityCount --;
        if(activeActivityCount < 0)activeActivityCount = 0;
        if(activeActivityCount == 0){
            isAppForeground = false; // foreground switch background
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //是否完全退出
    }
}
