package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/15.
 * author bradyxiao
 */
@XStreamAlias("Grant")
public class Grant {
    //Resource information of authorized account.
    @XStreamAlias("Permission")
    public String permission;

    //Permission information, enumerated values: READ, WRITE, FULL_CONTROL
    @XStreamAlias("Grantee")
    public Grantee grantee;


    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Grantee:").append(grantee != null?grantee.toString():"null").append("\n")
                .append("Permission:").append(permission).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
