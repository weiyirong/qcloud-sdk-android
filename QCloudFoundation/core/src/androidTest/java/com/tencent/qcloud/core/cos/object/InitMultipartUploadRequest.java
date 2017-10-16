package com.tencent.qcloud.core.cos.object;

import com.tencent.qcloud.core.cos.CosXmlRequest;
import com.tencent.qcloud.core.cos.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.cos.common.COSACL;
import com.tencent.qcloud.core.cos.common.RequestContentType;
import com.tencent.qcloud.core.cos.common.RequestMethod;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestByteArraySerializer;

import java.util.List;
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
public class InitMultipartUploadRequest extends CosXmlRequest<InitMultipartUploadResult> {

    private String cosPath;

    public InitMultipartUploadRequest(){
        contentType = RequestContentType.MULITPART_FORMM_DATA;
    }

    @Override
    public void build() throws QCloudClientException {
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

        requestOriginBuilder.body(new RequestByteArraySerializer(new byte[0],"text/plain"));

        responseBodySerializer = new ResponseXmlS3BodySerializer(InitMultipartUploadResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("uploads",null);
    }

    @Override
    public void checkParameters() throws QCloudClientException {
        if(bucket == null){
            throw new QCloudClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new QCloudClientException("cosPath must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.POST;
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
     * @param contentEncodeing Content-Encoding头部
     */
    public void setContentEncodeing(String contentEncodeing) {
        if(contentEncodeing == null)return;
        requestHeaders.put("Content-Encoding",contentEncodeing);
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
     * 已废弃，请用{@link InitMultipartUploadRequest#setXCOSGrantReadWithUIN(List)} 替代。
     * </p>
     *
     * @param idList
     * @see InitMultipartUploadRequest#setXCOSGrantReadWithUIN(List)
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
     * uinList的中列表项的格式和{@link InitMultipartUploadRequest#setXCOSGrantReadWithUIN(List)}一致
     * </p>
     * @param uinList x-cos-grant-write字符串列表
     */
    public void setXCOSGrantWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 1);
    }

    /**
     * <p>
     * 已废弃，请用{@link InitMultipartUploadRequest#setXCOSGrantWriteWithUIN(List)} 替代。
     * </p>
     *
     * @param idList
     * @see InitMultipartUploadRequest#setXCOSGrantWriteWithUIN(List)
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
     * uinList的中列表项的格式和{@link InitMultipartUploadRequest#setXCOSGrantReadWithUIN(List)}一致
     * </p>
     *
     * @param uinList x-cos-grant-full-control字符串列表
     */
    public void setXCOSReadWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 2);
    }

    /**
     * <p>
     * 已废弃，请用{@link InitMultipartUploadRequest#setXCOSReadWriteWithUIN(List)} 替代。
     * </p>
     *
     * @param idList
     * @see InitMultipartUploadRequest#setXCOSReadWriteWithUIN(List)
     */
    @Deprecated
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
