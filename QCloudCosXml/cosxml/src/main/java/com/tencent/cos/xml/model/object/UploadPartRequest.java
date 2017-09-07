package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.serializer.body.RequestByteArraySerializer;
import com.tencent.qcloud.network.request.serializer.body.RequestFileBodySerializer;
import com.tencent.qcloud.network.request.serializer.body.RequestStreamBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 * Upload Part request is used to implement the multipart upload after initialization. The allowed
 * number of parts is limited to 10000, and the size of part should be between 1 MB and 5 GB.
 * Upload Part should be used with partNumber and uploadID. PartNumber is the part No. and supports
 * out-of-order upload.If the uploadID and partNumber are the same, the parts uploaded later will
 * overwrite the parts uploaded earlier. A 404 error "NoSuchUpload" will be returned if the UploadID
 * does not exist.
 */
public class UploadPartRequest extends CosXmlRequest {
    private int partNumber;
    private String uploadId;
    private String cosPath;
    private String srcPath;
    private byte[] data;
    private InputStream inputStream;
    private long fileLength;
    private long fileOffset;
    private long fileContentLength;

    private QCloudProgressListener progressListener;

    public UploadPartRequest(){
        contentType = RequestContentType.TEXT_PLAIN;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        fileOffset = -1;
        fileContentLength = -1L;
    }

    @Override
    public void build() {
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_LOW;

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

        if(srcPath != null){
            RequestFileBodySerializer requestFileBodySerializer = new RequestFileBodySerializer(srcPath,"text/plain", fileOffset, fileContentLength);
            requestFileBodySerializer.setProgressListener(progressListener);
            requestBodySerializer = requestFileBodySerializer;
        }else if(data != null){
            RequestByteArraySerializer requestByteArraySerializer = new RequestByteArraySerializer(data,"text/plain");
            requestByteArraySerializer.setProgressListener(progressListener);
            requestBodySerializer = requestByteArraySerializer;
        }else if(inputStream != null){
            RequestStreamBodySerializer requestStreamBodySerializer = new RequestStreamBodySerializer(inputStream, fileLength, "text/plain");
            requestStreamBodySerializer.setProgressListener(progressListener);
            requestBodySerializer = requestStreamBodySerializer;
        }

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(UploadPartResult.class);
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("partNumber", String.valueOf(partNumber));
        requestQueryParams.put("uploadID", uploadId);
    }

    @Override
    public void checkParameters() throws QCloudException{
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
        if(partNumber <= 0){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "partNumber must be >= 1");
        }
        if(uploadId == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "uploadID must not be null");
        }
        if(cosPath == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "cosPath must not be null");
        }
        if(srcPath == null && data == null && inputStream == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "Data Source must not be null");
        }
        if(srcPath != null){
            File file = new File(srcPath);
            if(!file.exists()){
                throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "upload file does not exist");
            }
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.PUT ;
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

    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setSrcPath(String srcPath, long fileOffset, long contentLength) {
        this.srcPath = srcPath;
        this.fileOffset = fileOffset;
        this.fileContentLength = contentLength;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setInputStream(InputStream inputStream, long fileLength) {
        this.inputStream = inputStream;
        this.fileLength = fileLength;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setProgressListener(QCloudProgressListener progressListener){
        this.progressListener = progressListener;
    }
}
