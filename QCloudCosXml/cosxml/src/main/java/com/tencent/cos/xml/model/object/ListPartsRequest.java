package com.tencent.cos.xml.model.object;

import android.text.TextUtils;

import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpOkSerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.Map;


/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 * List Parts is used to query uploaded parts in a specific multipart upload.
 */
public class ListPartsRequest extends CosXmlRequest {
    private String uploadId;
    private String maxParts;
    private String partNumberMarker;
    private String encodingType;
    private String cosPath;
    public ListPartsRequest(){
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
        requestMethod = RequestMethod.GET;
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

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setMaxParts(int maxParts) {
        maxParts = maxParts <= 0 ? 1: maxParts;
        this.maxParts = String.valueOf(maxParts);
    }

    public int getMaxParts() {
        return Integer.parseInt(maxParts);
    }

    public void setPartNumberMarker(int partMumberMarker) {
        this.partNumberMarker = String.valueOf(partMumberMarker);
    }

    public int getPartNumberMarker() {
        return Integer.parseInt(partNumberMarker);
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }
}
