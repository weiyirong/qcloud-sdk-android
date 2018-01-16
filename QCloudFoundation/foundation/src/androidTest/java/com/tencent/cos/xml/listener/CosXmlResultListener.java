package com.tencent.cos.xml.listener;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

/**
 * Created by bradyxiao on 2017/12/1.
 */

public interface CosXmlResultListener {
    void onSuccess(CosXmlRequest request, CosXmlResult result);
    void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException);

}
