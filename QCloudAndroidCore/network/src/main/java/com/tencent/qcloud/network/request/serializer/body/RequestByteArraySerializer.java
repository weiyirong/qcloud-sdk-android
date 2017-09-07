package com.tencent.qcloud.network.request.serializer.body;


import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.request.bodys.BodyUploadProgressListener;
import com.tencent.qcloud.network.request.bodys.ByteArrayRequestBody;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestByteArraySerializer implements RequestBodySerializer {

    private byte[] content;

    private String mimeType;

    private QCloudProgressListener progressListener;

    public RequestByteArraySerializer(byte[] content, String mimeType) {

        this.content = content;
        this.mimeType = mimeType;
    }


    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public RequestBody serialize() {

        ByteArrayRequestBody requestBody = new ByteArrayRequestBody(content, mimeType);
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
