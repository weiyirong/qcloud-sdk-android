package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSRequestHeaderKey;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.tag.CompleteMultipartUpload;
import com.tencent.cos.xml.transfer.XmlBuilder;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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
 */
final public class CompleteMultiUploadRequest extends ObjectRequest{

    private CompleteMultipartUpload completeMultipartUpload;
    private String uploadId;

    public CompleteMultiUploadRequest(String bucket, String cosPath, String uploadId, Map<Integer,String> partNumberAndETag ){
        super(bucket,cosPath);
        this.uploadId = uploadId;
        completeMultipartUpload = new CompleteMultipartUpload();
        completeMultipartUpload.parts = new ArrayList<CompleteMultipartUpload.Part>();
        setPartNumberAndETag(partNumberAndETag);
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
        CompleteMultipartUpload.Part part = new CompleteMultipartUpload.Part();
        part.partNumber = partNumbers;
        part.eTag = eTag;
        completeMultipartUpload.parts.add(part);
    }

    /**
     * 添加多个分块的eTag值
     *
     */
    public void setPartNumberAndETag(Map<Integer,String> partNumberAndETag){
        if(partNumberAndETag != null){
            CompleteMultipartUpload.Part part;
            for(Map.Entry<Integer,String> entry : partNumberAndETag.entrySet()){
                part = new CompleteMultipartUpload.Part();
                part.partNumber = entry.getKey();
                part.eTag = entry.getValue();
                completeMultipartUpload.parts.add(part);
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
    public String getMethod() {
        return RequestMethod.POST;
    }


    @Override
    public Map<String, String> getQueryString() {
        queryParameters.put("uploadID", uploadId);
        return queryParameters;
    }

    @Override
    public RequestBodySerializer getRequestBody() throws CosXmlClientException {
        try {
            return RequestBodySerializer.string(COSRequestHeaderKey.APPLICATION_XML,
                    XmlBuilder.buildCompleteMultipartUpload(completeMultipartUpload));
        } catch (IOException e) {
            throw new CosXmlClientException(e);
        } catch (XmlPullParserException e) {
            throw new CosXmlClientException(e);
        }
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        super.checkParameters();
        if(uploadId == null){
            throw new CosXmlClientException("uploadID must not be null");
        }
    }

}
