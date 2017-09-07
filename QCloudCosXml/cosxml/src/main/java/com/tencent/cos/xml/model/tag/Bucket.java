package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Bucket")
public class Bucket {
    /**
     * Name of Bucket
     */
    @XStreamAlias("Name")
    public String name;
    /**
     *
     */
    @XStreamAlias("Location")
    public String location;
    /**
     * Date on which the Bucket was created. It takes an ISO8601 format, for example, 2016-11-09T08:46:32.000Z
     */
    @XStreamAlias("CreateDate")
    public String createDate;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("Name:").append(name).append("\n")
                .append("Location:").append(location).append("\n")
                .append("CreateDate:").append(createDate).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
