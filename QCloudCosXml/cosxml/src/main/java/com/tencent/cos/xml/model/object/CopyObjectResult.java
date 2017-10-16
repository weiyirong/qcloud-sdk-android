package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.CopyObject;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/9/20.
 * author bradyxiao
 */
public class CopyObjectResult extends CosXmlResult {
    @XStreamAlias("CopyObjectResult")
    public CopyObject copyObject;
    @Override
    public String printBody() {
        return copyObject != null ? copyObject.toString() : super.printBody();
    }
}
