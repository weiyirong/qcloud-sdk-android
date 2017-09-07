package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Buckets")
public class Buckets {
    /**
     * {@link Bucket}
     */
    @XStreamImplicit(itemFieldName = "Bucket")
    public List<Bucket> bucketList;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        if(bucketList != null){
            int size = bucketList.size();
            for(int i = 0; i < size; ++ i){
                stringBuilder.append("Bucket:").append(bucketList.get(i).toString()).append("\n");
            }
            stringBuilder.append("Bucket:").append(bucketList.get(size -1).toString()).append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
