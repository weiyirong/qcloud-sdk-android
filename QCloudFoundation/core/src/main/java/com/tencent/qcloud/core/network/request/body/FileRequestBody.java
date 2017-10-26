package com.tencent.qcloud.core.network.request.body;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 将文件转化为RequestBody
 */
public class FileRequestBody extends RequestBody {

    private final File file ;

    private long offset = 0;
    private long length = 0L;

    private final String contentType;

    private BodyUploadProgressListener progressListener;

    public void setProgressListener(BodyUploadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public FileRequestBody(File file, String contentType) {
        this(file, contentType, -1, -1L);

    }

    public FileRequestBody(File file, String contentType, long offset, long length) {
        this.file = file;
        this.contentType = contentType;
        if(offset < 0){
            offset = 0;
        }
        this.offset = offset;
        long fileLen = file.length();
        if(length < 0 || length > fileLen){
            length = fileLen;
        }
        this.length = length;
    }

    @Override
    public MediaType contentType() {

        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        InputStream inputStream = null;
        Source source = null;
        try {
            inputStream = new FileInputStream(file);
            inputStream.skip(offset);
            source = Okio.source(inputStream);
            //long all = file.length();
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
                } else {
                    break;
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
