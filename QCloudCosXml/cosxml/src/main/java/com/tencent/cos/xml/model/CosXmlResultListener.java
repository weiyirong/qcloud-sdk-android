package com.tencent.cos.xml.model;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;

/**
 */
public interface CosXmlResultListener {

    /**
     *
     * @param request {@link CosXmlRequest}
     * @param result {@link CosXmlResult}
     */
    void onSuccess(CosXmlRequest request, CosXmlResult result);

    /**
     *
     *
     * @param request {@link CosXmlRequest}
     * @param exception {@link CosXmlClientException}
     */
    void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException);
}
