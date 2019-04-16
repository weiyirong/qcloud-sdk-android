package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.MTAProxy;
import com.tencent.cos.xml.common.ClientErrorCode;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.GetObjectBytesResult;
import com.tencent.cos.xml.model.tag.CosError;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpResponse;
import com.tencent.qcloud.core.http.ResponseBodyConverter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * 解析下载的字节流，并保存为文本
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class ResponseBytesConverter<T> extends ResponseBodyConverter<T> {

    private GetObjectBytesResult getObjectBytesResult;

    public ResponseBytesConverter(GetObjectBytesResult getObjectBytesResult) {

        this.getObjectBytesResult = getObjectBytesResult;
    }

    @Override
    public T convert(HttpResponse<T> response) throws QCloudClientException, QCloudServiceException {
        parseCOSXMLError(response);
        getObjectBytesResult.parseResponseBody(response);
        return (T) getObjectBytesResult;
    }

    private void parseCOSXMLError(HttpResponse response) throws CosXmlServiceException, CosXmlClientException {
        int httpCode = response.code();
        if(httpCode >= 200 && httpCode < 300)return;
        String message = response.message();
        CosXmlServiceException cosXmlServiceException = new CosXmlServiceException(message);
        cosXmlServiceException.setStatusCode(httpCode);
        cosXmlServiceException.setRequestId(response.header("x-cos-request-id"));
        InputStream inputStream = response.byteStream();
        if(inputStream != null){
            CosError cosError = new CosError();
            try {
                XmlSlimParser.parseError(inputStream, cosError);
                cosXmlServiceException.setErrorCode(cosError.code);
                cosXmlServiceException.setErrorMessage(cosError.message);
                cosXmlServiceException.setRequestId(cosError.requestId);
                cosXmlServiceException.setServiceName(cosError.resource);
            } catch (XmlPullParserException e) {
                String reportMessage = String.format(Locale.ENGLISH, "%d %s", ClientErrorCode.SERVERERROR.getCode(), e.getCause() == null ?
                        e.getClass().getSimpleName() : e.getCause().getClass().getSimpleName());
                MTAProxy.getInstance().reportCosXmlClientException(ResponseBytesConverter.class.getSimpleName(), reportMessage);
                throw new CosXmlClientException(ClientErrorCode.SERVERERROR.getCode(), e);
            } catch (IOException e) {
                String reportMessage = String.format(Locale.ENGLISH, "%d %s", ClientErrorCode.IO_ERROR.getCode(), e.getCause() == null ?
                        e.getClass().getSimpleName() : e.getCause().getClass().getSimpleName());
                MTAProxy.getInstance().reportCosXmlClientException(ResponseBytesConverter.class.getSimpleName(), reportMessage);
                throw new CosXmlClientException(ClientErrorCode.IO_ERROR.getCode(), e);
            }
        }
        MTAProxy.getInstance().reportCosXmlServerException(ResponseXmlS3BodySerializer.class.getSimpleName(),
                String.format(Locale.ENGLISH, "%s %s",cosXmlServiceException.getStatusCode(), cosXmlServiceException.getErrorCode()));
        throw cosXmlServiceException;
    }
}
