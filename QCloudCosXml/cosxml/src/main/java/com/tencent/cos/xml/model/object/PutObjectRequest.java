package com.tencent.cos.xml.model.object;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tencent.cos.xml.common.COSACL;
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
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpOkSerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 * Put Object request allows you to upload a file (Object) to the specified Bucket.
 */
public class PutObjectRequest extends CosXmlRequest {
    private String cosPath;
    private String srcPath;
    private byte[] data;
    private InputStream inputStream;
    private long fileLength;
    private QCloudProgressListener progressListener;

    public PutObjectRequest(){
        contentType = RequestContentType.TEXT_PLAIN;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
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
                if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                    requestOriginBuilder.header(entry.getKey(), entry.getValue());
                }
        }

        if(srcPath != null){
            RequestFileBodySerializer requestFileBodySerializer = new RequestFileBodySerializer(srcPath,"text/plain");
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
        responseBodySerializer = new ResponseXmlS3BodySerializer(PutObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {

    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
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
        requestMethod = RequestMethod.PUT;
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

    public void setProgressListener(QCloudProgressListener progressListener){
        this.progressListener = progressListener;
    }

    public QCloudProgressListener getProgressListener() {
        return progressListener;
    }

    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }

    public void setSrcPath(@NonNull String srcPath){
        this.srcPath = srcPath;
    }

    public String getSrcPath(){
       return srcPath;
    }

    public void setCacheControl(String cacheControl) {
        if(cacheControl == null)return;
        requestHeaders.put("Cache-Control",cacheControl);
    }

    public void setContentDisposition(String contentDisposition) {
        if(contentDisposition == null)return;
        requestHeaders.put("Content-Disposition",contentDisposition);
    }

    public void setContentEncodeing(String contentEncodeing) {
        if(contentEncodeing == null)return;
        requestHeaders.put("Content-Encoding",contentEncodeing);
    }

    public void setExpires(String expires) {
        if(expires == null)return;
        requestHeaders.put("Expires",expires);
    }

    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            requestHeaders.put(key,value);
        }
    }

    public void setXCOSACL(COSACL xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL.getACL());
        }
    }

    public void setXCOSACL(String xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL);
        }
    }

    public void setXCOSGrantReadWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 0);
    }

    public void setXCOSGrantRead(List<String> idList){
        setXCOSGrant(idList, 0, 0);
    }


    public void setXCOSGrantWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 1);
    }

    public void setXCOSGrantWrite(List<String> idList){
        setXCOSGrant(idList, 0, 1);
    }

    public void setXCOSReadWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 2);
    }

    public void setXCOSReadWrite(List<String> idList){
        setXCOSGrant(idList, 0, 2);
    }

    private String getXCOSGrantForId(List<String> idList){
        if(idList != null){
            int size = idList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size -1; ++ i){
                    stringBuilder.append("id=\"qcs::cam::")
                            .append(idList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("id=\"qcs::cam::")
                        .append(idList.get(size -1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    private String getXCOSGrantForUIN(List<String> uinList){
        if(uinList != null){
            int size = uinList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size - 1; ++ i){
                    stringBuilder.append("uin=")
                            .append("\"").append(uinList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("uin=")
                        .append("\"").append(uinList.get(size - 1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    /**
     *
     * @param list ==> account list
     * @param idType ==> uin (old, 0) or id (new, 1)
     * @param grantType ==> x-cos-grant-read(0), x-cos-grant-write(1), x-cos-grant-full-control(2)
     */
    private void setXCOSGrant(List<String> list, int idType, int grantType){
        if(list != null){
            String grantMsg = null;
            if(idType == 0){
                grantMsg = getXCOSGrantForUIN(list);
            }else if(idType == 1){
                grantMsg = getXCOSGrantForId(list);
            }
            switch (grantType){
                case 0:
                    requestHeaders.put("x-cos-grant-read", grantMsg);
                    break;
                case 1:
                    requestHeaders.put("x-cos-grant-write", grantMsg);
                    break;
                case 2:
                    requestHeaders.put("x-cos-grant-full-control", grantMsg);
                    break;
            }
        }
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
}
