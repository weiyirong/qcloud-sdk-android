package com.tencent.qcloud.netdemo.cosjson;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.QCloudResult;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class UpdateFileAttrsResult extends QCloudResult {

    int code;

    String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
