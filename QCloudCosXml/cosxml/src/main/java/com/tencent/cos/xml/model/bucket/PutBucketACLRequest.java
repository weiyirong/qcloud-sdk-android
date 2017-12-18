package com.tencent.cos.xml.model.bucket;


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
 * 给Bucket设置ACL(access control list)， 即用户空间（Bucket）的访问权限控制列表
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putBucket(PutBucketRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketAsync(PutBucketRequest, CosXmlResultListener)
 */
final public class PutBucketACLRequest extends CosXmlRequest {

    public PutBucketACLRequest(String bucket){
        setBucket(bucket);
        contentType = QCloudNetWorkConstants.ContentType.XML;
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

        requestOriginBuilder.body(new RequestByteArraySerializer(new byte[0], "text/plain"));

        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketACLResult.class);

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
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
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
     * @param xCOSACL acl字符串
     */
    public void setXCOSACL(String xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put(RequestHeader.X_COS_ACL, xCOSACL);
        }
    }

    /**
     * 设置Bucket的ACL信息
     *
     * @param xCOSACL acl枚举
     */
    public void setXCOSACL(COSACL xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put(RequestHeader.X_COS_ACL, xCOSACL.getACL());
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
