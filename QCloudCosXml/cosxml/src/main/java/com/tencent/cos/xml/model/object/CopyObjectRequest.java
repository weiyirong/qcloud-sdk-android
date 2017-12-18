package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSStorageClass;
import com.tencent.cos.xml.common.MetaDataDirective;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.utils.URLEncodeUtils;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.request.serializer.RequestByteArraySerializer;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/9/20.
 * author bradyxiao
 *
 * Copy Object request is used to copy a file from source path to the destination path.
 * The recommended file size is 1M-5G. For any file above 5G, please use multipart upload (Upload - Copy).
 * In the process of copying, file meta-attributes and ACLs can be modified.
 * Users can use this API to move or rename a file, modify file attributes and create a copy.
 *
 */
public class CopyObjectRequest extends CosXmlRequest {

    // copy source struct
    private CopySourceStruct copySourceStruct;
    // cos destination object's cosPath
    private String cosPath;
    public CopyObjectRequest(String bucket, String cosPath, CopySourceStruct copySourceStruct){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.copySourceStruct = copySourceStruct;
        contentType = QCloudNetWorkConstants.ContentType.TEXT_PLAIN;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE, contentType);
        setCopySource(copySourceStruct);
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT;
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

    @Override
    protected void setRequestQueryParams() {
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new CosXmlClientException("cosPath must not be null");
        }
        if(copySourceStruct == null){
            throw new CosXmlClientException("copy source must not be null");
        }else {
            copySourceStruct.checkParameters();
        }
    }

    @Override
    public void build() throws CosXmlClientException {
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Object object : requestQueryParams.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
            }
        }

        if(requestHeaders.size() > 0){
            for(Object object : requestHeaders.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
            }
        }

        requestOriginBuilder.body(new RequestByteArraySerializer(new byte[0], "text/plain"));

        if(this instanceof UploadPartCopyRequest){
            responseBodySerializer = new ResponseXmlS3BodySerializer(UploadPartCopyResult.class);
        }else {
            responseBodySerializer = new ResponseXmlS3BodySerializer(CopyObjectResult.class);
        }
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

    /**
     * Indicate absolute path of source file URL from CopySourceStruct.
     * You can specify the history version with the versionid sub-resource
     * @param copySource
     */
    public void setCopySource(CopySourceStruct copySource){
        this.copySourceStruct = copySource;
        if(copySourceStruct != null){
            requestHeaders.put("x-cos-copy-source", copySourceStruct.toString());
        }
    }

    public String getCopySource(){
        return copySourceStruct.toString();
    }

    /**
     * Indicate whether to copy metadata.
     * Enumerated values: Copy, Replaced. The default is Copy.
     * If it is marked as Copy, the copying action will be performed directly,
     * with the user metadata in the Header ignored ; if it is marked as Replaced,
     * the metadata will be modified based on the Header information.
     * If the destination path and the source path are the same, that is,
     * the user attempts to modify the metadata, the value must be Replaced
     * @param metaDataDirective
     */
    public void setCopyMetaDataDirective(MetaDataDirective metaDataDirective){
        if(metaDataDirective != null){
            requestHeaders.put("x-cos-metadata-directive", metaDataDirective.getMetaDirective());
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

    /**
     * storage class. Enumerated values: Standard, Standard_IA, Nearline; the default is Standard
     * @param cosStorageClass
     */
    public void setCosStorageClass(COSStorageClass cosStorageClass){
        if(cosStorageClass != null){
            requestHeaders.put("x-cos-storage-class", cosStorageClass.getStorageClass());
        }
    }

    /**
     * Allow users to define file permissions.
     * valid values: private, public-read. The default is private.
     */
    public void setXCOSACL(COSACL cosacl){
        if(cosacl != null){
            requestHeaders.put("x-cos-acl", cosacl.getACL());
        }
    }

    /**
     * Grant read permission to the authorized user
     * uin = "uin/OwnerUin:uin/SubUin"
     * @param uinList
     */
    public void setXCOSGrantRead(List<String> uinList){
        setXCOSGrant(uinList, 0);
    }

    /**
     * Grant write permission to the authorized user
     * uin = "uin/OwnerUin:uin/SubUin"
     * @param uinList
     */
    public void setXCOSGrantWrite(List<String> uinList){
        setXCOSGrant(uinList, 1);
    }

    /**
     * Grant read and write permissions to the authorized user
     * uin = "uin/OwnerUin:uin/SubUin"
     * @param uinList
     */
    public void setXCOSReadWrite(List<String> uinList){
        setXCOSGrant(uinList, 2);
    }

    /**
     * @param uinList
     * @param grantType x-cos-grant-read(0), x-cos-grant-write(1), x-cos-grant-full-control(2)
     */
    private void setXCOSGrant(List<String> uinList, int grantType){
        String result = generateXCOSGrant(uinList);
        if(result != null){
            switch (grantType){
                case 0:
                    requestHeaders.put("x-cos-grant-read", result);
                    break;
                case 1:
                    requestHeaders.put("x-cos-grant-write", result);
                    break;
                case 2:
                    requestHeaders.put("x-cos-grant-full-control", result);
                    break;
            }
        }
    }
    private String generateXCOSGrant(List<String> uinList){
        String result = null;
        if(uinList != null){
            int size = uinList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size -1; ++ i){
                    stringBuilder.append("id=\"qcs::cam::")
                            .append(uinList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("id=\"qcs::cam::")
                        .append(uinList.get(size -1)).append("\"");
                result =  stringBuilder.toString();
            }
        }
        return result;
    }

    /**
     * custom file headers
     * @param key
     * @param value
     */
    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            requestHeaders.put(key,value);
        }
    }

    public static class CopySourceStruct{
        public String appid;
        public String bucket;
        public String region;
        public String cosPath;

        public CopySourceStruct(String appid, String bucket, String region, String cosPath){
            this.appid = appid;
            this.bucket = bucket;
            this.region = region;
            this.cosPath = cosPath;
        }

        public void checkParameters() throws CosXmlClientException {
            if(bucket == null){
                throw new CosXmlClientException("copy source bucket must not be null");
            }
            if(cosPath == null){
                throw new CosXmlClientException("copy source cosPath must not be null");
            }
            if(appid == null){
                throw new CosXmlClientException("copy source appid must not be null");
            }
            if(region == null){
                throw new CosXmlClientException("copy source region must not be null");
            }
            try {
                cosPath = URLEncodeUtils.cosPathEncode(cosPath);
            } catch (UnsupportedEncodingException e) {
               throw new CosXmlClientException(e);
            }
        }

        @Override
        public String toString(){
            if(cosPath != null){
                if(!cosPath.startsWith("/")){
                    cosPath = "/" + cosPath;
                }
            }
            StringBuilder copySource = new StringBuilder();
            if(bucket.endsWith("-" + appid)){
                copySource.append(bucket).append(".");
            }else {
                copySource.append(bucket).append("-")
                        .append(appid).append(".");
            }
            copySource.append("cos").append(".")
                    .append(region).append(".")
                    .append("myqcloud.com")
                    .append(cosPath);
            return copySource.toString();
        }
    }
}
