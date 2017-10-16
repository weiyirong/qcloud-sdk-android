package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/9/22.
 * author bradyxiao
 */
@XStreamAlias("NoncurrentVersionExpiration")
public class NoncurrentVersionExpiration {
    @XStreamAlias("NoncurrentDays")
    public String noncurrentDays;
    @XStreamAlias("StorageClass")
    public String storageClass;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("NoncurrentDays:").append(noncurrentDays).append("\n");
        stringBuilder.append("StorageClass:").append(storageClass).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
