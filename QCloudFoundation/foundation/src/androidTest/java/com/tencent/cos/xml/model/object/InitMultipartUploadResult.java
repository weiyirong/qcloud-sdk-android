package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.InitiateMultipartUpload;
import com.tencent.cos.xml.transfer.XmlParser;
import com.tencent.qcloud.core.http.HttpResponse;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

final public class InitMultipartUploadResult extends CosXmlResult {

    public InitiateMultipartUpload initMultipartUpload;

    @Override
    public void parseResponseBody(HttpResponse response) throws CosXmlServiceException, CosXmlClientException {
        super.parseResponseBody(response);
        initMultipartUpload = new InitiateMultipartUpload();
        try {
            XmlParser.parseInitiateMultipartUploadResult(response.byteStream(), initMultipartUpload);
        } catch (XmlPullParserException e) {
           throw new CosXmlClientException(e);
        } catch (IOException e) {
            throw new CosXmlClientException(e);
        }
    }

    @Override
    public String printResult() {
       return initMultipartUpload != null ? initMultipartUpload.toString() : super.printResult();
    }
}
