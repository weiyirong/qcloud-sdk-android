package com.tencent.cos.xml.model.object;


import android.support.annotation.NonNull;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.serializer.body.RequestByteArraySerializer;
import com.tencent.qcloud.network.request.serializer.body.RequestFormDataSerializer;
import com.tencent.qcloud.network.request.serializer.body.RequestStreamBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/7.
 * author bradyxiao
 * Append request is used to upload a file (Object) to Bucket via multipart upload method.
 * A file must be configured Appendable before it can be uploaded using Append. If you commence Put
 * Object operation to an Appendable file, the file will be overwritten and its attribute will change
 * to Normal.You can query the attribute of the file using Head Object operation. When you initiate
 * Head Object request, the custom Header [x-cos-object-type] will be returned, which only contains
 * two enumerated values: Normal or Appendable. Recommended size of the appended file is 1M - 5G.
 * COS will return 409 error if the value of position and the length of current Object are inconsistent.
 * COS will return "409 ObjectNotAppendable" if you try to Append a Normal Object. Appendable files
 * cannot be copied, are not involved in versioning, life cycle management and cannot be replicated
 * across domains.
 */
public class AppendObjectRequest extends CosXmlRequest {

    /** 追加的起始点 */
    private long position = 0;
    private String cosPath;
    private String srcPath;
    private byte[] data;
    private InputStream inputStream;
    private long fileLength;

    private QCloudProgressListener progressListener;

    public AppendObjectRequest(){
        contentType = RequestContentType.MULITPART_FORMM_DATA;
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
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
        }

        if(srcPath != null){
            RequestFormDataSerializer requestFormDataSerializer = new RequestFormDataSerializer();
            requestFormDataSerializer.setProgressListener(progressListener);
            requestFormDataSerializer.uploadFile(srcPath,"file");
            requestBodySerializer = requestFormDataSerializer;
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
        responseBodySerializer = new ResponseXmlS3BodySerializer(AppendObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("append",null);
        requestQueryParams.put("position",String.valueOf(position));
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

    // set position (start position)for append object.
    public void setPosition(long position) {
        if(position < 0)this.position = 0;
        this.position = position;
    }

    public long getPosition() {
        return position;
    }

    // set cosPath for append Object
    public void setCosPath(@NonNull String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }

    //if append object by file, need to set srcPath .
    public void setSrcPath(@NonNull String srcPath) {
        this.srcPath = srcPath;
    }

    public String getSrcPath() {
       return srcPath;
    }

    // if append object by byte[], need to set Data
    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    // if append object by stream. need to set inputStream.
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

    // set progress for append object.
    public QCloudProgressListener getProgressListener() {
        return progressListener;
    }

    // set cache control for object.
    public void setCacheControl(String cacheControl) {
        if(cacheControl == null)return;
        requestHeaders.put("Cache-Control",cacheControl);
    }

    // set content-disposition for object.
    public void setContentDisposition(String contentDisposition) {
        if(contentDisposition == null)return;
        requestHeaders.put("Content-Disposition",contentDisposition);
    }

    // set content encoding for object.
    public void setContentEncodeing(String contentEncodeing) {
        if(contentEncodeing == null)return;
        requestHeaders.put("Content-Encoding",contentEncodeing);
    }

    // set expires for object.
    public void setExpires(String expires) {
        if(expires == null)return;
        requestHeaders.put("Expires",expires);
    }

    // set cos meta for object.
    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            requestHeaders.put(key,value);
        }
    }

    /**
     * set cos acl for object.
     * @param xCOSACL {@link COSACL}
     */
    public void setXCOSACL(String xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL);
        }
    }

    public void setXCOSACL(COSACL xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL.getACL());
        }
    }

    /**
     * set grant read for object.
     * @param uinList, for example, uin:rootaccount/uin:subroot.
     */
    public void setXCOSGrantReadWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 0);
    }

    /**
     * set grant read for object.
     * @param idList, for example, rootaccount/subaccout.
     */
    public void setXCOSGrantRead(List<String> idList){
        setXCOSGrant(idList, 0, 0);
    }

    /**
     * set grant write for object.
     * @param uinList, for example, uin:rootaccount/uin:subroot.
     */
    public void setXCOSGrantWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 1);
    }

    /**
     * set grant write for object.
     * @param idList, for example, rootaccount/subaccout.
     */
    public void setXCOSGrantWrite(List<String> idList){
        setXCOSGrant(idList, 0, 1);
    }

    /**
     * set grant full control for object.
     * @param uinList, for example, uin:rootaccount/uin:subroot.
     */
    public void setXCOSReadWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 2);
    }

    /**
     * set grant full control for object.
     * @param idList, for example, rootaccount/subaccout.
     */
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
}
