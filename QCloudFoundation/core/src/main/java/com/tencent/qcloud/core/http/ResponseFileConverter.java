package com.tencent.qcloud.core.http;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.util.QCloudHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.internal.Util;

/**
 * 解析下载的字节流，并保存为文本
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

class ResponseFileConverter extends ResponseBodyConverter<Void> {

    private String filePath;
    private long start;
    private long end;

    private QCloudProgressListener progressListener;

    public ResponseFileConverter(String filePath, long start, long end) {
        this.filePath = filePath;
        this.start = start;
        this.end = end;
    }

    void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Void convert(HttpResponse response) throws QCloudClientException, QCloudServiceException {
        if (response == null) {
            return null;
        }

        String contentRangeString = response.header(HttpConstants.Header.CONTENT_RANGE);
        long[] contentRange = QCloudHttpUtils.parseContentRange(contentRangeString);
        long hasRead = 0;
        long max;
        if (contentRange != null) {
            //206
            max = contentRange[1] - contentRange[0] + 1;
        } else {
            //200
            max = response.contentLength();
        }

        File downloadFilePath = new File(filePath);
        File parentDir = downloadFilePath.getParentFile();
        if(parentDir != null && !parentDir.exists() && !parentDir.mkdirs()){
            throw new QCloudClientException("local file directory can not create.");
        }

        InputStream inputStream = response.byteStream();
        if (inputStream != null) {
            FileOutputStream fileOutputStream = null;
            RandomAccessFile randomAccessFile = null;
            try {
                if (start < 0) {
                    fileOutputStream = new FileOutputStream(downloadFilePath);
                } else {
                    randomAccessFile = new RandomAccessFile(downloadFilePath, "rws");
                    randomAccessFile.seek(start);
                }

                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    if (fileOutputStream != null) {
                        fileOutputStream.write(buffer, 0, len);
                    } else {
                        randomAccessFile.write(buffer, 0, len);
                    }
                    hasRead += len;
                    if (progressListener != null && max != 0) {
                        progressListener.onProgress(hasRead, max);
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                }
                return null;
            } catch (IOException e) {
                throw new QCloudClientException("write local file error", e);
            } finally {
                Util.closeQuietly(inputStream);
                Util.closeQuietly(fileOutputStream);
                Util.closeQuietly(randomAccessFile);
            }
        } else {
            throw new QCloudClientException("response body is empty");
        }
    }
}
