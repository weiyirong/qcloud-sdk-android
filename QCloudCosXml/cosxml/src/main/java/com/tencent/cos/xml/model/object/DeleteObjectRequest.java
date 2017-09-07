package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 * Delete Object request is used for deletion of one file (Object).
 */
public class DeleteObjectRequest extends CosXmlRequest {
    private String cosPath;
    public DeleteObjectRequest(){
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
    }

    @Override
    public void build() {
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Map.Entry<String,String> entry : requestQueryParams.entrySet())
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
        }

        if(requestHeaders.size() > 0){
            for(Map.Entry<String,String> entry : requestHeaders.entrySet())
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
        }

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(DeleteObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {

    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
        if(cosPath == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "cosPath must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.DELETE;
    }

    @Override
    protected void setRequestPath() {
        if(cosPath != null){
            if(!cosPath.startsWith("/")){
                requestPath = "/" + cosPath;
            }else{
                requestPath = cosPath;
            }
        }
    }

    /**
     *  set cosPath for object.
     * @param cosPath
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }
}
