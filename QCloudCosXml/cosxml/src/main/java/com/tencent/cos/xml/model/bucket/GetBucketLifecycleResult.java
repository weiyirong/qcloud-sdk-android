package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

final public class GetBucketLifecycleResult extends CosXmlResult {
    @XStreamAlias("LifecycleConfiguration")
    public LifecycleConfiguration lifecycleConfiguration;

    @Override
    public String printBody() {
        return lifecycleConfiguration != null ? lifecycleConfiguration.toString() : super.printBody();
    }
}
