package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListParts;
import com.thoughtworks.xstream.annotations.XStreamAlias;

final public class ListPartsResult extends CosXmlResult {
    @XStreamAlias("ListPartsResult")
    public ListParts listParts;

    @Override
    public String printBody() {
        return listParts != null ? listParts.toString() : super.printBody();
    }
}
