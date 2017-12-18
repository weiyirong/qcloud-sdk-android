package com.tencent.qcloud.core.cos.bucket;


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
 * 删除Bucket。
 * </p>
 * <p>
 * 删除的Bucket必须是一个空的Bucket，否则会删除失败。
 * </p>
 *
 */
final public class DeleteBucketRequest extends CosXmlRequest<DeleteBucketResult> {

    public DeleteBucketRequest(){
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
    }

    @Override
    protected void build() throws QCloudClientException {

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

        responseBodySerializer = new ResponseXmlS3BodySerializer(DeleteBucketResult.class);

    }

    @Override
    protected void setRequestQueryParams() {

    }

    @Override
    protected void checkParameters() throws QCloudClientException {
        if(bucket == null){
            throw new QCloudClientException("bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.DELETE ;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }
}
