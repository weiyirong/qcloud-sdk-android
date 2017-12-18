package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.bucket.PutBucketCORSRequest;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;


import java.util.Map;

/**
 * <p>
 * Object 跨域访问配置的预请求。
 * </p>
 * <p>
 * 即在发送跨域请求之前会发送一个 OPTIONS 请求并带上特定的来源域，HTTP 方法和 HEADER 信息等给 COS，以决定是否可以发送真正的跨域请求。
 * 当 CORS 配置不存在时，请求返回 403 Forbidden。
 * </p>
 * <p>
 * 可以通过 PutBucketCORS 接口来开启 Bucket 的 CORS 支持。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putBucketCORS(PutBucketCORSRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketCORSAsync(PutBucketCORSRequest, CosXmlResultListener)
 */
final public class OptionObjectRequest extends CosXmlRequest {
    private String origin;
    private String accessControlMethod;
    private String accessControlHeaders;
    private String cosPath;
    public OptionObjectRequest(String bucket, String cosPath, String origin, String accessControlMethod){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.origin = origin;
        this.accessControlMethod = accessControlMethod;
        contentType = QCloudNetWorkConstants.ContentType.X_WWW_FORM_URLENCODED;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        setOrigin(origin);
        setAccessControlMethod(accessControlMethod);
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

        responseBodySerializer = new ResponseXmlS3BodySerializer(OptionObjectResult.class);
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
        if(origin == null){
            throw new CosXmlClientException("option request origin must not be null");
        }
        if(accessControlMethod == null){
            throw new CosXmlClientException("option request accessControlMethod must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.OPTIONS;
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
     * <p>
     * 设置模拟跨域访问的请求来源域名。
     * </p>
     *
     * @param origin 请求来源域名
     */
    public void setOrigin(String origin) {
        this.origin = origin;
        if(origin != null){
            requestHeaders.put("Origin",origin);
        }
    }

    /**
     * 获取设置的模拟跨域访问的请求来源域名。
     *
     * @return 请求来源域名
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * 设置模拟跨域访问的请求 HTTP 方法
     *
     * @param accessControlMethod 请求 HTTP 方法
     */
    public void setAccessControlMethod(String accessControlMethod) {
        if(accessControlMethod != null){
            this.accessControlMethod = accessControlMethod.toUpperCase();
            requestHeaders.put("Access-Control-Request-Method",this.accessControlMethod);
        }
    }

    /**
     * 获取设置的模拟跨域访问的请求 HTTP 方法
     *
     * @return 请求 HTTP 方法
     */
    public String getAccessControlMethod() {
        return accessControlMethod;
    }

    /**
     * 设置模拟跨域访问的请求头部
     *
     * @param accessControlHeaders 模拟跨域访问的请求头部
     */
    public void setAccessControlHeaders(String accessControlHeaders) {
        this.accessControlHeaders = accessControlHeaders;
        if(accessControlHeaders != null){
            requestHeaders.put("Access-Control-Request-Headers",accessControlHeaders);
        }
    }

    /**
     * 获取用户设置的模拟跨域访问的请求头部
     *
     * @return 模拟跨域访问的请求头部
     */
    public String getAccessControlHeaders() {
        return accessControlHeaders;
    }

    /**
     * 设置Object的COS路径
     *
     * @param cosPath COS 上的Object路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的COS Object路径
     *
     * @return Object路径
     */
    public String getCosPath() {
        return cosPath;
    }
}
