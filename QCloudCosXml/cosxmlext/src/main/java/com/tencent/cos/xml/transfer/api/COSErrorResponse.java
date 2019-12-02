package com.tencent.cos.xml.transfer.api;

import android.text.TextUtils;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;

/**
 * Created by rickenwang on 2019-11-21.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class COSErrorResponse<T> extends COSResponse<T> {

    private QCloudClientException clientException;

    private QCloudServiceException serviceException;

    public COSErrorResponse(CosXmlClientException clientException, CosXmlServiceException serviceException) {

        this.clientException = clientException;
        this.serviceException = serviceException;
    }

    public QCloudClientException getClientException() {
        return clientException;
    }

    public QCloudServiceException getServiceException() {
        return serviceException;
    }

    public String getErrorMessage() {

        String errorMessage = "";

        if (serviceException != null) {
            errorMessage = serviceException.getErrorCode(); // serviceException 不一定会有 errorCode
            if (TextUtils.isEmpty(errorMessage) && serviceException instanceof CosXmlServiceException) {
                errorMessage = ((CosXmlServiceException) serviceException).getHttpMessage();
            }

        } else if (clientException != null){
            errorMessage = clientException.getMessage();
        }
        return errorMessage;
    }
}
