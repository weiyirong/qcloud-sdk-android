package com.tencent.cos.xml.model.tag;

/**
 * Created by bradyxiao on 2017/11/24.
 */

public class CompleteMultipartUploadResult {
    public String location;
    public String bucket;
    public String key;
    public String eTag;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{CompleteMultipartUploadResult:\n");
        stringBuilder.append("Location:").append(location).append("\n");
        stringBuilder.append("Bucket:").append(bucket).append("\n");
        stringBuilder.append("Key:").append(key).append("\n");
        stringBuilder.append("ETag:").append(eTag).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
