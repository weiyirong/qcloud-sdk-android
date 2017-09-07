package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.tag.CompleteMultipartUpload;
import com.tencent.cos.xml.model.tag.Part;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.serializer.body.RequestXmlBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 * Complete Multipart Upload is used to complete the entire multipart upload. You can use this API to
 * complete the upload operation when you have uploaded all parts using Upload Parts. When using this
 * API, you need to provide the PartNumber and ETag for every part in Body, to verify the accuracy of
 * parts.Merging the parts will take several minutes, thus COS will immediately return status code
 * 200 when the merging process starts. When merging, COS will return blank information periodically
 * to keep the connection active, until the merging process completes, upon which the COS will return
 * the content of the merged parts in Body.When calling this request, a return of "400 EntityTooSmall"
 * means the uploaded part is smaller than 1 MB; "400 InvalidPart" means the numbers of uploaded parts
 * are discontinuous; "400 InvalidPartOrder" means the part information entries in the request Body
 * are not sorted in ascending order according to their numbers; "404 NoSuchUpload" means the UploadId
 * does not exist.
 */
public class CompleteMultiUploadRequest extends CosXmlRequest{
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

        requestBodySerializer = new RequestXmlBodySerializer(completeMultipartUpload);

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(CompleteMultiUploadResult.class);
    }

    public CompleteMultipartUpload getCompleteMultipartUpload() {
        return completeMultipartUpload;
    }

    /**
     * set number and etag(md5).
     * @param partNumbers
     * @param sha1
     */
    public void setPartNumberAndETag(int partNumbers, String sha1){
        Part part = new Part();
        part.partNumber = partNumbers;
        part.eTag = sha1;
        completeMultipartUpload.partList.add(part);
    }

    /**
     * set number and etag(md5).
     * @param parNumberAndSha1
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
     *  set uploadId
     * @param uploadId
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
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
     * set cosPath for object.
     * @param cosPath
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }
}
