package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 */
public class GetBucketResult extends CosXmlResult {
    @XStreamAlias( "ListBucketResult")
    public ListBucket listBucket;

    @Override
    public String printBody() {
        return listBucket != null ? listBucket.toString() : super.printBody();
    }
}
