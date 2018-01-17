package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("ListMultipartUploadsResult")
public class ListMultipartUploads {

    @XStreamAlias("Bucket")
    public String bucket;

    @XStreamAlias("Encoding-Type")
    public String encodingType;

    @XStreamAlias("KeyMarker")
    public String keyMarker;

    @XStreamAlias("UploadIdMarker")
    public String uploadIdMarker;

    @XStreamAlias("NextKeyMarker")
    public String nextKeyMarker;

    @XStreamAlias("NextUploadIdMarker")
    public String nextUploadIdMarker;

    @XStreamAlias("MaxUploads")
    public String maxUploads;

    @XStreamAlias( "IsTruncated")
    public boolean isTruncated;

    @XStreamAlias("Prefix")
    public String prefix;

    @XStreamAlias("Delimiter")
    public String delimiter;

    @XStreamImplicit( itemFieldName = "Upload")
    public List<Upload> uploads;

    @XStreamAlias("CommonPrefixs")
    public CommonPrefixes commonPrefixes;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Bucket:").append(bucket).append("\n")
                .append("Encoding-type:").append(encodingType).append("\n")
                .append("KeyMarker:").append(keyMarker).append("\n")
                .append("UploadIdMarker:").append(uploadIdMarker).append("\n")
                .append("NextKeyMarker:").append(nextKeyMarker).append("\n")
                .append("NextUploadIdMarker:").append(nextUploadIdMarker).append("\n")
                .append("MaxUploads:").append(maxUploads).append("\n")
                .append("IsTruncated:").append(isTruncated).append("\n")
                .append("Prefix:").append(prefix).append("\n")
                .append("CommonPrefixes:").append(commonPrefixes != null?commonPrefixes.toString():"null").append("\n")
                .append("delimiter:").append(delimiter).append("\n");
        if(uploads != null){
            int size = uploads.size();
            for(int i = 0; i < size -1; ++ i){
                stringBuilder.append("Upload").append(uploads.get(i).toString()).append("\n");
            }
            stringBuilder.append("Upload").append(uploads.get(size - 1).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
