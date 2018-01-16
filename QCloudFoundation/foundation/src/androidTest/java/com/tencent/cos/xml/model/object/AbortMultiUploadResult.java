package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.qcloud.core.http.HttpResponse;

final public class AbortMultiUploadResult extends CosXmlResult {

    @Override
    public void parseResponseBody(HttpResponse response)  throws CosXmlClientException, CosXmlServiceException {
        super.parseResponseBody(response);
    }
}
