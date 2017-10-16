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
 * 设置 Object 的ACL访问权限列表
 * </p>
 * <p>
 * PutObjectACL 是一个覆盖操作，传入新的 ACL 将覆盖原有 ACL。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putObjectACL(PutObjectACLRequest)
 * @see com.tencent.cos.xml.CosXml#putObjectACLAsync(PutObjectACLRequest, CosXmlResultListener)
 */
final public class PutObjectACLRequest extends CosXmlRequest {
    private String cosPath;
    public PutObjectACLRequest(String bucket, String cosPath){
        this.bucket = bucket;
        this.cosPath = cosPath;
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    @Override
    protected void build() throws CosXmlClientException {
        super.build();

        // priority for request
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        //method for url
        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        //path for url
        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        // host for url
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
        // parameters for body

        //body for body
        requestOriginBuilder.body(new RequestByteArraySerializer(new byte[0], "text/plain"));

        responseBodySerializer = new ResponseXmlS3BodySerializer(PutObjectACLResult.class);
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
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT;
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
     * 设置 COS 的 Object 路径
     *
     * @param cosPath COS 上的 Object 路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的 COS Object 路径。
     *
     * @return COS 上的Object路径
     */
    public String getCosPath() {
        return cosPath;
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
            requestHeaders.put("x-cos-acl",xCOSACL);
        }
    }

    /**
     * 设置Bucket的ACL信息
     *
     * @param xCOSACL acl枚举
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
