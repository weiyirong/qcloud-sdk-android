package com.tencent.qcloud.core.http;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.util.QCloudHttpUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;

/**
 * 解析下载的字节流，并保存为文本
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class ResponseFileConverter<T> extends ResponseBodyConverter<T> implements ProgressBody {

    private String filePath;
    private long offset;

    private QCloudProgressListener progressListener;

    private CountingSink countingSink;

    public ResponseFileConverter(String filePath, long offset) {
        this.filePath = filePath;
        this.offset = offset;
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public T convert(HttpResponse<T> response) throws QCloudClientException, QCloudServiceException {
        HttpResponse.checkResponseSuccessful(response);

        String contentRangeString = response.header(HttpConstants.Header.CONTENT_RANGE);
        long[] contentRange = QCloudHttpUtils.parseContentRange(contentRangeString);
        long contentLength;
        if (contentRange != null) {
            //206
            contentLength = contentRange[1] - contentRange[0] + 1;
        } else {
            //200
            contentLength = response.contentLength();
        }

        File downloadFilePath = new File(filePath);
        File parentDir = downloadFilePath.getParentFile();
        if(parentDir != null && !parentDir.exists() && !parentDir.mkdirs()){
            throw new QCloudClientException("local file directory can not create.");
        }

        BufferedSink sink = null;
        try {
            if (offset <= 0) {
                countingSink = new CountingSink(Okio.sink(downloadFilePath), contentLength,
                        progressListener);
                sink = Okio.buffer(countingSink);
                sink.write(response.response.body().source(), contentLength);
                sink.flush();
            } else {
                writeRandomAccessFile(downloadFilePath, response.byteStream(), contentLength);
            }
            return null;
        } catch (IOException e) {
            throw new QCloudClientException("write local file error for " + e.toString(), e);
        } finally {
            Util.closeQuietly(sink);
        }
    }

    private void writeRandomAccessFile(File downloadFilePath, InputStream inputStream, long contentLength)
            throws IOException, QCloudClientException {
        if (inputStream == null) {
            throw new QCloudClientException("response body stream is null");
        }
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(downloadFilePath, "rws");
            randomAccessFile.seek(offset);
            byte[] buffer = new byte[8192];
            countingSink = new CountingSink(new Buffer(), contentLength, progressListener);
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, len);
                countingSink.writeBytesInternal(len);
            }
        } finally {
            Util.closeQuietly(inputStream);
            Util.closeQuietly(randomAccessFile);
        }
    }

    @Override
    public long getBytesTransferred() {
        return countingSink != null ? countingSink.getTotalTransferred() : 0;
    }
}
