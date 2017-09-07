package com.tencent.qcloud.network.request.bodys;


import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 将字节转化为RequestBody
 */
public class ByteArrayRequestBody extends RequestBody {

    private static final Logger logger = LoggerFactory.getLogger(ByteArrayRequestBody.class);

    private final byte[] bytes ;

    private final String contentType;

    private BodyUploadProgressListener progressListener;

    public void setProgressListener(BodyUploadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public ByteArrayRequestBody(byte[] bytes, String contentType) {
        this.bytes = bytes;
        this.contentType = contentType;
    }

    @Override
    public MediaType contentType() {

        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Source source = Okio.source(byteArrayInputStream);
        long all = bytes.length;
        long remain = all;
        int bufferSize = 1024 * 2;// 每次最多读取2k数据
        long maxReadSize;
        int percent = 0;
        try {
            while (remain > 0) {
                maxReadSize = Math.min(bufferSize, remain);
                long readLength = source.read(bufferedSink.buffer(), maxReadSize);
                //QLogger.info(logger, "readLength = {}", readLength);
                if (readLength != -1) {
                    remain -= readLength;
                    int progress = (int) Math.floor(100.0 * (all - remain) / all);
                    if (progress >= percent) {
                        percent++;
                        //QCloudLogger.info(logger, "has write {}%", progress);
                        if (progressListener != null) {
                            progressListener.onProgress(all - remain, all);
                        }
                    }
                }
                bufferedSink.flush();
            }
            QCloudLogger.info(logger, "file finished.");
        } finally {
            if (source != null) {
                source.close();
            }
        }
    }


    @Override
    public long contentLength() throws IOException {
        //QCloudLogger.debug(logger, "file length = "+file.length());
        return bytes.length;
    }

}
