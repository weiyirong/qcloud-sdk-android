package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.CompleteMultipartUploadResult;
import com.tencent.cos.xml.transfer.XmlSlimParser;
import com.tencent.qcloud.core.http.HttpResponse;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

final public class CompleteMultiUploadResult extends CosXmlResult {
    /**
     *  <a href="https://www.qcloud.com/document/product/436/7742"></a>
     */
    public CompleteMultipartUploadResult completeMultipartUpload;

    @Override
    public void parseResponseBody(HttpResponse response) throws CosXmlServiceException, CosXmlClientException {
        super.parseResponseBody(response);
        try {
            completeMultipartUpload = new CompleteMultipartUploadResult();
            XmlSlimParser.parseCompleteMultipartUploadResult(response.byteStream(), completeMultipartUpload);
        } catch (XmlPullParserException e) {
            throw new CosXmlClientException(e);
        } catch (IOException e) {
            throw new CosXmlClientException(e);
        }
    }

    @Override
    public String printResult() {
        return completeMultipartUpload != null ? completeMultipartUpload.toString() : super.printResult();
    }
}
