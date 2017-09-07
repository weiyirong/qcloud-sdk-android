package com.tencent.cos.xml.model.bucket;

import android.text.TextUtils;

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
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 * List Multiparts Uploads is used to query multipart upload operations that are still in process.
 * Up to 1000 such operations can be listed each time.
 *
 */
public class ListMultiUploadsRequest extends CosXmlRequest {

    /**Delimiter is a sign.
     * If Prefix exists, the same paths between Prefix and delimiter will be grouped as the same
     * type and defined Common Prefix, then all Common Prefixes will be listed.
     * If Prefix doesn't exist, the listing process will start from the beginning of the path*/
    private String delimiter;
    /**Indicate the encoding method of the returned value*/
    private String encodingType;
    /**Prefix match, used to specify the prefix address of the returned file*/
    private String prefix;
    /**Max number of entries returned each time, default is 1000*/
    private String maxUploads;
    /**Used together with upload-id-marker
     If upload-id-marker is not specified, entries whose ObjectNames are in front of key-marker
     (according to alphabetical order) will be listed.If upload-id-marker is specified, besides
     the above entries, those whose ObjectNames are equal to key-marker and UploadIDs are in front
     of upload-id-marker (according to alphabetical order) will also be listed.*/
    private String keyMarker;
    /**Used together with key-marker
     If key-marker is not specified, upload-id-marker will be ignored
     If key-marker is specified, entries whose ObjectNames are in front of key-marker
     (according to alphabetical order) will be listed, and entries whose ObjectNames
     are equal to key-marker and UploadIDs are in front of upload-id-marker
     (according to alphabetical order) will also be listed.*/
    private String uploadIdMarker;

    public ListMultiUploadsRequest(){
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
        responseBodySerializer = new ResponseXmlS3BodySerializer(ListMultiUploadsResult.class);

    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("uploads",null);
        if(!TextUtils.isEmpty(delimiter)){
            requestQueryParams .put("delimiter",delimiter);
        }
        if(!TextUtils.isEmpty(encodingType)){
            requestQueryParams.put("Encoding-type",encodingType);
        }
        if(!TextUtils.isEmpty(prefix)){
            requestQueryParams.put("Prefix",prefix);
        }
        if(!TextUtils.isEmpty(maxUploads)){
            requestQueryParams .put("max-uploads",maxUploads);
        }
        if(!TextUtils.isEmpty(keyMarker)){
            requestQueryParams.put("key-marker",keyMarker);
        }
        if(!TextUtils.isEmpty(uploadIdMarker)){
            requestQueryParams.put("upload-id-marker",uploadIdMarker);
        }
    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.GET;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    public String getKeyMarker() {
        return keyMarker;
    }

    public void setMaxUploads(String maxUploads) {
        this.maxUploads = maxUploads;
    }

    public String getMaxUploads() {
        return maxUploads;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

}
