package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
@XStreamAlias("Initiator")
public class Initiator {
    @XStreamAlias("UIN")
    public String uin;

    @XStreamAlias("UID")
    public String uid;

    @XStreamAlias("ID")
    public String id;

    @XStreamAlias("DisplayName")
    public String displayName;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("\n");
        if(uin != null){
            stringBuilder.append("UIN:").append(uin).append("\n");
        }
        if(uid != null){
            stringBuilder.append("UID:").append(uid).append("\n");
        }
        if(id != null){
            stringBuilder.append("ID:").append(id).append("\n");
        }
        if(displayName != null){
            stringBuilder.append("DisplayName:").append(displayName).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
