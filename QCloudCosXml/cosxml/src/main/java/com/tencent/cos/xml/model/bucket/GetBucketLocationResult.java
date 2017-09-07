package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 */
public class GetBucketLocationResult extends CosXmlResult {
    @XStreamAlias("LocationConstraint")
    public String region;
    @Override
    public String printBody() {
        return region != null ? region : super.printBody();
    }
}
