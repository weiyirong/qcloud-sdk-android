package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("LifecycleConfiguration")
public class LifecycleConfiguration {
    @XStreamImplicit(itemFieldName = "Rule")
    public List<Rule> ruleList;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        if(ruleList != null){
            int size = ruleList.size();
            for(int i = 0; i < size; ++ i){
                stringBuilder.append("Rule:").append(ruleList.get(i).toString()).append("\n");
            }
            stringBuilder.append("Rule:").append(ruleList.get(size -1).toString()).append("\n");
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
