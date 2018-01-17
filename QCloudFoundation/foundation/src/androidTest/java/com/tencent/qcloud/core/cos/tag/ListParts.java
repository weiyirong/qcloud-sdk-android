package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/24.
 * author bradyxiao
 */
@XStreamAlias("ListPartsResult")
public class ListParts {

    @XStreamAlias("Bucket")
    public String bucket;

    @XStreamAlias("Encoding-type")
    public String encodingType;

    @XStreamAlias("Key")
    public String key;

    @XStreamAlias("UploadId")
    public String uploadId;

    @XStreamAlias("Initiator")
    public Initiator initiator;

    @XStreamAlias("Owner")
    public Owner owner;

    @XStreamAlias("StorageClass")
    public String storageClass;

    @XStreamAlias("PartNumberMarker")
    public String partNumberMarker;

    @XStreamAlias("NextPartNumberMarker")
    public String nextPartNumberMarker;

    @XStreamAlias("MaxParts")
    public String maxParts;

    @XStreamAlias("IsTruncated")
    public String isTruncated;

    @XStreamImplicit(itemFieldName = "Part")
    public List<Part> parts;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Bucket:").append(bucket).append("\n")
                .append("Encoding-type:").append(encodingType).append("\n")
                .append("Key:").append(key).append("\n")
                .append("UploadID:").append(uploadId).append("\n")
                .append("Initiator:").append(initiator == null ? "null" : initiator.toString()).append("\n")
                .append("Owner:").append(owner == null ? "null" : owner.toString()).append("\n")
                .append("StorageClass:").append(storageClass).append("\n")
                .append("PartNumberMarker:").append(partNumberMarker).append("\n")
                .append("NextPartNumberMarker:").append(nextPartNumberMarker).append("\n")
                .append("MaxParts:").append(maxParts).append("\n")
                .append("IsTruncated:").append(isTruncated).append("\n");
        if(parts != null){
            int size = parts.size();
            for(int i = 0; i < size -1; ++ i){
                stringBuilder.append("Part").append(parts.get(i).toString()).append("\n");
            }
            stringBuilder.append("Part").append(parts.get(size - 1).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
