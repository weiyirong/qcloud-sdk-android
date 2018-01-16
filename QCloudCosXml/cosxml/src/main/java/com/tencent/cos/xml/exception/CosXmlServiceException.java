package com.tencent.cos.xml.exception;

import com.tencent.qcloud.core.common.QCloudServiceException;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public class CosXmlServiceException extends QCloudServiceException {

    private String httpMsg;

    public CosXmlServiceException(String httpMsg) {
        super(null);
        this.httpMsg = httpMsg;
    }

    public CosXmlServiceException(String errorMessage, Exception cause) {
        super(errorMessage, cause);
    }

    public String getHttpMessage(){
        return httpMsg;
    }

    @Override
    public String getMessage() {
        return getErrorMessage()
                + " (Service: " + getServiceName()
                + "; Status Code: " + getStatusCode()
                + "; Status Message: " + httpMsg
                + "; Error Code: " + getErrorCode()
                + "; Request ID: " + getRequestId() + ")";
    }
}
