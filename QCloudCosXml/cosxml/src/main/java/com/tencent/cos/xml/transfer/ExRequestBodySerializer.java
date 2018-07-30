package com.tencent.cos.xml.transfer;


import com.tencent.qcloud.core.http.RequestBodySerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by bradyxiao on 2018/6/14.
 */

public class ExRequestBodySerializer extends RequestBodySerializer {

    RequestBody body;
    public ExRequestBodySerializer(RequestBody body){
        this.body = body;
    }

    @Override
    public RequestBody body() {
        return body;
    }

    public static ExRequestBodySerializer multipart(Map<String, String> parameters, File file, long offset, long counts){
        MultipartRequestBody multipartRequestBody = new MultipartRequestBody();
        multipartRequestBody.setParameters(parameters);
        multipartRequestBody.setContent(null,"file", file.getName(), file, offset, counts);
        multipartRequestBody.build();
        return new ExRequestBodySerializer(multipartRequestBody);
    }

    public static ExRequestBodySerializer multipart(Map<String, String> parameters, String fileName, byte[] content, long offset, long counts){
        MultipartRequestBody multipartRequestBody = new MultipartRequestBody();
        multipartRequestBody.setParameters(parameters);
        multipartRequestBody.setContent(null, "file", fileName, content, offset, counts);
        multipartRequestBody.build();
        return new ExRequestBodySerializer(multipartRequestBody);
    }

    public static ExRequestBodySerializer multipart(Map<String, String> parameters, File tmpFile, InputStream inputStream, long offset, long counts) throws IOException {
        MultipartRequestBody multipartRequestBody = new MultipartRequestBody();
        multipartRequestBody.setParameters(parameters);
        multipartRequestBody.setContent(null,"file", tmpFile.getName(), tmpFile, inputStream, offset, counts);
        multipartRequestBody.build();
        return new ExRequestBodySerializer(multipartRequestBody);
    }
}
