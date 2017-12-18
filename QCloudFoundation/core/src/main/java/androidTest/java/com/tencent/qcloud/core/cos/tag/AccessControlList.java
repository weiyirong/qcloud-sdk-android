package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/15.
 * author bradyxiao
 */
@XStreamAlias("AccessControlList")
public class AccessControlList {
    /**
     * A single authorization information entry. Each AccessControlList can contain 100 Grant entries
     */
    @XStreamImplicit(itemFieldName ="Grant")
    public List<Grant> grant;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        if(grant != null){
            int size = grant.size();
            for(int i = 0; i < size-1; ++ i){
                stringBuilder.append("Grant:").append(grant.get(i).toString()).append("\n");
            }
            stringBuilder.append("Grant:").append(grant.get(size -1).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
