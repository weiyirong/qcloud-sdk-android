package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.InitMultipartUpload;
import com.thoughtworks.xstream.annotations.XStreamAlias;


final public class InitMultipartUploadResult extends CosXmlResult {
    @XStreamAlias("InitiateMultipartUploadResult")
    public InitMultipartUpload initMultipartUpload;

    @Override
    public String printBody() {
       return initMultipartUpload != null ? initMultipartUpload.toString() : super.printBody();
    }
}
