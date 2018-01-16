package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
@XStreamAlias("Part")
public class Part {
    @XStreamAlias("PartNumber")
    public int partNumber;

    @XStreamAlias("LastModified")
    public String lastModified;

    @XStreamAlias("ETag")
    public String eTag;

    @XStreamAlias("Size")
    public String size;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PartNumber:").append(partNumber).append("\n")
                .append("LastModified:").append(lastModified).append("\n")
                .append("Etag:").append(eTag).append("\n")
                .append("Size:").append(size).append("]");
        return stringBuilder.toString();
    }
}
