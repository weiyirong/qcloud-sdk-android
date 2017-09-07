package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListParts;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 */
public class ListPartsResult extends CosXmlResult {
    @XStreamAlias("ListPartsResult")
    public ListParts listParts;

    @Override
    public String printBody() {
        return listParts != null ? listParts.toString() : super.printBody();
    }
}
