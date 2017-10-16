package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/9/22.
 * author bradyxiao
 */
@XStreamAlias("Filter")
public class Filter {
    @XStreamAlias("Prefix")
    public String prefix;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Prefix:").append(prefix).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
