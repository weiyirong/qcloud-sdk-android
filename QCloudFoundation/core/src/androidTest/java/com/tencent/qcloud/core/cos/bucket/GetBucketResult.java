package com.tencent.qcloud.core.cos.bucket;


import com.tencent.qcloud.core.cos.CosXmlResult;
import com.tencent.qcloud.core.cos.tag.ListBucket;
import com.thoughtworks.xstream.annotations.XStreamAlias;

final public class GetBucketResult extends CosXmlResult {

    @XStreamAlias( "ListBucketResult")
    public ListBucket listBucket;

    @Override
    public String printBody() {
        return listBucket != null ? listBucket.toString() : super.printBody();
    }

}
