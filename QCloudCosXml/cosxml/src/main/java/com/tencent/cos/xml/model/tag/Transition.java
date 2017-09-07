package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Transition")
public class Transition {
    @XStreamAlias("Date")
    public int date;
    @XStreamAlias("Days")
    public int days;
    @XStreamAlias("StorageClass")
    public String storageClass;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Date:").append(date).append("\n")
                .append("Days:").append(days).append("\n")
                .append("StorageClass:").append(storageClass).append("]");
        return stringBuilder.toString();
    }
}
