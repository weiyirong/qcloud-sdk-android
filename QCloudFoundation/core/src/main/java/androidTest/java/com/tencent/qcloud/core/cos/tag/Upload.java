package com.tencent.qcloud.core.cos.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Upload")
public class Upload {
    @XStreamAlias("Key")
    public String key;

    @XStreamAlias("UploadID")
    public String uploadID;

    @XStreamAlias("UploadId")
    public String uploadId;

    @XStreamAlias("Initiator")
    public Initiator initiator;

    @XStreamAlias("Owner")
    public Owner owner;

    @XStreamAlias("StorageClass")
    public String storageClass;

    @XStreamAlias("Initiated")
    public String initiated;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Key:").append(key).append("\n")
                .append("UploadID:").append(uploadID).append("\n")
                .append("UploadId:").append(uploadId).append("\n")
                .append("Initiator:").append(initiator == null?"null":initiator.toString()).append("\n")
                .append("Owner:").append(owner == null?"null":owner.toString()).append("\n")
                .append("StorageClass:").append(storageClass).append("\n")
                .append("Initiated:").append(initiated).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
