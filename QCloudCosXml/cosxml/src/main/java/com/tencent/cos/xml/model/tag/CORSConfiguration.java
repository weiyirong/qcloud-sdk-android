package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("CORSConfiguration")
public class CORSConfiguration {
    @XStreamImplicit(itemFieldName = "CORSRule")
    public List<CORSRule> corsRules;
    @Override
    public String toString(){
        StringBuilder stringBuilder  = new StringBuilder("{\n");
        if(corsRules != null){
            int size = corsRules.size();
            for(int i = 0; i < size -1; ++ i){
                stringBuilder.append("CORSRule:").append(corsRules.get(i).toString()).append("\n");
            }
            stringBuilder.append("CORSRule:").append(corsRules.get(size -1).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
