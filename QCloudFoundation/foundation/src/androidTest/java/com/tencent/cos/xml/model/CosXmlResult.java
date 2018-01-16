package com.tencent.cos.xml.model;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.qcloud.core.http.HttpResponse;

import java.util.List;
import java.util.Map;


/**
 * Created by bradyxiao on 2017/11/30.
 */

public abstract class CosXmlResult {

    public int httpCode;
    public String httpMessage;
    public Map<String, List<String>> headers;
    public String accessUrl;

    public  void parseResponseBody(HttpResponse response) throws CosXmlClientException, CosXmlServiceException {
        httpCode = response.code();
        httpMessage = response.message();
        headers = response.headers();
    }

    public String printResult(){
        return httpCode + "|" + httpMessage;
    }
}
