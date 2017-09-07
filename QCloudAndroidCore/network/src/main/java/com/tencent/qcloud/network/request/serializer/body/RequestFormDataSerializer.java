package com.tencent.qcloud.network.request.serializer.body;


import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.request.bodys.BodyUploadProgressListener;
import com.tencent.qcloud.network.request.bodys.FileRequestBody;
import com.tencent.qcloud.network.request.serializer.QCloudHttpRequestOrigin;
import com.tencent.qcloud.network.common.QCloudMimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.tencent.qcloud.network.common.QCloudNetWorkConst.HTTP_CONTENT_TYPE_FORM_DATA;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestFormDataSerializer implements RequestBodySerializer {

    private Logger logger = LoggerFactory.getLogger(RequestFormDataSerializer.class);

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
    public RequestBody serialize() {

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MediaType.parse(HTTP_CONTENT_TYPE_FORM_DATA));


        for (Map.Entry<String, String> entry : keyValues.entrySet()) {

            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }


        for (Map.Entry<String, String> entry : uploadFiles.entrySet()) {

            QCloudLogger.debug(logger, "file path is "+entry.getKey());

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
                QCloudLogger.error(logger, "file do not exists");
            }
        }

        return bodyBuilder.build();
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
