package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Tagging")
public class Tagging {
    @XStreamAlias("TagSet")
    public TagSet tagSet;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("tagSet:").append(tagSet == null ? "null" : tagSet.toString()).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
