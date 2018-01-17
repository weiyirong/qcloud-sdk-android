package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSRequestHeaderKey;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.utils.FileUtils;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * 将本地的文件（Object）上传至指定 COS 中。
 * </p>
 *
 */
final public class PutObjectRequest extends ObjectRequest {
    private String srcPath;
    private byte[] data;
    private long fileLength;
    private CosXmlProgressListener progressListener;

    private PutObjectRequest(String bucket, String cosPath){
        super(bucket, cosPath);
    }
    public PutObjectRequest(String bucket, String cosPath, String srcPath){
        this(bucket, cosPath);
        this.srcPath = srcPath;
    }

    public PutObjectRequest(String bucket, String cosPath, byte[] data){
        this(bucket, cosPath);
        this.data = data;
    }

    public PutObjectRequest(String bucket, String cosPath, InputStream inputStream) throws CosXmlClientException {
        this(bucket, cosPath);
        this.srcPath = FileUtils.tempCache(inputStream);
    }

    @Override
    public String getMethod() {
        return RequestMethod.PUT;
    }


    @Override
    public RequestBodySerializer getRequestBody() throws CosXmlClientException {
        if(srcPath != null){
            return RequestBodySerializer.file(null, new File(srcPath));
        }else if(data != null){
            return RequestBodySerializer.bytes(null, data);
        }
        return null;
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        super.checkParameters();
        if(srcPath == null && data == null){
            throw new CosXmlClientException("Data Source must not be null");
        }
        if(srcPath != null){
            File file = new File(srcPath);
            if(!file.exists()){
                throw new CosXmlClientException("upload file does not exist");
            }
        }
    }


    /**
     * 设置上传进度监听器
     *
     * @param progressListener 进度监听器
     */
    public void setProgressListener(CosXmlProgressListener progressListener){
        this.progressListener = progressListener;
    }

    public CosXmlProgressListener getProgressListener(){
        return progressListener;
    }

    /**
     * <p>
     * 设置上传的本地文件路径
     * </p>
     * <p>
     * 可以设置上传本地文件、字节数组或者输入流。每次只能上传一种类型，若同时设置，
     * 则优先级为 本地文件&gt;字节数组&gt;输入流
     * </p>
     *
     * @param srcPath 本地文件路径
     * @see PutObjectRequest#setData(byte[])
     */
    public void setSrcPath(String srcPath){
        this.srcPath = srcPath;
    }

    /**
     * 获取设置的本地文件路径
     *
     * @return
     */
    public String getSrcPath(){
       return srcPath;
    }

    /**
     * <p>
     * 设置上传的字节数组
     * </p>
     * <p>
     * 可以设置上传本地文件、字节数组或者输入流。每次只能上传一种类型，若同时设置，
     * 则优先级为 本地文件&gt;字节数组&gt;输入流
     * </p>
     *
     * @param data 需要上传的字节数组
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * 获取用户设置的字节数组
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }


    /**
     * 获取用户设置的输入流读取的字节长度
     *
     * @return
     */
    public long getFileLength() {
        if(srcPath != null){
            fileLength = new File(srcPath).length();
        }else if(data != null){
            fileLength =  data.length;
        }
        return fileLength;
    }

    /**
     * <p>
     * 设置缓存策略
     * </p>
     * <p>
     * 即RFC 2616 中定义的Cache-Control头部
     * </p>
     *
     * @param cacheControl Cache-Control头部
     */
    public void setCacheControl(String cacheControl) {
        if(cacheControl == null)return;
        addHeader(COSRequestHeaderKey.CACHE_CONTROL,cacheControl);
    }

    /**
     * <p>
     * 设置文件名称。
     * </p>
     * <p>
     * 即RFC 2616 中定义的Content-Disposition头部
     * </p>
     *
     * @param contentDisposition Content-Disposition头部
     */
    public void setContentDisposition(String contentDisposition) {
        if(contentDisposition == null)return;
       addHeader(COSRequestHeaderKey.CONTENT_DISPOSITION,contentDisposition);
    }

    /**
     * <p>
     * 设置编码格式。
     * </p>
     * <p>
     * 即RFC 2616 中定义的Content-Encoding头部
     * </p>
     *
     * @param contentEncodeing Content-Encoding头部
     */
    public void setContentEncodeing(String contentEncodeing) {
        if(contentEncodeing == null)return;
        addHeader(COSRequestHeaderKey.CONTENT_ENCODING,contentEncodeing);
    }

    /**
     * <p>
     * 设置过期时间。
     * </p>
     * <p>
     * 即RFC 2616 中定义的Expires头部
     * </p>
     *
     * @param expires Expires头部
     */
    public void setExpires(String expires) {
        if(expires == null)return;
        addHeader(COSRequestHeaderKey.EXPIRES,expires);
    }

    /**
     * <p>
     * 自定义的头部信息
     * </p>
     * <p>
     * key需要以x-cos-meta-开头
     * </p>
     *
     * @param key 自定义头部key，需要以x-cos-meta-开头
     * @param value 自定义头部value
     */
    public void setXCOSMeta(String key, String value){
        if(key != null && value != null){
            addHeader(key,value);
        }
    }

    /**
     * <p>
     * 设置Bucket访问权限
     * </p>
     *
     * <br>
     * 有效值：
     * <ul>
     * <li>private ：私有，默认值</li>
     * <li>public-read ：公有读</li>
     * <li>public-read-write ：公有读写</li>
     * </ul>
     * <br>
     *
     * @param cosacl acl字符串
     */
    public void setXCOSACL(COSACL cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL,cosacl.getAcl());
        }
    }


    /**
     * 设置Bucket的ACL信息
     *
     * @param cosacl acl枚举
     */
    public void setXCOSACL(String cosacl){
        if(cosacl != null){
            addHeader(COSRequestHeaderKey.X_COS_ACL,cosacl);
        }
    }

    /**
     * <p>
     * 单独明确赋予用户读权限
     * </p>
     *
     * @param aclAccount 读权限用户列表
     */
    public void setXCOSGrantRead(ACLAccount aclAccount){
        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_READ, aclAccount.getAccout());
        }
    }


    /**
     * <p>
     * 赋予被授权者写的权限
     * </p>
     *
     * @param aclAccount 写权限用户列表
     */
    public void setXCOSGrantWrite(ACLAccount aclAccount){

        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_WRITE, aclAccount.getAccout());
        }
    }

    /**
     * <p>
     * 赋予被授权者读写权限。
     * </p>
     *
     * @param aclAccount 读写用户权限列表
     */
    public void setXCOSReadWrite(ACLAccount aclAccount){

        if (aclAccount != null) {
            addHeader(COSRequestHeaderKey.X_COS_GRANT_FULL_CONTROL, aclAccount.getAccout());
        }
    }

}
