package com.tencent.qcloud.core.cos.object;

import com.tencent.qcloud.core.cos.CosXmlRequest;
import com.tencent.qcloud.core.cos.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.cos.common.RequestContentType;
import com.tencent.qcloud.core.cos.common.RequestHeader;
import com.tencent.qcloud.core.cos.common.RequestMethod;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.util.Map;

/**
 * <p>
 * 删除一个Object
 * </p>
 *
 */
public class DeleteObjectRequest extends CosXmlRequest<DeleteObjectResult> {
    private String cosPath;
    public DeleteObjectRequest(){
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
    }

    @Override
    public void build() throws QCloudClientException {
        super.build();
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

        responseBodySerializer = new ResponseXmlS3BodySerializer(DeleteObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {

    }

    @Override
    public void checkParameters() throws QCloudClientException {
        if(bucket == null){
            throw new QCloudClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new QCloudClientException("cosPath must not be null");
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
