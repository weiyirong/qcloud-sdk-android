package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListMultipartUploads;
import com.thoughtworks.xstream.annotations.XStreamAlias;

final public class ListMultiUploadsResult extends CosXmlResult {

    @XStreamAlias("ListMultipartUploadsResult")
    public ListMultipartUploads listMultipartUploads;

    @Override
    public String printBody() {
        if(listMultipartUploads != null){
            return listMultipartUploads.toString();
        }else{
            return super.printBody();
        }
    }
}
