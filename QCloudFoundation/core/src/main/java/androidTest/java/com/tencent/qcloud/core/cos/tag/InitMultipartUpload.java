package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/24.
 * author bradyxiao
 */
@XStreamAlias("InitiateMultipartUploadResult")
public class InitMultipartUpload {
    @XStreamAlias("Bucket")
    public String bucket;

    @XStreamAlias("Key")
    public String key;

    @XStreamAlias("UploadId")
    public String uploadId;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Bucket:").append(bucket).append("\n")
                .append("Key:").append(key).append("\n")
                .append("UploadId:").append(uploadId).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
