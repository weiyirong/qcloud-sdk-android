package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("AbortIncompleteMultipartUpload")
public class AbortIncompleteMultiUpload {
    @XStreamAlias("DaysAfterInititation")
    public String daysAfterInitiation;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[DaysAfterInititation:").append(daysAfterInitiation).append("]");
        return stringBuilder.toString();
    }
}
