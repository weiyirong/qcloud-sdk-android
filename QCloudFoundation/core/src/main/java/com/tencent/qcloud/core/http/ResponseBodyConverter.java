package com.tencent.qcloud.core.http;


import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;

import java.io.IOException;

/**
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class ResponseBodyConverter<T> {

    /**
     *
     * @param response  需要解析的ResponseBody
     *
     * @throws QCloudClientException
     */
    protected abstract T convert(HttpResponse response) throws QCloudClientException, QCloudServiceException;

    private static final class StringConverter extends ResponseBodyConverter<String> {
        @Override
        protected String convert(HttpResponse response) throws QCloudClientException, QCloudServiceException {
            try {
                return response.string();
            } catch (IOException e) {
                throw new QCloudClientException(e);
            }
        }
    }

    public static ResponseBodyConverter<Void> file(String filePath) {
        return file(filePath, -1, -1);
    }

    public static ResponseBodyConverter<Void> file(String filePath, long start, long end) {
        return new ResponseFileConverter(filePath, start, end);
    }

    public static ResponseBodyConverter<String> string() {
        return new StringConverter();
    }
}
