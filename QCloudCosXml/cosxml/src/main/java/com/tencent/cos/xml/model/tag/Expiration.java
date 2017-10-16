package com.tencent.cos.xml.model.tag;


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
    @XStreamAlias("ExpiredObjectDeleteMarker")
    public String expiredObjectDeleteMarker;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Date:").append(date).append("\n")
                .append("Days:").append(days).append("\n")
                .append("ExpiredObjectDeleteMarker:").append(expiredObjectDeleteMarker).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
