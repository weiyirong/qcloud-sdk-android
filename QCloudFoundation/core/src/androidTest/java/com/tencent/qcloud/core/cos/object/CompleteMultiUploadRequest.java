package com.tencent.qcloud.core.cos.object;



import com.tencent.qcloud.core.cos.CosXmlRequest;
import com.tencent.qcloud.core.cos.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.cos.common.RequestContentType;
import com.tencent.qcloud.core.cos.common.RequestHeader;
import com.tencent.qcloud.core.cos.common.RequestMethod;
import com.tencent.qcloud.core.cos.tag.CompleteMultipartUpload;
import com.tencent.qcloud.core.cos.tag.Part;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestXmlBodySerializer;

import java.util.ArrayList;
import java.util.Map;

/**
 * <p>
 * 完成整个分块上传。
 * </p>
 * <p>
 * 在使用该 API 时，您必须在请求 Body 中给出每一个块的 PartNumber 和 ETag，用来校验块的准确性。
 * </p>
 *  <p>
 * <ul>
 * <li>当上传块小于 1 MB 的时候，在调用该 API 时，会返回 400 EntityTooSmall；</li>
 * <li>当上传块编号不连续的时候，在调用该 API 时，会返回 400 InvalidPart；</li>
 * <li>当请求 Body 中的块信息没有按序号从小到大排列的时候，在调用该 API 时，会返回 400 InvalidPartOrder；</li>
 * <li>当 UploadId 不存在的时候，在调用该 API 时，会返回 404 NoSuchUpload。 </li>
 * </ul>
 * </p>
 *
 * <p>
 * 建议您及时完成分块上传或者舍弃分块上传，因为已上传但是未终止的块会占用存储空间进而产生存储费用。
 * </p>
 *
 */
public class CompleteMultiUploadRequest extends CosXmlRequest<CompleteMultiUploadResult> {
    CompleteMultipartUpload completeMultipartUpload;
    private String uploadId;
    private String cosPath;
    public CompleteMultiUploadRequest(){
        contentType = RequestContentType.APPLICATION_XML;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        completeMultipartUpload = new CompleteMultipartUpload();
        completeMultipartUpload.partList = new ArrayList<Part>();
    }

    @Override
    public void build() throws QCloudClientException{
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
     * @param sha1 该分块的SHA1值
     */
    public void setPartNumberAndETag(int partNumbers, String sha1){
        Part part = new Part();
        part.partNumber = partNumbers;
        part.eTag = sha1;
        completeMultipartUpload.partList.add(part);
    }

    /**
     * 添加多个分块的eTag值
     *
     */
    public void setPartNumberAndETag(Map<Integer,String> parNumberAndSha1){
        if(parNumberAndSha1 != null){
            Part part;
            for(Map.Entry<Integer,String> entry : parNumberAndSha1.entrySet()){
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
    public void checkParameters() throws QCloudClientException {
        if(bucket == null){
            throw new QCloudClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new QCloudClientException("cosPath must not be null");
        }
        if(uploadId == null){
            throw new QCloudClientException("uploadID must not be null");
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
