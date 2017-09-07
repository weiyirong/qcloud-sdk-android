package com.tencent.qcloud.network.response.serializer.body;


import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.assist.ContentRange;
import com.tencent.qcloud.network.assist.Range;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.QCloudStringTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    private Logger logger = LoggerFactory.getLogger(ResponseFilePartSerializer.class);

    private String downloadPath;

    private Class cls;

    private QCloudProgressListener progressListener;

    private Range range;

    public ResponseFilePartSerializer(String downloadPath, Range range, Class cls) {

        this.downloadPath = downloadPath;
        this.range = range;
        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudException {

        if (response == null) {
            return null;
        }
        ResponseBody responseBody = response.body();
        String contentRangeString = response.header(QCloudNetWorkConst.HTTP_HEADER_CONTENT_RANGE);
        ContentRange remoteContentRange = QCloudStringTools.contentRange(contentRangeString);


        long hasRead = 0;
        long max = 0;
        if (remoteContentRange != null) {
            max = remoteContentRange.getEnd() - remoteContentRange.getStart() + 1;
            if (range.getStart() != remoteContentRange.getStart()
                    || range.getEnd() != remoteContentRange.getEnd()) {
                QCloudLogger.warn(logger, "local content range is {}, remote content range is {}", range, remoteContentRange);
            }
        }

        if (responseBody != null) {

            InputStream inputStream = responseBody.byteStream();
            RandomAccessFile fileOutputStream = null;
            try {
                fileOutputStream = new RandomAccessFile(new File(downloadPath), "rws");
                fileOutputStream.seek(range.getStart());
                byte[] buffer = new byte[2048];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    hasRead += len;
                    if (progressListener != null && max != 0) {
                        progressListener.onProgress(hasRead, max);
                    }
                }
                //fileOutputStream.flush();

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
