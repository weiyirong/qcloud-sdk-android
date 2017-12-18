package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;


import java.util.Map;

/**
 * <p>
 * 获取某个 Bucket 下的某个 Object 的访问权限ACL。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#getObjectACL(GetObjectACLRequest)
 * @see com.tencent.cos.xml.CosXml#getObjectACLAsync(GetObjectACLRequest, CosXmlResultListener)
 */
final public class GetObjectACLRequest extends CosXmlRequest{

    private String cosPath;
    public GetObjectACLRequest(String bucket, String cosPath){
        setBucket(bucket);
        this.cosPath = cosPath;
        contentType = QCloudNetWorkConstants.ContentType.X_WWW_FORM_URLENCODED;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    @Override
    protected void build() throws CosXmlClientException {
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

        responseBodySerializer = new ResponseXmlS3BodySerializer(GetObjectACLResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("acl",null);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new CosXmlClientException("cosPath must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.GET;
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
     * 设置需要查询的Object路径
     *
     * @param cosPath Object路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的Object路径
     *
     * @return Object路径
     */
    public String getCosPath() {
        return cosPath;
    }
}
