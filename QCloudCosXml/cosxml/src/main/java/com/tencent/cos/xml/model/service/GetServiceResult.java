package com.tencent.cos.xml.model.service;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListAllMyBuckets;
import com.thoughtworks.xstream.annotations.XStreamAlias;

final public class GetServiceResult extends CosXmlResult {
    /**
     * ListAllMyBuckets <a href="https://www.qcloud.com/document/product/436/8291"></a>
     * {@link ListAllMyBuckets}
     */
    @XStreamAlias("ListAllMyBucketsResult")
    public ListAllMyBuckets listAllMyBuckets;

    @Override
    public String printBody() {
        return listAllMyBuckets != null ? listAllMyBuckets.toString() : super.printBody();
    }
}
