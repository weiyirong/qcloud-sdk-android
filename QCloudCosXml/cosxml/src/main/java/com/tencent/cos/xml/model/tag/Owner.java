package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/15.
 * author bradyxiao
 */
@XStreamAlias("Owner")
public class Owner {
    /**
     * UIN of owner of Bucket. Parent node: ListAllMyBucketsResult.Owner
     */
    @XStreamAlias("uin")
    public String uin;
    @XStreamAlias("UID")
    public String uid;
    @XStreamAlias("ID")
    public String id;
    @XStreamAlias("DisplayName")
    public String disPlayerName;
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
       if(uid != null){
            stringBuilder.append("UID:").append(uid).append("\n");
        }
        if(uin != null){
            stringBuilder.append("uin:").append(uin).append("\n");
        }
        if(id != null){
           stringBuilder.append("ID:").append(id).append("\n");
       }
        if(disPlayerName != null){
            stringBuilder.append("DisplayName:").append(disPlayerName).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
