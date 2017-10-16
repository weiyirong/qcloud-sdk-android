package com.tencent.qcloud.core.network.response.serializer;


import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.ContentRange;
import com.tencent.qcloud.core.network.Range;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 解析下载的字节流，并保存在文本的任意位置
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseFilePartSerializer implements ResponseBodySerializer {

    private String downloadPath;

    private QCloudProgressListener progressListener;

    private Class cls;

    private Range range;

    public ResponseFilePartSerializer(String downloadPath, Range range, Class cls) {

        this.downloadPath = downloadPath;
        this.range = range;
        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudClientException {

        if (response == null) {
            return null;
        }
        ResponseBody responseBody = response.body();
        String contentRangeString = response.header(QCloudNetWorkConstants.HttpHeader.CONTENT_RANGE);
        ContentRange remoteContentRange = ContentRange.newContentRange(contentRangeString);

        long hasRead = 0;
        long max = 0;
        if (remoteContentRange != null) {
            max = remoteContentRange.getEnd() - remoteContentRange.getStart() + 1;
            if (range.getStart() != remoteContentRange.getStart()
                    || range.getEnd() != remoteContentRange.getEnd()) {
            }
        }

        File downloadFilePath = new File(downloadPath);
        File parentDir = downloadFilePath.getParentFile();
        if(parentDir != null && !parentDir.exists() && !parentDir.mkdirs()){
            throw new QCloudClientException("local file directory can not create.");
        }

        if (responseBody != null) {
            InputStream inputStream = responseBody.byteStream();
            RandomAccessFile fileOutputStream = null;
            try {
                fileOutputStream = new RandomAccessFile(downloadFilePath, "rws");
                fileOutputStream.seek(range.getStart());
                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    hasRead += len;
                    if (progressListener != null && max != 0) {
                        progressListener.onProgress(hasRead, max);
                    }
                }
                //fileOutputStream.flush();
                return ResponseHelper.noBodyResult(cls, response);

            } catch (FileNotFoundException e) {
                throw new QCloudClientException("local file not found", e);
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
