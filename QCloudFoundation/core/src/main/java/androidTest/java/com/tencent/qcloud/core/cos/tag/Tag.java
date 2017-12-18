package com.tencent.qcloud.core.cos.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Tag")
public class Tag {
    @XStreamAlias("Key")
    public String key;
    @XStreamAlias("Value")
    public String value;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Key:").append(key ).append("\n")
                .append("Value:").append(value ).append("]");
        return stringBuilder.toString();
    }
}
