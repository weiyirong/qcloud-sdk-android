package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.DeleteResult;
import com.thoughtworks.xstream.annotations.XStreamAlias;


final public class DeleteMultiObjectResult extends CosXmlResult {
    /**
     * <a href="https://www.qcloud.com/document/api/436/8289"></a>
     */
    @XStreamAlias("DeleteResult")
    public DeleteResult deleteResult;
    @Override
    public String printBody() {
        return deleteResult != null ? deleteResult.toString() : super.printBody();
    }
}
