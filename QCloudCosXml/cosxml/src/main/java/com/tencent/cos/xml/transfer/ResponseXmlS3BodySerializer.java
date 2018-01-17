package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.tag.CosError;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpResponse;
import com.tencent.qcloud.core.http.ResponseBodyConverter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Cos xml api下，成功和失败返回的xml文件的根目录节点不一致，导致解析困难，
 * <p>
 * 这里先将response返回的xml文件添加一个相同的根节点
 * </p>
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseXmlS3BodySerializer<T> extends ResponseBodyConverter<T> {

    private CosXmlResult cosXmlResult;

    public ResponseXmlS3BodySerializer(CosXmlResult cosXmlResult) {
       this.cosXmlResult = cosXmlResult;
    }

    @Override
    protected T convert(HttpResponse response) throws QCloudClientException, QCloudServiceException {
        parseCOSXMLError(response);
        cosXmlResult.parseResponseBody(response);
        return (T) cosXmlResult;
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
                throw new CosXmlClientException(e);
            } catch (IOException e) {
                throw new CosXmlClientException(e);
            }
        }
        throw cosXmlServiceException;
    }

}