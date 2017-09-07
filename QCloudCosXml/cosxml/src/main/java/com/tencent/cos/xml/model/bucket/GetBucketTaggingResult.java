package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.Tagging;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 */
public class GetBucketTaggingResult extends CosXmlResult {
    @XStreamAlias("Tagging")
    public Tagging tagging;
    @Override
    public String printBody() {
        return tagging != null ? tagging.toString() : super.printBody();
    }
}
