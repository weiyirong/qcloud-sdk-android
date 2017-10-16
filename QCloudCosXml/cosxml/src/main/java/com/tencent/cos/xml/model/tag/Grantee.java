package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by bradyxiao on 2017/5/15.
 * author bradyxiao
 */
@XStreamAlias("Grantee")
public class Grantee {

    @XStreamAlias("ID")
    public String id;

    @XStreamAlias("uin")
    public String uin;

    @XStreamAlias("Subaccount")
    public String subaccount;

    @XStreamAlias("DisplayName")
    public String disPlayName;

    @XStreamAsAttribute
    @XStreamAlias("xmlns:xsi")
    public String xsi;

    @XStreamAsAttribute
    @XStreamAlias("xsi:type")
    public String type;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("xmlns:xsi:").append(xsi).append("\n")
                .append("xsi:type:").append(type).append("\n")
                .append("ID:").append(id).append("\n")
                .append("Subaccount:").append(subaccount).append("\n")
                .append("DisplayName:").append(disPlayName).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
