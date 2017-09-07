package com.tencent.cos.xml.model.service;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 * Get Service API is used to obtain the list of all Buckets under the current account.
 * This API requires Authorization signature for verification and can only obtain the Bucket list
 * under the account to which the AccessID in signature belongs.
 *
 */
public class GetServiceRequest extends CosXmlRequest {
    @Override
    protected void setRequestQueryParams() {
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
    }

    @Override
    public void checkParameters() throws QCloudException{

    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.GET;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    @Override
    public void build() {
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.host("service.cos.myqcloud.com");

        setRequestQueryParams();

        /**
         *
         * Service URL中不包含query params
         *
         * delete by rickenwang
         */
//        if(requestQueryParams.size() > 0){
//            for(Map.Entry<String,String> entry : requestQueryParams.entrySet())
//                requestOriginBuilder.query(entry.getKey(),entry.getValue());
//        }

        if(requestHeaders.size() > 0){
            for(Map.Entry<String,String> entry : requestHeaders.entrySet())
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
        }

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(GetServiceResult.class);
    }

}
