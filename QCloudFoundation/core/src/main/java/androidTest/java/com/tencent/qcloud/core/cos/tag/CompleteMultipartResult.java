package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
@XStreamAlias("CompleteMultipartUploadResult")
public class CompleteMultipartResult {
    /**
     * External network access domain of the created Object
     */
    @XStreamAlias("Location")
    public String location;

    /**
     * Target Bucket of the multipart upload operation
     */
    @XStreamAlias("Bucket")
    public String bucket;

    /**
     * Object name
     */
    @XStreamAlias( "Key")
    public String key;

    /**
     * MD5 algorithm check value for the merged file
     */
    @XStreamAlias("ETag")
    public String eTag;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Location =").append(location).append("\n")
                .append("Bucket =").append(bucket).append("\n")
                .append("Key =").append(key).append("\n")
                .append("ETag =").append(eTag).append("]");
        return stringBuilder.toString();
    }
}
