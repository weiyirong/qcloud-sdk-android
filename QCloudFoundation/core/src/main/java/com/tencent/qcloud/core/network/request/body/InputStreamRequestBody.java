package com.tencent.qcloud.core.network.request.body;


import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 将流转化为RequestBody
 */
public class InputStreamRequestBody extends RequestBody {

    private final InputStream inputStream;

    private final String contentType;

    private final long length;

    private BodyUploadProgressListener progressListener;

    public void setProgressListener(BodyUploadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public InputStreamRequestBody(InputStream inputStream, long length, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.length = length;
    }

    @Override
    public MediaType contentType() {

        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(inputStream);
            long all = length;
            long remain = all;
            int bufferSize = 1024 * 2;// 每次最多读取2k数据
            long maxReadSize;
            int percent = 0;

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
        } finally {
            if (source != null) {
                source.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    @Override
    public long contentLength() throws IOException {
        return length;
    }

}
