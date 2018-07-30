package com.tencent.cos.xml.transfer;


import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.http.HttpConstants;
import com.tencent.qcloud.core.http.ProgressBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;


/**
 * Created by bradyxiao on 2018/6/12.
 */

public class MultipartRequestBody extends RequestBody implements ProgressBody {

    private File file;
    private byte[] bytes;
    private long offset = -1L, contentLength = -1L;
    private Map<String, String> parameters = new LinkedHashMap<>();
    private String name = "file";
    private String fileName;
    private String contentType;
    private QCloudProgressListener progressListener;
    private MultipartBody multipartBody;

    public void  build(){
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MediaType.parse(HttpConstants.ContentType.MULTIPART_FORM_DATA));
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        builder.addFormDataPart(name, fileName, new InnerRequestBody());
        multipartBody = builder.build();
    }

    public void setParameters(Map<String, String> parameters){
        if(parameters != null){
            this.parameters.putAll(parameters);
        }
    }

    public void setContent(String contentType, String name, String fileName, byte[] bytes, long offset, long counts){
        this.contentType = contentType;
        if(name != null){
            this.name = name;
        }
        this.fileName = fileName;
        this.bytes = bytes;
        setOffset(offset, counts);
    }

    public void setContent(String contentType, String name, String fileName, File file, long offset, long counts){
        this.contentType = contentType;
        if(name != null){
            this.name = name;
        }
        this.fileName = fileName;
        this.file = file;
        setOffset(offset, counts);
    }

    public void setContent(String contentType, String name, String fileName, File tmpFile, InputStream stream, long offset, long counts) throws IOException {
        this.contentType = contentType;
        if(name != null){
            this.name = name;
        }
        this.fileName = fileName;
        setOffset(offset, counts);
        saveStreamToTmpFile(stream, tmpFile);
        this.file = tmpFile;
        this.offset = -1;
        this.contentLength = -1;
    }

    private void setOffset(long offset, long counts){
        this.offset = offset;
        this.contentLength = counts;
    }

    @Override
    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public long getBytesTransferred() {
        return 0;
    }

    private void saveStreamToTmpFile(InputStream stream, File tmpFile) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tmpFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            long bytesLimit = contentLength < 0 ? Long.MAX_VALUE : contentLength;
            long bytesTotal = 0;
            if (offset > 0) {
                long skip = stream.skip(offset);
                if(skip != offset){ //出现问题，如offset > available length
                    return;
                }
            }
            while (bytesTotal < bytesLimit &&
                    (bytesRead = stream.read(buffer, 0, (int) Math.min(buffer.length, bytesLimit - bytesTotal))) != -1) {
                fos.write(buffer, 0, bytesRead);
                bytesTotal += bytesRead;
            }
            fos.flush();
        } finally {
            Util.closeQuietly(fos);
        }
    }

    private long getContentRawLength() throws IOException {
        long rawLength = 0L;
        if(file != null){
            rawLength = file.length();
        }else if(bytes != null){
            rawLength = bytes.length;
        }
        if(offset < 0){
            offset = 0;
        }
        if(contentLength < 0){
            contentLength = rawLength;
        }
        return rawLength > offset + contentLength ? contentLength : rawLength - offset;
    }

    @Override
    public MediaType contentType() {
        return multipartBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return multipartBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        multipartBody.writeTo(sink);
    }

    private class InnerRequestBody extends RequestBody{

        @Override
        public MediaType contentType() {
           if (contentType != null) {
                return MediaType.parse(contentType);
            } else {
                return null;
            }
        }

        @Override
        public long contentLength() throws IOException {
            return getContentRawLength();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            InputStream stream = null;
            try {
                if(file != null){
                    stream = new FileInputStream(file);
                }else if(bytes != null){
                    stream = new ByteArrayInputStream(bytes);
                }
                if(offset > 0){
                    long realSkip = stream.skip(offset);
                    if(realSkip != offset)return;
                }
                if(stream != null){
                    long length = contentLength();
                    byte[] buffer = new byte[1024 * 2];
                    int count;
                    long hasWriteLength = 0;
                    while ((count = stream.read(buffer, 0, buffer.length)) != -1){
                        sink.write(buffer, 0, count);
                        hasWriteLength += count;
                        progressListener.onProgress(hasWriteLength, length);
                    }
                }
            }finally {
                Util.closeQuietly(stream);
            }
        }
    }

}
