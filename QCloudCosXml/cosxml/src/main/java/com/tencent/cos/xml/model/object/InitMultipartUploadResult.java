package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.InitMultipartUpload;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 */
public class InitMultipartUploadResult extends CosXmlResult {
    @XStreamAlias("InitiateMultipartUploadResult")
    public InitMultipartUpload initMultipartUpload;

    @Override
    public String printBody() {
       return initMultipartUpload != null ? initMultipartUpload.toString() : super.printBody();
    }
}
