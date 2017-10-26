package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.CompleteMultipartUpload;
import com.tencent.cos.xml.model.tag.Part;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.cos.xml.model.RequestXmlBodySerializer;


import java.util.ArrayList;
import java.util.Map;

/**
 * <p>
 * 完成整个分块上传。
 * </p>
 * <p>
 * 在使用该 API 时，您必须在请求 Body 中给出每一个块的 PartNumber 和 ETag，用来校验块的准确性。
 * </p>
 *  <br>
 * <ul>
 * <li>当上传块小于 1 MB 的时候，在调用该 API 时，会返回 400 EntityTooSmall；</li>
 * <li>当上传块编号不连续的时候，在调用该 API 时，会返回 400 InvalidPart；</li>
 * <li>当请求 Body 中的块信息没有按序号从小到大排列的时候，在调用该 API 时，会返回 400 InvalidPartOrder；</li>
 * <li>当 UploadId 不存在的时候，在调用该 API 时，会返回 404 NoSuchUpload。 </li>
 * </ul>
 *
 *
 * <p>
 * 建议您及时完成分块上传或者舍弃分块上传，因为已上传但是未终止的块会占用存储空间进而产生存储费用。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#completeMultiUpload(CompleteMultiUploadRequest)
 * @see com.tencent.cos.xml.CosXml#completeMultiUploadAsync(CompleteMultiUploadRequest, CosXmlResultListener)
 */
final public class CompleteMultiUploadRequest extends CosXmlRequest<CompleteMultiUploadResult>{

    private CompleteMultipartUpload completeMultipartUpload;
    private String uploadId;
    private String cosPath;
    public CompleteMultiUploadRequest(String bucket, String cosPath, String uploadId, Map<Integer,String> partNumberAndETag ){
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.uploadId = uploadId;
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        completeMultipartUpload = new CompleteMultipartUpload();
        completeMultipartUpload.partList = new ArrayList<Part>();
        setPartNumberAndETag(partNumberAndETag);
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

        requestOriginBuilder.body(new RequestXmlBodySerializer(completeMultipartUpload));

        responseBodySerializer = new ResponseXmlS3BodySerializer(CompleteMultiUploadResult.class);
    }

    public CompleteMultipartUpload getCompleteMultipartUpload() {
        return completeMultipartUpload;
    }

    /**
     * 添加单个分块的eTag值
     *
     * @param partNumbers 分块数
     * @param eTag 该分块的eTag值
     */
    public void setPartNumberAndETag(int partNumbers, String eTag){
        Part part = new Part();
        part.partNumber = partNumbers;
        part.eTag = eTag;
        completeMultipartUpload.partList.add(part);
    }

    /**
     * 添加多个分块的eTag值
     *
     */
    public void setPartNumberAndETag(Map<Integer,String> partNumberAndETag){
        if(partNumberAndETag != null){
            Part part;
            for(Map.Entry<Integer,String> entry : partNumberAndETag.entrySet()){
                part = new Part();
                part.partNumber = entry.getKey();
                part.eTag = entry.getValue();
                completeMultipartUpload.partList.add(part);
            }
        }
    }

    /**
     * 设置该次分块上传的uploadId
     *
     * @param uploadId 分块上传的UploadId
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 获取用户设置的该次分块上传的uploadId
     *
     * @return 分块上传的UploadId
     */
    public String getUploadId() {
        return uploadId;
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
     * 设置本次分块上传的COS路径
     *
     * @param cosPath COS路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的本次分块上传的路径
     *
     * @return COS路径
     */
    public String getCosPath() {
        return cosPath;
    }
}
