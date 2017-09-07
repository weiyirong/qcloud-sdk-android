package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.AccessControlPolicy;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 */
public class GetBucketACLResult extends CosXmlResult {
    @XStreamAlias("AccessControlPolicy")
    public AccessControlPolicy accessControlPolicy;

    @Override
    public String printBody() {
        return accessControlPolicy != null ? accessControlPolicy.toString() : super.printBody();
    }
}
