package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.ACLAccounts;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.request.serializer.RequestByteArraySerializer;

import java.util.Map;


/**
 * <p>
 * 初始化分片上传。
 * </p>
 *
 * <p>
 * 成功执行此请求以后会返回 UploadId 用于后续的 Upload Part 请求。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#initMultipartUpload(InitMultipartUploadRequest)
 * @see com.tencent.cos.xml.CosXml#initMultipartUploadAsync(InitMultipartUploadRequest, CosXmlResultListener)
 */
final public class InitMultipartUploadRequest extends CosXmlRequest {

    private String cosPath;

    public InitMultipartUploadRequest(String bucket, String cosPath){
        this.bucket = bucket;
        this.cosPath = cosPath;
        contentType = QCloudNetWorkConstants.ContentType.MULTIPART_FORM_DATA;
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

        requestOriginBuilder.body(new RequestByteArraySerializer(new byte[0],"text/plain"));

        responseBodySerializer = new ResponseXmlS3BodySerializer(InitMultipartUploadResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("uploads",null);
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
        requestMethod = QCloudNetWorkConstants.RequestMethod.POST;
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
     * 设置分片上传的 COS 路径
     *
     * @param cosPath COS 路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的分片上传 COS 路径
     *
     * @return COS 路径
     */
    public String getCosPath() {
        return cosPath;
    }

    /**
     * 设置Cache-Control头部
     *
     * @param cacheControl Cache-Control头部
     */
    public void setCacheControl(String cacheControl) {
        if(cacheControl == null)return;
        requestHeaders.put("Cache-Control",cacheControl);
    }

    /**
     * 设置 Content-Disposition 头部
     *
     * @param contentDisposition Content-Disposition 头部
     */
    public void setContentDisposition(String contentDisposition) {
        if(contentDisposition == null)return;
        requestHeaders.put("Content-Disposition",contentDisposition);
    }

    /**
     * 设置 Content-Encoding 头部
     *
     * @param contentEncoding Content-Encoding头部
     */
    public void setContentEncoding(String contentEncoding) {
        if(contentEncoding == null)return;
        requestHeaders.put("Content-Encoding",contentEncoding);
    }

    /**
     * 设置 Expires 头部
     *
     * @param expires Expires 头部
     */
    public void setExpires(String expires) {
        if(expires == null)return;
        requestHeaders.put("Expires",expires);
    }

    /**
     * 设置用户自定义头部信息
     *
     * @param key 自定义头部信息的key值，需要以 x-cos-meta- 开头
     * @param value 自定义头部信息的value值。
     */
    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            requestHeaders.put(key,value);
        }
    }

    /**
     * <p>
     * 设置 Object 的 ACL 属性
     * </p>
     *
     * <p>
     * 有效值：private，public-read-write，public-read；默认值：private
     * </p>
     *
     * @param xCOSACL ACL属性
     */
    public void setXCOSACL(String xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL);
        }
    }

    /**
     * <p>
     * 设置 Object 的 ACL 属性
     * </p>
     *
     * @param xCOSACL ACL枚举值
     */
    public void setXCOSACL(COSACL xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL.getACL());
        }
    }

    /**
     * <p>
     * 单独明确赋予用户读权限
     * </p>
     *
     * @param aclAccounts 读权限用户列表
     */
    public void setXCOSGrantRead(ACLAccounts aclAccounts){
        if (aclAccounts != null) {
            requestHeaders.put(RequestHeader.X_COS_GRANT_READ, aclAccounts.aclDesc());
        }
    }


    /**
     * <p>
     * 赋予被授权者写的权限
     * </p>
     *
     * @param aclAccounts 写权限用户列表
     */
    public void setXCOSGrantWrite(ACLAccounts aclAccounts){

        if (aclAccounts != null) {
            requestHeaders.put(RequestHeader.X_COS_GRANT_WRITE, aclAccounts.aclDesc());
        }
    }


    /**
     * <p>
     * 赋予被授权者读写权限。
     * </p>
     *
     * @param aclAccounts 读写用户权限列表
     */
    public void setXCOSReadWrite(ACLAccounts aclAccounts){

        if (aclAccounts != null) {
            requestHeaders.put(RequestHeader.X_COS_GRANT_FULL_CONTROL, aclAccounts.aclDesc());
        }
    }
}
