package com.tencent.qcloud.netdemo.cosjson;

import com.tencent.qcloud.network.QCloudResult;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class DeleteDirResult extends QCloudResult {


    private int code;

    private String message;

    public DeleteDirResult() {}

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
