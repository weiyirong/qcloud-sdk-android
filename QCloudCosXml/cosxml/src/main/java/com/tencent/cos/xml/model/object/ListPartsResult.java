package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ListParts;
import com.tencent.cos.xml.transfer.XmlSlimParser;
import com.tencent.qcloud.core.http.HttpResponse;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

final public class ListPartsResult extends CosXmlResult {

    public ListParts listParts;

    @Override
    public void parseResponseBody(HttpResponse response) throws CosXmlServiceException, CosXmlClientException {
        super.parseResponseBody(response);
        listParts = new ListParts();
        try {
            XmlSlimParser.parseListPartsResult(response.byteStream(), listParts);
        } catch (XmlPullParserException e) {
           throw new CosXmlClientException(e);
        } catch (IOException e) {
            throw new CosXmlClientException(e);
        }
    }

    @Override
    public String printResult() {
        return listParts != null ? listParts.toString() : super.printResult();
    }
}
