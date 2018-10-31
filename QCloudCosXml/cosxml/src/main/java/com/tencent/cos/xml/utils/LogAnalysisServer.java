package com.tencent.cos.xml.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import com.tencent.cos.xml.LogActivity;

/**
 * Created by bradyxiao on 2018/10/16.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class LogAnalysisServer {

    private ClipboardManager clipboardManager;
    private Context context;
    private ClipboardManager.OnPrimaryClipChangedListener onPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            if(clipboardManager.hasPrimaryClip()){
                ClipData clipData = clipboardManager.getPrimaryClip();
                String content = clipData.getItemAt(0).getText().toString().trim();
                if(content.contains("COS-SDK-LOG")){
                    jump();
                }
            }
        }
    };
    private static LogAnalysisServer instance;

    private LogAnalysisServer(Context context){
        this.context = context;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener);
    }

    public static LogAnalysisServer getInstance(Context context){
        synchronized (LogAnalysisServer.class){
            if(instance == null){
                instance = new LogAnalysisServer(context);
            }
        }
        return instance;
    }

    private void jump(){
        Intent intent = new Intent();
        intent.setClass(context, LogActivity.class);
        context.startActivity(intent);
    }

    public void onDestory(){
        if(clipboardManager != null){
            clipboardManager.removePrimaryClipChangedListener(onPrimaryClipChangedListener);
        }
    }



}
