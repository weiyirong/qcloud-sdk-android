package com.tencent.qcloud.core.http;

import com.tencent.qcloud.core.common.QCloudProgressListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/29.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

class StreamingRequestBody extends RequestBody implements ProgressBody {

    private File file;
    private byte[] bytes;
    private InputStream stream;

    private long offset = 0;
    private long length = 0L;

    private String contentType;

    private QCloudProgressListener progressListener;

    private CountingSink countingSink;

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    private StreamingRequestBody() {
    }

    @Override
    public long getBytesTransferred() {
        return countingSink != null ? countingSink.getTotalTransferred() : 0;
    }

    static StreamingRequestBody file(File file, String contentType) {
        return file(file, contentType, 0, Long.MAX_VALUE);
    }

    static StreamingRequestBody file(File file, String contentType, long offset, long length) {
        StreamingRequestBody requestBody = new StreamingRequestBody();
        requestBody.file = file;
        requestBody.contentType = contentType;
        requestBody.offset = offset < 0 ? 0 : offset;
        requestBody.length = length < 1 ? Long.MAX_VALUE : length;

        return requestBody;
    }

    static StreamingRequestBody bytes(byte[] bytes, String contentType, long offset, long length) {
        StreamingRequestBody requestBody = new StreamingRequestBody();
        requestBody.bytes = bytes;
        requestBody.contentType = contentType;
        requestBody.offset = offset < 0 ? 0 : offset;
        requestBody.length = length < 1 ? Long.MAX_VALUE : length;

        return requestBody;
    }

    static StreamingRequestBody steam(InputStream inputStream, File tmpFile, String contentType,
                                      long offset, long length) {
        StreamingRequestBody requestBody = new StreamingRequestBody();
        requestBody.stream = inputStream;
        requestBody.contentType = contentType;
        requestBody.file = tmpFile;
        requestBody.offset = offset < 0 ? 0 : offset;
        requestBody.length = length < 1 ? Long.MAX_VALUE : length;

        return requestBody;
    }

    @Override
    public MediaType contentType() {
        if(contentType != null){
            return MediaType.parse(contentType);
        }else {
            return null;
        }
    }

    @Override
    public long contentLength() throws IOException {
        if (stream != null) {
            return Math.min(stream.available() - offset, length);
        } else if (file != null) {
            return Math.min(file.length() - offset, length);
        } else {
            return Math.min(bytes.length - offset, length);
        }
    }

    private InputStream getStream() throws IOException {
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        } else if (stream != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
                return new FileInputStream(file);
            } finally {
                Util.closeQuietly(fos);
                Util.closeQuietly(stream);
                stream = null;
            }
        } else {
            return new FileInputStream(file);
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        InputStream inputStream = null;
        Source source = null;
        try {
            inputStream = getStream();
            if (offset > 0) {
                long skip = inputStream.skip(offset);
            }
            source = Okio.source(inputStream);

            long contentLength = contentLength();
            countingSink = new CountingSink(sink, contentLength, progressListener);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            bufferedSink.write(source, contentLength);
            bufferedSink.flush();
        } finally {
            Util.closeQuietly(inputStream);
            Util.closeQuietly(source);
            Util.closeQuietly(countingSink);
        }

    }
}
