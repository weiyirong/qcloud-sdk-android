package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSRequestHeaderKey;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.qcloud.core.http.RequestBodySerializer;

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
 */
final public class InitMultipartUploadRequest extends ObjectRequest {


    public InitMultipartUploadRequest(String bucket, String cosPath){
        super(bucket, cosPath);
    }

    /**
     * 设置Cache-Control头部
     *
     * @param cacheControl Cache-Control头部
     */
    public void setCacheControl(String cacheControl) {
        if(cacheControl == null)return;
        addHeader(COSRequestHeaderKey.CACHE_CONTROL,cacheControl);
    }

    /**
     * 设置 Content-Disposition 头部
     *
     * @param contentDisposition Content-Disposition 头部
     */
    public void setContentDisposition(String contentDisposition) {
        if(contentDisposition == null)return;
        addHeader(COSRequestHeaderKey.CONTENT_DISPOSITION,contentDisposition);
    }

    /**
     * 设置 Content-Encoding 头部
     *
     * @param contentEncoding Content-Encoding头部
     */
    public void setContentEncoding(String contentEncoding) {
        if(contentEncoding == null)return;
        addHeader(COSRequestHeaderKey.CONTENT_ENCODING,contentEncoding);
    }

    /**
     * 设置 Expires 头部
     *
     * @param expires Expires 头部
     */
    public void setExpires(String expires) {
        if(expires == null)return;
        addHeader(COSRequestHeaderKey.EXPIRES,expires);
    }

    /**
     * 设置用户自定义头部信息
     *
     * @param key 自定义头部信息的key值，需要以 x-cos-meta- 开头
     * @param value 自定义头部信息的value值。
     */
    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            addHeader(key,value);
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
     * @param cosacl ACL属性
     */
    public void setXCOSACL(String cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL,cosacl);
        }
    }

    /**
     * <p>
     * 设置 Object 的 ACL 属性
     * </p>
     *
     * @param cosacl ACL枚举值
     */
    public void setXCOSACL(COSACL cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL,cosacl.getAcl());
        }
    }

    /**
     * <p>
     * 单独明确赋予用户读权限
     * </p>
     *
     * @param aclAccount 读权限用户列表
     */
    public void setXCOSGrantRead(ACLAccount aclAccount){
        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_READ, aclAccount.getAccout());
        }
    }


    /**
     * <p>
     * 赋予被授权者写的权限
     * </p>
     *
     * @param aclAccount 写权限用户列表
     */
    public void setXCOSGrantWrite(ACLAccount aclAccount){

        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_WRITE, aclAccount.getAccout());
        }
    }


    /**
     * <p>
     * 赋予被授权者读写权限。
     * </p>
     *
     * @param aclAccount 读写用户权限列表
     */
    public void setXCOSReadWrite(ACLAccount aclAccount){

        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_FULL_CONTROL, aclAccount.getAccout());
        }
    }

    @Override
    public String getMethod() {
        return RequestMethod.POST;
    }

    @Override
    public Map<String, String> getQueryString() {
        queryParameters.put("uploads", null);
        return queryParameters;
    }

    @Override
    public RequestBodySerializer getRequestBody() {
        return RequestBodySerializer.bytes(COSRequestHeaderKey.APPLICATION_OCTET_STREAM, new byte[0]);
    }
}
