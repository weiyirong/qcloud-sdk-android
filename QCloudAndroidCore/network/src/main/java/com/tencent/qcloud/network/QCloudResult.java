package com.tencent.qcloud.network;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudResult {

    private int httpCode;

    private String httpMessage;

    private Map<String, List<String>> headers;

    public QCloudResult() {
        this(-1, "no message");
    }

    public QCloudResult(int httpCode, String httpMessage) {

        this.httpCode = httpCode;
        this.httpMessage = httpMessage;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public void setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "http code = %d, http message = %s \r\n header is %s", httpCode, httpMessage, headers);
    }
}
