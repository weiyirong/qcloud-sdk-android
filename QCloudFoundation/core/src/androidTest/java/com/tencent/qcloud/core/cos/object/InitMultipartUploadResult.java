package com.tencent.qcloud.core.cos.object;


import com.tencent.qcloud.core.cos.CosXmlResult;
import com.tencent.qcloud.core.cos.tag.InitMultipartUpload;
import com.thoughtworks.xstream.annotations.XStreamAlias;


public class InitMultipartUploadResult extends CosXmlResult {
    @XStreamAlias("InitiateMultipartUploadResult")
    public InitMultipartUpload initMultipartUpload;

    @Override
    public String printBody() {
       return initMultipartUpload != null ? initMultipartUpload.toString() : super.printBody();
    }
}
