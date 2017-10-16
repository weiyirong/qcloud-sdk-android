package com.tencent.qcloud.core.network.request.serializer;


import com.tencent.qcloud.core.network.QCloudMimeType;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.body.BodyUploadProgressListener;
import com.tencent.qcloud.core.network.request.body.FileRequestBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.tencent.qcloud.core.network.QCloudNetWorkConstants.ContentType.MULTIPART_FORM_DATA;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestFormDataSerializer implements RequestBodySerializer {

    private Map<String, String> keyValues;

    private Map<String, String> uploadFiles;
    private QCloudProgressListener progressListener;

    public RequestFormDataSerializer() {

        keyValues = new HashMap<>();
        uploadFiles = new HashMap<>();
    }

    public void bodyKeyValue(String key, String value) {
        keyValues.put(key, value);
    }

    public void uploadFile(String filePath, String uploadName) {
        uploadFiles.put(filePath, uploadName);
    }

    @Override
    public RequestBody serialize() throws QCloudClientException {

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MediaType.parse(MULTIPART_FORM_DATA));


        for (Map.Entry<String, String> entry : keyValues.entrySet()) {

            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }


        for (Map.Entry<String, String> entry : uploadFiles.entrySet()) {

            String path = entry.getKey();
            String type = entry.getValue();
            File file = new File(path);
            if (file.exists()) {
                FileRequestBody fileRequestBody = new FileRequestBody(file, QCloudMimeType.getTypeByFileName(file.getName()));
                bodyBuilder.addFormDataPart(type, file.getName(), fileRequestBody);
                fileRequestBody.setProgressListener(new BodyUploadProgressListener() {
                    @Override
                    public void onProgress(long progress, long max) {
                        if (progressListener!=null) {
                            progressListener.onProgress(progress, max);
                        }
                    }
                });
            } else {
                throw new QCloudClientException("file do not exists");
            }
        }

        return bodyBuilder.build();
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
