package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/11/6.
 */
@XStreamAlias("VersioningConfiguration")
public class VersioningConfiguration {
    /**
     *
     */
    @XStreamAlias("Status")
    public String status;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Status:").append(status).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
