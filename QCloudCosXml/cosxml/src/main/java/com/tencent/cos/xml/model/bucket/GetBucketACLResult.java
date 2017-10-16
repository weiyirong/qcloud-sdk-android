package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.AccessControlPolicy;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * 获取Bucket的ACL结果
 * </p>
 *
 * @see AccessControlPolicy
 */
final public class GetBucketACLResult extends CosXmlResult {

    @XStreamAlias("AccessControlPolicy")
    public AccessControlPolicy accessControlPolicy;

    @Override
    public String printBody() {
        return accessControlPolicy != null ? accessControlPolicy.toString() : super.printBody();
    }
}
