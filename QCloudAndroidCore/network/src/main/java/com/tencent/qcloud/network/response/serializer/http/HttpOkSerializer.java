package com.tencent.qcloud.network.response.serializer.http;

import com.tencent.qcloud.network.QCloudResult;

import okhttp3.Response;

/**
 * 只有当Http返回码为200时会进一步解析
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class HttpOkSerializer implements ResponseSerializer {


    @Override
    public boolean serialize(Response response) {

        if (response == null) {
            return false;
        }

        if (response.code() == 200) {
            return true;
        }

        return false;
    }
}
