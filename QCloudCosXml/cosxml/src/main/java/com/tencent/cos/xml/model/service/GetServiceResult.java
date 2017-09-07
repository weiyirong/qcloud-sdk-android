package com.tencent.cos.xml.model.service;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListAllMyBuckets;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
public class GetServiceResult extends CosXmlResult {
    /**
     * ListAllMyBuckets <a herf https://www.qcloud.com/document/product/436/8291></a>
     * {@link ListAllMyBuckets}
     */
    @XStreamAlias("ListAllMyBucketsResult")
    public ListAllMyBuckets listAllMyBuckets;

    @Override
    public String printBody() {
        return listAllMyBuckets != null ? listAllMyBuckets.toString() : super.printBody();
    }
}
