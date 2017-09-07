package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 */
public class GetBucketLifecycleResult extends CosXmlResult {
    @XStreamAlias("LifecycleConfiguration")
    public LifecycleConfiguration lifecycleConfiguration;

    @Override
    public String printBody() {
        return lifecycleConfiguration != null ? lifecycleConfiguration.toString() : super.printBody();
    }
}
