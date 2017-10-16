package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;


import java.util.Map;

/**
 * 获取对应 Object 的 meta 信息数据
 *
 * @see com.tencent.cos.xml.CosXml#headObject(HeadObjectRequest)
 * @see com.tencent.cos.xml.CosXml#headObjectAsync(HeadObjectRequest, CosXmlResultListener)
 */
final public class HeadObjectRequest extends CosXmlRequest {
    private String cosPath;
    public HeadObjectRequest(String bucket, String cosPath){
        this.bucket = bucket;
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

        responseBodySerializer = new ResponseXmlS3BodySerializer(HeadObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {

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
        requestMethod = QCloudNetWorkConstants.RequestMethod.HEAD;
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
     * 设置Object的COS路径
     *
     * @param cosPath COS 路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的COS路径
     *
     * @return COS 路径
     */
    public String getCosPath() {
        return cosPath;
    }

    /**
     * <p>
     * 设置If-Modified-Since头部
     * </p>
     * <p>
     * 当 Object 在指定时间后被修改，则返回对应 Object 的 meta 信息，否则返回 304。
     * </p>
     *
     * @param ifModifiedSince If-Modified-Since头部
     */
    public void setIfModifiedSince(String ifModifiedSince){
        if(ifModifiedSince != null){
            requestHeaders.put("If-Modified-Since",ifModifiedSince);
        }
    }
}
