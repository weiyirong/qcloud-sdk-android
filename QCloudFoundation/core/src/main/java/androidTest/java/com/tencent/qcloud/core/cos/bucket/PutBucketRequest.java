package com.tencent.qcloud.core.cos.bucket;

import com.tencent.qcloud.core.cos.CosXmlRequest;
import com.tencent.qcloud.core.cos.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.cos.common.COSACL;
import com.tencent.qcloud.core.cos.common.RequestContentType;
import com.tencent.qcloud.core.cos.common.RequestHeader;
import com.tencent.qcloud.core.cos.common.RequestMethod;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.action.QCloudBodyMd5Action;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestByteArraySerializer;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 创建Bucket
 * </p>
 *
 *
 */
final public class PutBucketRequest extends CosXmlRequest<PutBucketResult> {

    public PutBucketRequest(){
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

        requestOriginBuilder.body(new RequestByteArraySerializer(new byte[0], "text/plain"));

        requestActions.add(new QCloudBodyMd5Action());

        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketResult.class);
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
        requestMethod = RequestMethod.PUT;
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
     * <p>
     * 有效值：
     * <ul>
     * <li>private ：私有，默认值</li>
     * <li>public-read ：公有读</li>
     * <li>public-read-write ：公有读写</li>
     * </ul>
     * </p>
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
     * 赋予被授权者读的权限
     * </p>
     * <p>
     * uinList的列表项有两种格式：
     * <ul>
     * <li>当需要给子账户授权时，列表项为 uin/OwnerUin:uin/SubUin 格式的字符串</li>
     * <li>当需要给根账户授权时，列表项为 uin/OwnerUin:uin/OwnerUin 格式的字符串</li>
     * </ul>
     * </p>
     *
     * @param uinList x-cos-grant-read字符串列表
     */
    public void setXCOSGrantReadWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 0);
    }

    /**
     * <p>
     * 已废弃，请用{@link PutBucketRequest#setXCOSGrantReadWithUIN(List)} 替代。
     * </p>
     *
     * @param idList
     * @see PutBucketRequest#setXCOSGrantReadWithUIN(List)
     */
    @Deprecated
    public void setXCOSGrantRead(List<String> idList){
        setXCOSGrant(idList, 0, 0);
    }

    /**
     * <p>
     * 赋予被授权者写的权限
     * </p>
     *
     * <p>
     * uinList的中列表项的格式和{@link PutBucketRequest#setXCOSGrantReadWithUIN(List)}一致
     * </p>
     * @param uinList x-cos-grant-write字符串列表
     */
    public void setXCOSGrantWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 1);
    }

    /**
     * <p>
     * 已废弃，请用{@link PutBucketRequest#setXCOSGrantWriteWithUIN(List)} 替代。
     * </p>
     *
     * @param idList
     * @see PutBucketRequest#setXCOSGrantWriteWithUIN(List)
     */
    @Deprecated
    public void setXCOSGrantWrite(List<String> idList){
        setXCOSGrant(idList, 0, 1);
    }

    /**
     * <p>
     * 赋予被授权者读写权限。
     * </p>
     * <p>
     * uinList的中列表项的格式和{@link PutBucketRequest#setXCOSGrantReadWithUIN(List)}一致
     * </p>
     *
     * @param uinList x-cos-grant-full-control字符串列表
     */
    public void setXCOSReadWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 2);
    }

    /**
     * <p>
     * 已废弃，请用{@link PutBucketRequest#setXCOSReadWriteWithUIN(List)} 替代。
     * </p>
     *
     * @param idList
     * @see PutBucketRequest#setXCOSReadWriteWithUIN(List)
     */
    public void setXCOSReadWrite(List<String> idList){
        setXCOSGrant(idList, 0, 2);
    }

    private String getXCOSGrantForId(List<String> idList){
        if(idList != null){
            int size = idList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size -1; ++ i){
                    stringBuilder.append("id=\"qcs::cam::")
                            .append(idList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("id=\"qcs::cam::")
                        .append(idList.get(size -1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    private String getXCOSGrantForUIN(List<String> uinList){
        if(uinList != null){
            int size = uinList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size - 1; ++ i){
                    stringBuilder.append("uin=")
                            .append("\"").append(uinList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("uin=")
                        .append("\"").append(uinList.get(size - 1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    /**
     *
     * @param list ==> account list
     * @param idType ==> uin (old, 0) or id (new, 1)
     * @param grantType ==> x-cos-grant-read(0), x-cos-grant-write(1), x-cos-grant-full-control(2)
     */
    private void setXCOSGrant(List<String> list, int idType, int grantType){
        if(list != null){
            String grantMsg = null;
            if(idType == 0){
                grantMsg = getXCOSGrantForUIN(list);
            }else if(idType == 1){
                grantMsg = getXCOSGrantForId(list);
            }
            switch (grantType){
                case 0:
                    requestHeaders.put("x-cos-grant-read", grantMsg);
                    break;
                case 1:
                    requestHeaders.put("x-cos-grant-write", grantMsg);
                    break;
                case 2:
                    requestHeaders.put("x-cos-grant-full-control", grantMsg);
                    break;
            }
        }
    }
}
