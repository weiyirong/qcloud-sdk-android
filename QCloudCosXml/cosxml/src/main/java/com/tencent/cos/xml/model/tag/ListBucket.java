package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias( "ListBucketResult")
public class ListBucket {
    @XStreamAlias("Name")
    public String name;
    @XStreamAlias("Prefix")
    public String prefix;
    @XStreamAlias("NextMarker")
    public String nextMarker;
    @XStreamAlias("Marker")
    public String marker;
    @XStreamAlias("MaxKeys")
    public String maxKeys;
    @XStreamAlias("IsTruncated")
    public boolean isTruncated;
    @XStreamImplicit( itemFieldName = "Contents")
    public List<Contents> contents;
    @XStreamAlias("CommonPrefixes")
    public CommonPrefixes commonPrefixes;
    @XStreamAlias("Delimiter")
    public String delimiter;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Name:").append(name).append("\n")
                .append("Prefix:").append(prefix).append("\n")
                .append("Marker:").append(marker).append("\n")
                .append("MaxKeys:").append(maxKeys).append("\n")
                .append("NextMarker:").append(nextMarker).append("\n")
                .append("Delimiter:").append(delimiter).append("\n")
                .append("IsTruncated:").append(isTruncated).append("\n");
        if(contents != null){
            int size = contents.size();
            for(int i = 0; i < size; ++i){
                stringBuilder .append("Contents:").append(contents.get(i).toString()).append("\n");
            }
        }else{
            stringBuilder .append("Contents:").append("null").append("\n");
        }
        stringBuilder.append("CommonPrefixes:").append(commonPrefixes== null?"null":commonPrefixes.toString()).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
