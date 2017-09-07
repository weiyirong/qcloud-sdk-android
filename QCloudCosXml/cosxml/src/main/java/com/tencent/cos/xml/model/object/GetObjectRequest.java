package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.assist.ContentRange;
import com.tencent.qcloud.network.assist.Range;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.response.serializer.body.ResponseBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseFilePartSerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseFileSerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlBodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpOkSerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.io.File;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/7.
 * author bradyxiao
 *  Get Object request is used to download a file (Object) locally. This action requires that the
 *  user has the read permission for the target Object or the read permission for the target Object
 *  has been made available for everyone (public-read).
 */
public class GetObjectRequest extends CosXmlRequest {
    private String rspContentType;
    private String rspContentLanguage;
    private String rspExpires;
    private String rspCacheControl;
    private String rspContentDisposition;
    private String rspContentEncoding;

    private String cosPath;
    private Range range;
    private QCloudProgressListener progressListener;
    private String savePath; //保留本地文件夹路径

    public GetObjectRequest(String savePath){
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        this.savePath = savePath;
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

        responseSerializer = new HttpPassAllSerializer();
        if(range != null){
            ResponseFilePartSerializer responseFileSerializer  = new ResponseFilePartSerializer(getDownloadPath(),
                    range, GetObjectResult.class);
            responseFileSerializer.setProgressListener(progressListener);
            responseBodySerializer = responseFileSerializer;
        }else{
            ResponseFileSerializer responseFileSerializer = new ResponseFileSerializer(getDownloadPath(),GetObjectResult.class);
            responseFileSerializer.setProgressListener(progressListener);
            responseBodySerializer = responseFileSerializer;
        }

    }

    @Override
    protected void setRequestQueryParams() {
        if(rspContentType != null){
            requestQueryParams.put("response-content-type",rspContentType);
        }
        if(rspContentLanguage != null){
            requestQueryParams.put("response-content-language",rspContentLanguage);
        }
        if(rspExpires != null){
            requestQueryParams.put("response-expires",rspExpires);
        }
        if(rspCacheControl != null){
            requestQueryParams.put("response-cache-control",rspCacheControl);
        }
        if(rspContentDisposition != null){
            requestQueryParams.put("response-content-dispositio",rspContentDisposition);
        }
        if(rspContentEncoding != null){
            requestQueryParams.put("response-content-encoding", rspContentEncoding);
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

    public void setRspContentType(String rspContentType) {
        this.rspContentType = rspContentType;

    }

    public String getRspContentType() {
        return rspContentType;
    }

    public void setRspContentLanguage(String rspContentLanguage) {
        this.rspContentLanguage = rspContentLanguage;

    }

    public String getRspContentLanguage() {
        return rspContentLanguage;
    }

    public void setRspExpires(String rspExpires) {
        this.rspExpires = rspExpires;

    }

    public String getRspExpires() {
        return rspExpires;
    }

    public void setRspCacheControl(String rspCacheControl) {
        this.rspCacheControl = rspCacheControl;

    }

    public String getRspCacheControl() {
        return rspCacheControl;
    }

    public void setRspContentDispositon(String rspContentDispositon) {
        this.rspContentDisposition = rspContentDispositon;

    }

    public String getRspContentDispositon() {
        return rspContentDisposition;
    }

    public void setRspContentEncoding(String rspContentEncoding) {
        this.rspContentEncoding = rspContentEncoding;

    }

    public String getRspContentEncoding() {
        return rspContentEncoding;
    }

    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }

    public void setRange(long start, long end) {
        if(start < 0) start = 0;
        Range range = new Range(start, end);
        requestHeaders.put("Range",range.toString());
        this.range = range;
    }

    public void setRange(long start) {
        if(start < 0) start = 0;
        Range range = new Range(start);
        requestHeaders.put("Range",range.toString());
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public void setIfModifiedSince(String ifModifiedSince){
        if(ifModifiedSince != null){
            requestHeaders.put("If-Modified-Since",ifModifiedSince);
        }
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public QCloudProgressListener getProgressListener() {
        return progressListener;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSavePath() {
        return savePath;
    }

    protected String getDownloadPath(){
        String path  = null;
        if(savePath != null && cosPath != null){
            if(!savePath.endsWith("/")){
                path = savePath + "/";
            }else{
                path = savePath;
            }
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            int separator = cosPath.lastIndexOf("/");
            if(separator >= 0){
                path = path + cosPath.substring(separator + 1);
            }else{
                path = path + cosPath;
            }
        }
        return path;
    }
}
