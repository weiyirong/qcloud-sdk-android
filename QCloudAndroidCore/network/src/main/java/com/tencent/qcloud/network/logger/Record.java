package com.tencent.qcloud.network.logger;

import android.util.Log;

import com.tencent.qcloud.network.tools.TimesUtils;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class Record {
    private String tag =null;// log 的TAG
    private String msg = null;//log 的msg
    private Throwable throwable = null;
    private String level = null; //log 的level
    private long timestamp;
    private long threadId;
    private String threadName = null;
    public Record(String tag, String level, String msg, Throwable t) {
        this.level = level;
        this.tag = tag;
        this.msg = msg;
        this.throwable = t;

        this.timestamp = System.currentTimeMillis();
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(level).append("/");
        builder.append(TimesUtils.timeUtils(timestamp,"yyyy-MM-dd HH:mm:ss"));
        builder.append("[").append(threadName).append(" ").append(threadId).append("]");
        builder.append("[").append(tag).append("]");
        builder.append("[").append(msg).append("]");
        if(throwable != null){
            builder.append(" * Exception :\n").append(Log.getStackTraceString(throwable));
        }
        builder.append("\n");
        return  builder.toString();
    }

    public long getLength(){
        return (msg != null ? msg.length() : 0) + 40;
    }

}