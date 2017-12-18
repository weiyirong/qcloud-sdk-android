package com.tencent.qcloud.core.cos.object;


import com.tencent.qcloud.core.cos.CosXmlResult;
import com.tencent.qcloud.core.cos.tag.CompleteMultipartResult;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class CompleteMultiUploadResult extends CosXmlResult {
    /**
     *  <a herf https://www.qcloud.com/document/product/436/7742></a>
     */
    @XStreamAlias("CompleteMultipartUploadResult")
    public CompleteMultipartResult completeMultipartUpload;
    @Override
    public String printBody() {
        return completeMultipartUpload != null ? completeMultipartUpload.toString() : super.printBody();
    }
}
