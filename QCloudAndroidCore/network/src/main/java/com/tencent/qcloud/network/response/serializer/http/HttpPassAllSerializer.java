package com.tencent.qcloud.network.response.serializer.http;

import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class HttpPassAllSerializer implements ResponseSerializer{

    @Override
    public boolean serialize(Response response) {

        if (response == null) {
            return false;
        }

        return true;
    }
}
