package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.thoughtworks.xstream.annotations.XStreamAlias;

final public class GetBucketLocationResult extends CosXmlResult {
    @XStreamAlias("LocationConstraint")
    public String region;
    @Override
    public String printBody() {
        return region != null ? region : super.printBody();
    }
}
