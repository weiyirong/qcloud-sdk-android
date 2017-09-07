package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("TagSet")
public class TagSet {
    @XStreamImplicit( itemFieldName = "Tag")
    public List<Tag> tagList;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        if(tagList != null){
            int size = tagList.size();
            for(int i = 0; i < size; ++ i){
                stringBuilder.append("Bucket:").append(tagList.get(i).toString()).append("\n");
            }
            stringBuilder.append("Bucket:").append(tagList.get(size -1).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
