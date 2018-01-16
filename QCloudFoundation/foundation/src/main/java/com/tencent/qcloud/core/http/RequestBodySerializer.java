package com.tencent.qcloud.core.http;


import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.ByteString;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class RequestBodySerializer {

    /**
     * 将请求体转换成 ResponseBody
     *
     * @return ResponseBody
     */
    abstract RequestBody body();

    private static final class BaseRequestBodyWrapper extends RequestBodySerializer {

        private final RequestBody body;

        public BaseRequestBodyWrapper(RequestBody body) {
            this.body = body;
        }

        @Override
        RequestBody body() {
            return body;
        }
    }

    /**
     * Returns a new request body that transmits {@code content}. If {@code contentType} is non-null
     * and lacks a charset, this will use UTF-8.
     */
    public static RequestBodySerializer string(String contentType, String content) {
        return new BaseRequestBodyWrapper(RequestBody.create(MediaType.parse(contentType), content));
    }

    /** Returns a new request body that transmits {@code content}. */
    public static RequestBodySerializer string(String contentType, ByteString content) {
        return new BaseRequestBodyWrapper(RequestBody.create(MediaType.parse(contentType), content));
    }

    /** Returns a new request body that transmits {@code content}. */
    public static RequestBodySerializer bytes(final String contentType, final byte[] content) {
        return bytes(contentType, content,  0L, -1);
    }

    /** Returns a new request body that transmits {@code content}. */
    public static RequestBodySerializer bytes(final String contentType, final byte[] content,
                                               final long offset, final long byteCount) {
        StreamingRequestBody requestBody = StreamingRequestBody.bytes(content, contentType, offset, byteCount);
        return new BaseRequestBodyWrapper(requestBody);
    }

    public static RequestBodySerializer file(String contentType, File file) {
        return file(contentType, file,  0L, -1);
    }

    public static RequestBodySerializer file(String contentType, File file, long offset, long length) {
        if (TextUtils.isEmpty(contentType)) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
            contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        StreamingRequestBody fileRequestBody = StreamingRequestBody.file(file, contentType, offset, length);

        return new BaseRequestBodyWrapper(fileRequestBody);
    }

    public static RequestBodySerializer stream(String contentType, Context context, InputStream inputStream) {
        return stream(contentType, context, inputStream, 0L, -1);
    }

    public static RequestBodySerializer stream(String contentType, Context context, InputStream inputStream,
                                               long offset, long length) {
        File tmpFile = new File(context.getExternalCacheDir(), "inputStream_tmp");
        return stream(contentType, tmpFile, inputStream, offset, length);
    }

    public static RequestBodySerializer stream(String contentType, File tmpFile, InputStream inputStream) {
        return stream(contentType, tmpFile, inputStream, 0L, -1);
    }

    public static RequestBodySerializer stream(String contentType, File tmpFile, InputStream inputStream,
                                               long offset, long length) {
        StreamingRequestBody requestBody = StreamingRequestBody.steam(inputStream, tmpFile, contentType,
                offset, length);

        return new BaseRequestBodyWrapper(requestBody);
    }

    public static RequestBodySerializer multipart(Map<String, String> keyValues, Map<String, String> uploadFiles) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MediaType.parse(HttpConstants.ContentType.MULTIPART_FORM_DATA));

        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : uploadFiles.entrySet()) {
            String path = entry.getKey();
            String type = entry.getValue();
            File file = new File(path);
            StreamingRequestBody fileRequestBody = StreamingRequestBody.file(file,
                    MimeType.getTypeByFileName(file.getName()));
            bodyBuilder.addFormDataPart(type, file.getName(), fileRequestBody);
        }

        return new BaseRequestBodyWrapper(bodyBuilder.build());
    }
}
