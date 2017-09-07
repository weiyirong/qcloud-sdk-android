package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.Map;


/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 * Abort Multipart Upload is used to abort a multipart upload operation and delete parts that are
 * already uploaded. When Abort Multipart Upload is called, the Upload Parts will return failure
 * to any request that is using the Upload Parts. "404 NoSuchUpload" will be returned if the UploadID
 * does not exist.It is recommended that you complete multipart upload in time or abort the upload
 * operation for the reason that parts that have been uploaded but not aborted will take up storage,
 * incurring cost.
 */
public class AbortMultiUploadRequest extends CosXmlRequest{
    // uploadId for aborting multi upload.
    private String uploadId;
    // cos path
    private String cosPath;
    public AbortMultiUploadRequest(){
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
    }


    @Override
    public void build() {
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

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(AbortMultiUploadResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("uploadID",uploadId);
    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
        if(cosPath == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "cosPath must not be null");
        }
        if(uploadId == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "uploadID must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.DELETE;
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
     * set uploadId for Abort Multi upload.
     *@param uploadId
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    /**
     * set cosPath for Abort Multi upload.
     * @param cosPath
     */
    public void setCosPath(String cosPath){
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }
}
