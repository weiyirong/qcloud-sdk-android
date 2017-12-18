package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Contents")
public class Contents {
    @XStreamAlias("Key")
    public String key;
    @XStreamAlias("LastModified")
    public String lastModified;
    @XStreamAlias("ETag")
    public String eTag;
    @XStreamAlias("Size")
    public String size;
    @XStreamAlias("Owner")
    public Owner owner;
    @XStreamAlias("StorageClass")
    public String storageClass;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Key:").append(key).append("\n")
                .append("LastModified:").append(lastModified).append("\n")
                .append("ETag:").append(eTag).append("\n")
                .append("Size:").append(size).append("\n")
                .append("Owner:").append(owner == null?"null":owner.toString()).append("\n")
                .append("StorageClass:").append(storageClass).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
