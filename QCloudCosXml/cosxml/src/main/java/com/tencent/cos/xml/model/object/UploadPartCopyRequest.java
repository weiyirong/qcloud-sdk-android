package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlResultListener;


/**
 * <p>
 * 实现将一个文件的分块内容从源路径复制到目标路径。
 * </p>
 * <H1>初始化 init multiupload, 获取uploadId</H1>
 * <H1>Upload Part Copy </H1>
 * <H1> 完成 complete multiupload </H1>
 *
 *
 * @see com.tencent.cos.xml.CosXml#copyObject(UploadPartCopyRequest)
 * @see com.tencent.cos.xml.CosXml#copyObjectAsync(UploadPartCopyRequest, CosXmlResultListener)
 */

public class UploadPartCopyRequest extends CopyObjectRequest {

    /** 指定的分块号*/
    private int partNumber = -1;
    /**init upload generate' s uploadId by service*/
    private String uploadId = null;

    public UploadPartCopyRequest(String bucket, String cosPath, int partNumber, String uploadId, CopySourceStruct copySourceStruct){
       this(bucket, cosPath, partNumber, uploadId, copySourceStruct, -1, -1);
    }

    public UploadPartCopyRequest(String bucket, String cosPath, int partNumber, String uploadId, CopySourceStruct copySourceStruct,
                                 long start, long end){
        super(bucket, cosPath, copySourceStruct);
        this.partNumber = partNumber;
        this.uploadId = uploadId;
        setCopyRanage(start, end);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("partNumber", String.valueOf(partNumber));
        requestQueryParams.put("uploadId", uploadId);
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        super.checkParameters();
        if(partNumber <= 0){
            throw new CosXmlClientException("partNumber must be >= 1");
        }
        if(uploadId == null){
            throw new CosXmlClientException("uploadID must not be null");
        }
    }

    /**
     * set cosPath for Abort Multi upload.
     * @param cosPath
     */
    public void setCosPath(String cosPath){
        setCosPath(cosPath);
    }

    public String getCosPath() {
        return getCosPath();
    }

    /** Indicate absolute path of source file URL from CopySourceStruct.
     * You can specify the history version with the versionid sub-resource
     * @param copySource
     */
    public void setCopySource(CopySourceStruct copySource){
        super.setCopySource(copySource);
    }

    public void setCopyRanage(long start, long end){
        if(start >= 0 && end >= start){
            String bytes = "bytes=" + start + "-" + end;
            requestHeaders.put("x-cos-copy-source-range", bytes);
        }
    }

    /**
     * The action is performed if the Object has been modified since the specified time,
     * otherwise error code 412 will be returned.
     * It can be used with x-cos-copy-source-If-None-Match.
     * Using it with other conditions can cause a conflict.
     * @param sourceIfModifiedSince
     */
    public void setCopyIfModifiedSince(String sourceIfModifiedSince){
        if(sourceIfModifiedSince != null){
            requestHeaders.put("x-cos-copy-source-If-Modified-Since", sourceIfModifiedSince);
        }
    }

    /**
     * The action is performed if the Object has not been modified since the specified time,
     * otherwise error code 412 will be returned.
     * It can be used with x-cos-copy-source-If-Match.
     * it with other conditions can cause a conflict.
     * @param sourceIfUnmodifiedSince
     */
    public void setCopyIfUnmodifiedSince(String sourceIfUnmodifiedSince){
        if(sourceIfUnmodifiedSince != null){
            requestHeaders.put("x-cos-copy-source-If-Unmodified-Since", sourceIfUnmodifiedSince);
        }
    }

    /**
     * The action is performed if the Etag of Object is the same as the given one,
     * otherwise error code 412 will be returned.
     * It can be used with x-cos-copy-source-If-Unmodified-Since.
     * Using it with other conditions can cause a conflict.
     * @param eTag
     */
    public void setCopyIfMatch(String eTag){
        if(eTag != null){
            requestHeaders.put("x-cos-copy-source-If-Match", eTag);
        }
    }

    /**
     * The action is performed if the Etag of Object is different from the given one,
     * otherwise error code 412 will be returned.
     * It can be used with x-cos-copy-source-If-Modified-Since.
     * Using it with other conditions can cause a conflict.
     * @param eTag
     */
    public void setCopyIfNoneMatch(String eTag){
        if(eTag != null){
            requestHeaders.put("x-cos-copy-source-If-None-Match", eTag);
        }
    }

    public String getCopySource(){
        return getCopySource();
    }

}
