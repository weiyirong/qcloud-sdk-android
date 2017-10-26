package com.tencent.qcloud.core.network.request.serializer;


import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.body.BodyUploadProgressListener;
import com.tencent.qcloud.core.network.request.body.FileRequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.RequestBody;
import okhttp3.internal.Util;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestStreamBodySerializer implements RequestBodySerializer {

    private InputStream inputStream;

    private String mimeType;

    private long length;

    private File tmpFileDir;

    private QCloudProgressListener progressListener;

    public RequestStreamBodySerializer(InputStream inputStream, long length, String mimeType) {
        this.inputStream = inputStream;
        this.mimeType = mimeType;
        this.length = length;
    }

    public void setTmpFileDir(File tmpFileDir) {
        this.tmpFileDir = tmpFileDir;
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public RequestBody serialize() throws QCloudClientException {
        // save to local temp file
        File localFile = new File(tmpFileDir, "inputStream_tmp");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(localFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new QCloudClientException("can not create tmp file for inputStream", e);
        } finally {
            Util.closeQuietly(fos);
            Util.closeQuietly(inputStream);
        }

        FileRequestBody requestBody = new FileRequestBody(localFile, mimeType, -1, length);
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
