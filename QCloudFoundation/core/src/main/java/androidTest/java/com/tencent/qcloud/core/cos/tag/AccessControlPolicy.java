package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/15.
 * author bradyxiao
 */
@XStreamAlias("AccessControlPolicy")
public class AccessControlPolicy {

    /**
     * Owner of the identified resource
     */
    @XStreamAlias("Owner")
    public Owner owner;

    /**
     * Information of authorized account and permissions
     */
    @XStreamAlias("AccessControlList")
    public AccessControlList accessControlList;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Owner:").append(owner != null?owner.toString():"null").append("\n")
            .append("AccessControlList:").append(accessControlList != null?accessControlList.toString():"null").append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
