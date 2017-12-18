package com.tencent.cos.xml.model.object;

import android.text.TextUtils;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;


import java.util.Map;


/**
 * <p>
 * 查询特定分块上传中的已上传的块。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#listParts(ListPartsRequest)
 * @see com.tencent.cos.xml.CosXml#listPartsAsync(ListPartsRequest, CosXmlResultListener)
 */
final public class ListPartsRequest extends CosXmlRequest {

    private String uploadId;
    private String maxParts;
    private String partNumberMarker;
    private String encodingType;
    private String cosPath;

    public ListPartsRequest(String bucket, String cosPath, String uploadId){
        setBucket(bucket);
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

        responseBodySerializer = new ResponseXmlS3BodySerializer(ListPartsResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        if(!TextUtils.isEmpty(uploadId)){
            requestQueryParams.put("uploadID",uploadId);
        }
        if(!TextUtils.isEmpty(maxParts)){
            requestQueryParams .put("max-parts",maxParts);
        }
        if(!TextUtils.isEmpty(partNumberMarker)){
            requestQueryParams.put("part-number-marker",partNumberMarker);
        }
        if(!TextUtils.isEmpty(encodingType)){
            requestQueryParams.put("Encoding-type",encodingType);
        }
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
        requestMethod = QCloudNetWorkConstants.RequestMethod.GET;
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
     * 设置查询的UploadId
     *
     * @param uploadId 分片上传的UploadId
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 获取用户设置的UploadId
     *
     * @return 分片上传的UploadId
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * 设置单次返回的最大条目数
     *
     * @param maxParts 查询返回的最大条目数
     */
    public void setMaxParts(int maxParts) {
        maxParts = maxParts <= 0 ? 1: maxParts;
        this.maxParts = String.valueOf(maxParts);
    }

    /**
     * 获取用户设置的单次返回最大条目数
     *
     * @return 查询返回的最大条目数
     */
    public int getMaxParts() {
        return Integer.parseInt(maxParts);
    }

    /**
     * 设置列出分片的起点。
     *
     * @param partNumberMarker 列出分片的起点，可从ListPartsResult中读取。
     *
     */
    public void setPartNumberMarker(int partNumberMarker) {
        this.partNumberMarker = String.valueOf(partNumberMarker);
    }

    /**
     * 获取用户设置的NumberMarker
     *
     * @return 用户设置的NumberMarker
     */
    public int getPartNumberMarker() {
        return Integer.parseInt(partNumberMarker);
    }

    /**
     * 设置返回值的编码方式
     *
     * @param encodingType 返回值的编码方式
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * 获取用户设置的返回值编码方式
     *
     * @return 返回值编码方式
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * 设置所List分片上传的 COS 路径
     *
     * @param cosPath COS 路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的 COS 路径
     *
     * @return COS 路径
     */
    public String getCosPath() {
        return cosPath;
    }
}
