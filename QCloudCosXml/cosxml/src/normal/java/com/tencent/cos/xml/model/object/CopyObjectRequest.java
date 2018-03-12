package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSRequestHeaderKey;
import com.tencent.cos.xml.common.COSStorageClass;
import com.tencent.cos.xml.common.MetaDataDirective;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.utils.URLEncodeUtils;
import com.tencent.qcloud.core.http.RequestBodySerializer;


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
public class CopyObjectRequest extends ObjectRequest {

    // copy source struct
    private CopySourceStruct copySourceStruct;

    public CopyObjectRequest(String bucket, String cosPath, CopySourceStruct copySourceStruct) throws CosXmlClientException {
        super(bucket, cosPath);
        this.copySourceStruct = copySourceStruct;
        setCopySource(copySourceStruct);
    }

    @Override
    public String getMethod() {
        return RequestMethod.PUT;
    }

    @Override
    public RequestBodySerializer getRequestBody() throws CosXmlClientException {
        return RequestBodySerializer.bytes(null, new byte[0]);
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        super.checkParameters();
        if(copySourceStruct == null){
            throw new CosXmlClientException("copy source must not be null");
        }else {
            copySourceStruct.checkParameters();
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
    public void setCopySource(CopySourceStruct copySource) throws CosXmlClientException {
        this.copySourceStruct = copySource;
        if(copySourceStruct != null){
            addHeader(COSRequestHeaderKey.X_COS_COPY_SOURCE, copySourceStruct.getSource());
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
            addHeader(COSRequestHeaderKey.X_COS_METADATA_DIRECTIVE, metaDataDirective.getMetaDirective());
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
            addHeader(COSRequestHeaderKey.X_COS_COPY_SOURCE_IF_MODIFIED_SINCE, sourceIfModifiedSince);
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
            addHeader(COSRequestHeaderKey.X_COS_COPY_SOURCE_IF_UNMODIFIED_SINCE, sourceIfUnmodifiedSince);
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
            addHeader(COSRequestHeaderKey.X_COS_COPY_SOURCE_IF_MATCH, eTag);
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
            addHeader(COSRequestHeaderKey.X_COS_COPY_SOURCE_IF_NONE_MATCH, eTag);
        }
    }

    /**
     * storage class. Enumerated values: Standard, Standard_IA, Nearline; the default is Standard
     * @param cosStorageClass
     */
    public void setCosStorageClass(COSStorageClass cosStorageClass){
        if(cosStorageClass != null){
            addHeader(COSRequestHeaderKey.X_COS_STORAGE_CLASS_, cosStorageClass.getStorageClass());
        }
    }

    /**
     * Allow users to define file permissions.
     * valid values: private, public-read. The default is private.
     */
    public void setXCOSACL(COSACL cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL, cosacl.getAcl());
        }
    }

    public void setXCOSACL(String cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL, cosacl);
        }
    }

    /**
     * Grant read permission to the authorized user
     * @param aclAccount
     */
    public void setXCOSGrantRead(ACLAccount aclAccount){
        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_READ, aclAccount.getAccount());
        }
    }

    /**
     * Grant write permission to the authorized user
     * @param aclAccount
     */
    public void setXCOSGrantWrite(ACLAccount aclAccount){
        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_WRITE, aclAccount.getAccount());
        }
    }

    /**
     * Grant read and write permissions to the authorized user
     * @param aclAccount
     */
    public void setXCOSReadWrite(ACLAccount aclAccount){
        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_FULL_CONTROL, aclAccount.getAccount());
        }
    }

    /**
     * custom file requestHeaders
     * @param key
     * @param value
     */
    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            addHeader(key,value);
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
            cosPath = URLEncodeUtils.cosPathEncode(cosPath);
        }

        public String getSource() throws CosXmlClientException {
            if(cosPath != null){
                if(!cosPath.startsWith("/")){
                    cosPath = "/" + cosPath;
                }
            }
            cosPath = URLEncodeUtils.cosPathEncode(cosPath);
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
