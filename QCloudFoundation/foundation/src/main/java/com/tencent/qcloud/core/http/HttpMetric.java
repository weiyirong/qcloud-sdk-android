package com.tencent.qcloud.core.http;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * </p>
 * Created by wjielai on 2018/5/30.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class HttpMetric {

    public static final String ATTR_REQUEST_URL = "request_url";
    public static final String ATTR_REQUEST_METHOD = "request_method";
    public static final String ATTR_REQUEST_CONTENT_LENGTH = "request_content_length";
    public static final String ATTR_REQUEST_CONTENT_TYPE = "request_content_type";
    public static final String ATTR_RESPONSE_CONTENT_LENGTH = "response_content_type";
    public static final String ATTR_RESPONSE_CONTENT_TYPE = "response_content_length";
    public static final String ATTR_RESPONSE_STATUS_CODE = "response_status_code";
    public static final String ATTR_HTTP_TOOK_TIME = "http_took_time";
    public static final String ATTR_EXCEPTION = "exception";

    private Map<String, String> mAttributes;

    private String requestMethod;
    private String requestUrl;

    private int statusCode;
    private Throwable exception;

    private long mStartNs;
    private long mTookTime;

    public HttpMetric() {
        mAttributes = new HashMap<>(16);
    }

    public String getAttribute(String attribute) {
        return mAttributes.get(attribute);
    }

    public Map<String, String> getAttributes() {
        return mAttributes;
    }

    public void putAttribute(String attribute, String value) {
        mAttributes.put(attribute, value);
    }

    public void removeAttribute(String attribute) {
        mAttributes.remove(attribute);
    }

    public void setRequestUrl(String url) {
        requestUrl = url;
        mAttributes.put(ATTR_REQUEST_URL, url);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestMethod(String method) {
        requestMethod = method;
        mAttributes.put(ATTR_REQUEST_METHOD, method);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setStatusCode(int responseCode) {
        statusCode = responseCode;
        mAttributes.put(ATTR_RESPONSE_STATUS_CODE, String.valueOf(statusCode));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setRequestPayloadSize(long bytes) {
        mAttributes.put(ATTR_REQUEST_CONTENT_LENGTH, String.valueOf(bytes));
    }

    public void setRequestContentType(String contentType) {
        mAttributes.put(ATTR_REQUEST_CONTENT_TYPE, contentType);
    }

    public void setResponseContentType(String contentType) {
        mAttributes.put(ATTR_RESPONSE_CONTENT_TYPE, contentType);
    }

    public void setResponsePayloadSize(long bytes) {
        mAttributes.put(ATTR_RESPONSE_CONTENT_LENGTH, String.valueOf(bytes));
    }

    public void traceException(Throwable e) {
        exception = e;
        mAttributes.put(ATTR_EXCEPTION, e.toString());
    }

    public Throwable getError() {
        return exception;
    }

    public void start() {
        mStartNs = System.nanoTime();
    }

    public void stop() {
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - mStartNs);
        mAttributes.put(ATTR_HTTP_TOOK_TIME, String.valueOf(tookMs) + "ms");
        mTookTime = tookMs;
    }

    public boolean isStoped() {
        return mAttributes.containsKey(ATTR_HTTP_TOOK_TIME);
    }

    @Override
    public String toString() {
        return "--> " + requestMethod + ' ' + requestUrl + "  (" + mTookTime + "ms)\n" +
                "<-- " + statusCode + " Attributes = " + mAttributes.toString() +
                (exception == null ? "" : "\n<-- Exception: " + Log.getStackTraceString(exception));

    }
}
