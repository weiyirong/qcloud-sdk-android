package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSRequestHeaderKey;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import java.util.Map;

/**
 * <p>
 * 给Bucket设置ACL(access control list)， 即用户空间（Bucket）的访问权限控制列表
 * </p>
 *
 */
final public class PutBucketACLRequest extends BucketRequest {

    public PutBucketACLRequest(String bucket){
        super(bucket);
    }

    /**
     * <p>
     * 设置Bucket访问权限
     * </p>
     *
     * <br>
     * 有效值：
     * <ul>
     * <li>private ：私有，默认值</li>
     * <li>public-read ：公有读</li>
     * <li>public-read-write ：公有读写</li>
     * </ul>
     * <br>
     *
     * @param cosacl acl字符串
     */
    public void setXCOSACL(String cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL, cosacl);
        }
    }

    /**
     * 设置Bucket的ACL信息
     *
     * @param cosacl acl枚举
     */
    public void setXCOSACL(COSACL cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL, cosacl.getAcl());
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
        return RequestMethod.PUT;
    }

    @Override
    public Map<String, String> getQueryString() {
        queryParameters.put("acl", null);
        return super.getQueryString();
    }

    @Override
    public RequestBodySerializer getRequestBody() {
        return RequestBodySerializer.bytes(COSRequestHeaderKey.APPLICATION_OCTET_STREAM, new byte[0]);
    }
}
