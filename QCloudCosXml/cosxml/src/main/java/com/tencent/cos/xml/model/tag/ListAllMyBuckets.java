package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("ListAllMyBucketsResult")
public class ListAllMyBuckets {
    /**
     *  Provide the information of owner of Bucket
     *  {@link Owner}
     */
    @XStreamAlias( "Owner")
    public Owner owner;

    /**
     * List all the information of returned list of Buckets
     * {@link Buckets}
     */
    @XStreamAlias("Buckets")
    public Buckets buckets;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Owner:").append(owner == null ? "null": owner.toString()).append("\n")
                .append("Buckets:").append(buckets == null ? "null": buckets.toString()).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
