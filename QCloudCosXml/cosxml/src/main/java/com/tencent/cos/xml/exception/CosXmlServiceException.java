package com.tencent.cos.xml.exception;

import com.tencent.cos.xml.model.tag.COSXMLError;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CosXmlServiceException extends com.tencent.qcloud.core.network.exception.QCloudServiceException {

    private String resource;

    private String traceId;

    private String httpMessage;

    public CosXmlServiceException(int httpCode, String httpMessage, COSXMLError cosxmlError) {
        super(cosxmlError != null ? cosxmlError.message : httpMessage);
        setStatusCode(httpCode);
        this.httpMessage = httpMessage;
        if(cosxmlError != null){
            setErrorCode(cosxmlError.code);
            resource = cosxmlError.resource;
            setRequestId(cosxmlError.requestId);
            traceId = cosxmlError.traceId;
        }
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("code =").append(getErrorCode()).append("\n")
                .append("message =").append(getErrorMessage()).append("\n")
                .append("resource =").append(resource).append("\n")
                .append("requestId =").append(getRequestId()).append("\n")
                .append("traceId =").append(traceId).append("\n")
                .append("http code =").append(getStatusCode()).append("\n")
                .append("http message =").append(httpMessage).append("\n");
        return stringBuilder.toString();
    }
}
