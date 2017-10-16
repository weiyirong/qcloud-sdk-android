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
 * 舍弃一个分块上传并删除已上传的块。
 * </p>
 * <p>
 * 当您调用 Abort Multipart Upload 时，如果有正在使用这个 Upload Parts 上传块的请求，
 * 则 Upload Parts 会返回失败。当该 UploadId 不存在时，会返回 404 NoSuchUpload。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#abortMultiUpload(AbortMultiUploadRequest)
 * @see com.tencent.cos.xml.CosXml#abortMultiUploadAsync(AbortMultiUploadRequest, CosXmlResultListener)
 */
final public class AbortMultiUploadRequest extends CosXmlRequest<AbortMultiUploadResult>{

    // uploadId for aborting multi upload.
    private String uploadId;
    // cos path
    private String cosPath;
    public AbortMultiUploadRequest(String bucket, String cosPath, String uploadId){
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.uploadId = uploadId;
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

        responseBodySerializer = new ResponseXmlS3BodySerializer(AbortMultiUploadResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("uploadID",uploadId);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new CosXmlClientException("cosPath must not be null");
        }
        if(uploadId == null){
            throw new CosXmlClientException("uploadID must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.DELETE;
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
     * 设置分片上传的uploadId
     *
     *@param uploadId
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 获取设置的分片上传的uploadId
     *
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * 设置上传到COS的路径
     *
     * @param cosPath
     */
    public void setCosPath(String cosPath){
        this.cosPath = cosPath;
    }

    /**
     * 获取设置的COS上的路径
     *
     * @return
     */
    public String getCosPath() {
        return cosPath;
    }
}
