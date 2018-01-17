package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias( "Expiration")
public class Expiration {
    @XStreamAlias("Date")
    public String date;
    @XStreamAlias("Days")
    public String days;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Date:").append(date).append("\n")
                .append("Days:").append(days).append("]");
        return stringBuilder.toString();
    }
}
