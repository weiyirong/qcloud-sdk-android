package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.DeleteResult;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/10.
 * author bradyxiao
 */
public class DeleteMultiObjectResult extends CosXmlResult {
    /**
     * <a herf https://www.qcloud.com/document/api/436/8289></a>
     */
    @XStreamAlias("DeleteResult")
    public DeleteResult deleteResult;
    @Override
    public String printBody() {
        return deleteResult != null ? deleteResult.toString() : super.printBody();
    }
}
