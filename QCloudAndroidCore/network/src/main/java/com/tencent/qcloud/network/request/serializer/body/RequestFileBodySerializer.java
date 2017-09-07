package com.tencent.qcloud.network.request.serializer.body;


import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.bodys.BodyUploadProgressListener;
import com.tencent.qcloud.network.request.bodys.FileRequestBody;

import java.io.File;
import java.security.InvalidParameterException;


import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestFileBodySerializer implements RequestBodySerializer {

    private String path;

    private long offset = -1;
    private long length = -1L;

    private String mimeType;
    private QCloudProgressListener progressListener;

    public RequestFileBodySerializer(String path, String mimeType) {
        this(path, mimeType, -1, -1L);
    }

    public RequestFileBodySerializer(String path) {

        this(path, null);
    }

    public RequestFileBodySerializer(String path, String mimeType, long offset, long length){
        this.path = path;
        this.mimeType = mimeType;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public RequestBody serialize(){
        File file = new File(path);

        if (!file.exists()) {
            throw new InvalidParameterException("upload file does not exist");
        }

        if (TextUtils.isEmpty(mimeType)) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        FileRequestBody fileRequestBody = new FileRequestBody(file, mimeType, offset, length);
        fileRequestBody.setProgressListener(new BodyUploadProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                if (progressListener!=null) {
                    progressListener.onProgress(progress, max);
                }
            }
        });

        return fileRequestBody;
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
