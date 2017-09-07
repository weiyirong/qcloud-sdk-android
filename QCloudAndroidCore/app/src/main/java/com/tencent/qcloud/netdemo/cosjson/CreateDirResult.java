package com.tencent.qcloud.netdemo.cosjson;

import com.alibaba.fastjson.annotation.JSONField;
import com.tencent.qcloud.network.QCloudResult;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CreateDirResult extends QCloudResult {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "request_id")
    private String requestId;

    @JSONField(name = "ctime")
    String ctime;


    public CreateDirResult() {}

    public CreateDirResult(int httpCode, String httpMessage) {
        super(httpCode, httpMessage);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "code = %d, message = %s, request id = %s, ctime = %s", code, message,requestId, ctime);
    }


}
