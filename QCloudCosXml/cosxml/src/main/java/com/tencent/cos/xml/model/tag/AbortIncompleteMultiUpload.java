package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("AbortIncompleteMultipartUpload")
public class AbortIncompleteMultiUpload {
    @XStreamAlias("DaysAfterInitiation")
    public int daysAfterInitiation;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("DaysAfterInititation:").append(daysAfterInitiation).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
