package com.tencent.qcloud.core.network.request.serializer;


import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.body.BodyUploadProgressListener;
import com.tencent.qcloud.core.network.request.body.InputStreamRequestBody;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestStreamBodySerializer implements RequestBodySerializer {

    private InputStream inputStream;

    private String mimeType;

    private long length;

    private QCloudProgressListener progressListener;

    public RequestStreamBodySerializer(InputStream inputStream, long length, String mimeType) {

        this.inputStream = inputStream;
        this.mimeType = mimeType;
        this.length = length;
    }

    public RequestStreamBodySerializer(InputStream inputStream, String mimeType) {
        this.inputStream = inputStream;
        this.mimeType = mimeType;
        this.length = -1;
    }


    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public RequestBody serialize() throws QCloudClientException {

        if (length == -1) {
            try {
                length = inputStream.available();
            } catch (IOException e) {
                throw new QCloudClientException("serialize request body stream error", e);
            }
        }
        InputStreamRequestBody requestBody = new InputStreamRequestBody(inputStream, length, mimeType);
        requestBody.setProgressListener(new BodyUploadProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                if (progressListener != null) {
                    progressListener.onProgress(progress, max);
                }
            }
        });

        return requestBody;
    }
}
