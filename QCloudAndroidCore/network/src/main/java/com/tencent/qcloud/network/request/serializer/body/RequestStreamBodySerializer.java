package com.tencent.qcloud.network.request.serializer.body;


import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.request.bodys.BodyUploadProgressListener;
import com.tencent.qcloud.network.request.bodys.ByteArrayRequestBody;
import com.tencent.qcloud.network.request.bodys.InputStreamRequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

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
    public RequestBody serialize() {

        if (length == -1) {
            try {
                length = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
                throw new InvalidParameterException("input stream available() failed");
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
