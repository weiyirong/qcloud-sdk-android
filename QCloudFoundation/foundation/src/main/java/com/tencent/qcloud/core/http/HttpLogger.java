package com.tencent.qcloud.core.http;

import com.tencent.qcloud.core.logger.FileLogAdapter;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * <p>
 * </p>
 * Created by wjielai on 2018/9/26.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
class HttpLogger implements HttpLoggingInterceptor.Logger {

    private boolean debuggable;
    private FileLogAdapter fileLogAdapter;
    private List<String> mRequestBufferLogs;

    HttpLogger(boolean debuggable) {
        this.debuggable = debuggable;
        mRequestBufferLogs = new ArrayList<>(10);
    }

    public void setDebug(boolean debuggable) {
        this.debuggable = debuggable;
    }

    @Override
    public void logRequest(String message) {
        if (debuggable) {
            QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, message);
        }
        fileLogAdapter = QCloudLogger.getAdapter(FileLogAdapter.class);
        if (fileLogAdapter != null) {
            mRequestBufferLogs.add(message);
        }
    }

    @Override
    public void logResponse(Response response, String message) {
        if (debuggable) {
            QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, message);
        }
        if (fileLogAdapter != null && response != null && !response.isSuccessful()) {
            flushRequestBufferLogs();
            fileLogAdapter.log(QCloudLogger.INFO, QCloudHttpClient.HTTP_LOG_TAG,
                    message, null);
        } else {
            mRequestBufferLogs.clear();
        }
    }

    @Override
    public void logException(Exception exception, String message) {
        QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, message);
        if (fileLogAdapter != null && exception != null) {
            flushRequestBufferLogs();
            fileLogAdapter.log(QCloudLogger.INFO, QCloudHttpClient.HTTP_LOG_TAG,
                    message, exception);
        } else {
            mRequestBufferLogs.clear();
        }
    }

    private void flushRequestBufferLogs() {
        if (fileLogAdapter != null && mRequestBufferLogs.size() > 0) {
            for (String requestLog : mRequestBufferLogs) {
                fileLogAdapter.log(QCloudLogger.INFO, QCloudHttpClient.HTTP_LOG_TAG,
                        requestLog, null);
            }
            mRequestBufferLogs.clear();
        }
    }
}
