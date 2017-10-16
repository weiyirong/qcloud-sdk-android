package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/9/20.
 * author bradyxiao
 *
 * Return the information of result of copying
 */
@XStreamAlias("CopyObjectResult")
public class CopyObject {
    @XStreamAlias("ETag")
    public String eTag;
    /** Return the last modification time of the file, GMT  */
    @XStreamAlias("LastModified")
    public String lastModified;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("ETag:").append(eTag).append("\n")
                .append("LastModified:").append(lastModified).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
