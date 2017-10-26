package com.tencent.qcloud.core.network.response.serializer;


import com.tencent.qcloud.core.network.ContentRange;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 解析下载的字节流，并保存为文本
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseFileSerializer implements ResponseBodySerializer {

    private String downloadPath;

    private Class cls;

    private QCloudProgressListener progressListener;

    public ResponseFileSerializer(String downloadPath, Class cls) {

        this.downloadPath = downloadPath;
        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudClientException {

        if (response == null) {
            return null;
        }
        ResponseBody responseBody = response.body();
        String contentRangeString = response.header(QCloudNetWorkConstants.HttpHeader.CONTENT_RANGE);
        ContentRange contentRange = ContentRange.newContentRange(contentRangeString);
        long hasRead = 0;
        long max = 0;
        if (contentRange != null) {
            max = contentRange.getEnd() - contentRange.getStart() + 1;
        }

        File downloadFilePath = new File(downloadPath);
        File parentDir = downloadFilePath.getParentFile();
        if(parentDir != null && !parentDir.exists() && !parentDir.mkdirs()){
            throw new QCloudClientException("local file directory can not create.");
        }

        if (responseBody != null) {

            InputStream inputStream = responseBody.byteStream();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(new File(downloadPath));
                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    hasRead += len;
                    if (progressListener != null && max != 0) {
                        progressListener.onProgress(hasRead, max);
                    }
                }
                fileOutputStream.flush();

                return ResponseHelper.noBodyResult(cls, response);
            } catch (IOException e) {
                throw new QCloudClientException("write local file error", e);
            } finally {
                try {
                    inputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    throw new QCloudClientException("close input stream error", e);
                }
            }
        } else {
            throw new QCloudClientException("response body is empty");
        }
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
