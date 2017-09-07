package com.tencent.qcloud.network.response.serializer.body;


import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.assist.ContentRange;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.tools.QCloudStringTools;

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
    public QCloudResult serialize(Response response) throws QCloudException {

        if (response == null) {
            return null;
        }
        ResponseBody responseBody = response.body();
        String contentRangeString = response.header(QCloudNetWorkConst.HTTP_HEADER_CONTENT_RANGE);
        ContentRange contentRange = QCloudStringTools.contentRange(contentRangeString);

        long hasRead = 0;
        long max = 0;
        if (contentRange != null) {
            max = contentRange.getEnd() - contentRange.getStart() + 1;
        }

        if (responseBody != null) {

            InputStream inputStream = responseBody.byteStream();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(new File(downloadPath));
                byte[] buffer = new byte[2048];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    hasRead += len;
                    if (progressListener != null && max != 0) {
                        progressListener.onProgress(hasRead, max);
                    }
                }
                fileOutputStream.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new QCloudException(QCloudExceptionType.WRITE_READ_LOCAL_FILE_FAILED, "down load local path not exist.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new QCloudException(QCloudExceptionType.WRITE_READ_LOCAL_FILE_FAILED, "write down load file to local failed");
            } finally {
                try {
                    inputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseBody.close();
            }
        }

        return ResponseSerializerHelper.noBodyResult(cls, response);
    }


    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
