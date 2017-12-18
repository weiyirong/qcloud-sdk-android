package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.RequestXmlBodySerializer;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.VersioningConfiguration;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;

import java.util.Map;

/**
 * Created by bradyxiao on 2017/11/6.
 * <p>
 *     Put Bucket Versioning 接口实现启用或者暂停存储桶的版本控制功能。
 * </p>
 * <H1>一旦开启，无法关闭，是不可逆操作</H1>
 *
 *  @see com.tencent.cos.xml.CosXml#putBucketVersioning(PutBucketVersioningRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketVersionAsync(PutBucketVersioningRequest, CosXmlResultListener)
 */

public class PutBucketVersioningRequest extends CosXmlRequest {

    private VersioningConfiguration versioningConfiguration;

    public PutBucketVersioningRequest(String bucket){
        setBucket(bucket);
        contentType = QCloudNetWorkConstants.ContentType.X_WWW_FORM_URLENCODED;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        versioningConfiguration = new VersioningConfiguration();
    }

    @Override
    protected void build() throws CosXmlClientException{
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Object object : requestQueryParams.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
            }
        }

        if(requestHeaders.size() > 0){
            for(Object object : requestHeaders.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
            }
        }

        requestOriginBuilder.body(new RequestXmlBodySerializer(versioningConfiguration));

        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketVersioningResult.class);
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("versioning", null);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }

    /** 版本是否开启，true:开启，false:不开启*/
    public void setEnableVersion(boolean isEnable){
        if(isEnable){
            versioningConfiguration.status = "Enabled";
        }else {
            versioningConfiguration.status = "Suspended";
        }
    }
}
