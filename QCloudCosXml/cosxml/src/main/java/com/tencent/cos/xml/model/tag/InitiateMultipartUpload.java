package com.tencent.cos.xml.model.tag;

/**
 * Created by bradyxiao on 2017/11/24.
 */

public class InitiateMultipartUpload {
    public String bucket;
    public String key;
    public String uploadId;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{InitiateMultipartUpload:\n");
        stringBuilder.append("Bucket:").append(bucket).append("\n");
        stringBuilder.append("Key:").append(key).append("\n");
        stringBuilder.append("UploadId:").append(uploadId).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
