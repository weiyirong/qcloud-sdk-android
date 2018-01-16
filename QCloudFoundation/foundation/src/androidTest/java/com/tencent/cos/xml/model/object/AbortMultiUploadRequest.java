package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.http.RequestBodySerializer;

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
*/
final public class AbortMultiUploadRequest extends ObjectRequest {

    // uploadId for aborting multi upload.
    private String uploadId;

    public AbortMultiUploadRequest(String bucket, String cosPath, String uploadId) {
        super(bucket, cosPath);
        this.uploadId = uploadId;
    }

    @Override
    public String getMethod() {
        return RequestMethod.DELETE;
    }


    @Override
    public Map<String, String> getQueryString() {
        queryParameters.put("uploadID",uploadId);
        return queryParameters;
    }

    @Override
    public RequestBodySerializer getRequestBody() {
        return null;
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        super.checkParameters();
        if(uploadId == null){
            throw new CosXmlClientException("uploadID must not be null");
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

}
