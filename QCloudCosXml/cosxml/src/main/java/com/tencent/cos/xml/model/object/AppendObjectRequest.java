package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.ACLAccounts;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.request.serializer.RequestByteArraySerializer;
import com.tencent.qcloud.core.network.request.serializer.RequestFormDataSerializer;
import com.tencent.qcloud.core.network.request.serializer.RequestStreamBodySerializer;

import java.io.File;
import java.io.InputStream;

import java.util.Map;


/**
 * <p>
 * 追加上传的对象。
 * </p>
 *
 * <p>
 * 追加上传的对象每个分块最小为 4K，建议大小 1M-5G。如果 Position 的值和当前对象的长度不致，COS 会返回 409 错误。
 * 如果追加一个 normal 属性的文件，COS 会返回 409 ObjectNotAppendable。
 * </p>
 * <p>
 * appendable 的对象不可以被复制，不参与版本管理，不参与生命周期管理，不可跨区域复制。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#appendObject(AppendObjectRequest)
 * @see com.tencent.cos.xml.CosXml#appendObjectAsync(AppendObjectRequest, CosXmlResultListener)
 */
final public class AppendObjectRequest extends CosXmlRequest {

    /** 追加的起始点 */
    private long position = 0;
    private String cosPath;
    private String srcPath;
    private byte[] data;
    private InputStream inputStream;
    private long fileLength;

    private QCloudProgressListener progressListener;

    public AppendObjectRequest(String bucket, String cosPath, String srcPath, long position){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.srcPath = srcPath;
        this.position = position;
        contentType = QCloudNetWorkConstants.ContentType.MULTIPART_FORM_DATA;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    public AppendObjectRequest(String bucket, String cosPath, byte[] data, long position){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.data = data;
        this.position = position;
        contentType = QCloudNetWorkConstants.ContentType.MULTIPART_FORM_DATA;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    public AppendObjectRequest(String bucket, String cosPath, InputStream inputStream, long sendLength, long position){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.inputStream = inputStream;
        this.fileLength = sendLength;
        this.position = position;
        contentType = QCloudNetWorkConstants.ContentType.MULTIPART_FORM_DATA;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    @Override
    protected void build() throws CosXmlClientException {
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_LOW;

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

        if(srcPath != null){
            RequestFormDataSerializer requestFormDataSerializer = new RequestFormDataSerializer();
            requestFormDataSerializer.setProgressListener(progressListener);
            requestFormDataSerializer.uploadFile(srcPath,"file");
            requestOriginBuilder.body(requestFormDataSerializer);
        }else if(data != null){
            RequestByteArraySerializer requestByteArraySerializer = new RequestByteArraySerializer(data,"text/plain");
            requestByteArraySerializer.setProgressListener(progressListener);
            requestOriginBuilder.body(requestByteArraySerializer);
        }else if(inputStream != null){
            RequestStreamBodySerializer requestStreamBodySerializer = new RequestStreamBodySerializer(inputStream, fileLength, "text/plain");
            requestStreamBodySerializer.setProgressListener(progressListener);
            requestOriginBuilder.body(requestStreamBodySerializer);
        }

        responseBodySerializer = new ResponseXmlS3BodySerializer(AppendObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("append",null);
        requestQueryParams.put("position",String.valueOf(position));
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
        if(cosPath == null){
            throw new CosXmlClientException("cosPath must not be null");
        }
        if(srcPath == null && data == null && inputStream == null){
            throw new CosXmlClientException("Data Source must not be null");
        }
        if(srcPath != null){
            File file = new File(srcPath);
            if(!file.exists()){
                throw new CosXmlClientException("upload file does not exist");
            }
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.POST;
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
     * 追加操作的起始点，单位：字节；
     * 首次追加 position=0，后续追加 position= 当前 Object 的 content-length
     *
     * @param position
     */
    public void setPosition(long position) {
        if(position < 0)this.position = 0;
        this.position = position;
    }

    /**
     * 获取设置的追加操作起点
     *
     * @return
     */
    public long getPosition() {
        return position;
    }

    /**
     *
     * 设置COS上对应的路径。
     *
     * @param cosPath COS上对应的路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取设置的COS上对应的路径。
     *
     * @return COS上对应的路径
     */
    public String getCosPath() {
        return cosPath;
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
     * @see AppendObjectRequest#setData(byte[])
     * @see AppendObjectRequest#setInputStream(InputStream, long)
     */
    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * 获取设置的本地文件路径
     *
     * @return
     */
    public String getSrcPath() {
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
     * <p>
     * 设置上传的输入流
     * </p>
     * <p>
     * 可以设置上传本地文件、字节数组或者输入流。每次只能上传一种类型，若同时设置，
     * 则优先级为 本地文件&gt;字节数组&gt;输入流
     * </p>
     *
     * @param inputStream 输入流
     * @param fileLength 读取的字节长度
     */
    public void setInputStream(InputStream inputStream, long fileLength) {
        this.inputStream = inputStream;
        this.fileLength = fileLength;
    }

    /**
     * 获取用户设置的输入流
     *
     * @return
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 获取用户设置的输入流读取的字节长度
     *
     * @return
     */
    public long getFileLength() {
        return fileLength;
    }

    /**
     * 设置上传进度监听
     *
     * @param progressListener
     */
    public void setProgressListener(QCloudProgressListener progressListener){
        this.progressListener = progressListener;
    }

    /**
     * 获取用户设置的进度监听
     *
     * @return
     */
    public QCloudProgressListener getProgressListener() {
        return progressListener;
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
        requestHeaders.put("Cache-Control",cacheControl);
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
        requestHeaders.put("Content-Disposition",contentDisposition);
    }

    /**
     * <p>
     * 设置编码格式。
     * </p>
     * <p>
     * 即RFC 2616 中定义的Content-Encoding头部
     * </p>
     *
     * @param contentEncoding Content-Encoding头部
     */
    public void setContentEncodeing(String contentEncoding) {
        if(contentEncoding == null)return;
        requestHeaders.put("Content-Encoding",contentEncoding);
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
        requestHeaders.put("Expires", expires);
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
            requestHeaders.put(key,value);
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
    public void setXCOSACL(String cosacl){
        if(cosacl != null){
            requestHeaders.put("x-cos-acl",cosacl);
        }
    }

    /**
     * 设置Bucket的ACL信息
     *
     * @param cosacl acl枚举
     */
    public void setXCOSACL(COSACL cosacl){
        if(cosacl != null){
            requestHeaders.put("x-cos-acl",cosacl.getACL());
        }
    }

    /**
     * <p>
     * 单独明确赋予用户读权限
     * </p>
     *
     * @param aclAccounts 读权限用户列表
     */
    public void setXCOSGrantRead(ACLAccounts aclAccounts){
        if (aclAccounts != null) {
            requestHeaders.put(RequestHeader.X_COS_GRANT_READ, aclAccounts.aclDesc());
        }
    }


    /**
     * <p>
     * 赋予被授权者写的权限
     * </p>
     *
     * @param aclAccounts 写权限用户列表
     */
    public void setXCOSGrantWrite(ACLAccounts aclAccounts){

        if (aclAccounts != null) {
            requestHeaders.put(RequestHeader.X_COS_GRANT_WRITE, aclAccounts.aclDesc());
        }
    }


    /**
     * <p>
     * 赋予被授权者读写权限。
     * </p>
     *
     * @param aclAccounts 读写用户权限列表
     */
    public void setXCOSReadWrite(ACLAccounts aclAccounts){

        if (aclAccounts != null) {
            requestHeaders.put(RequestHeader.X_COS_GRANT_FULL_CONTROL, aclAccounts.aclDesc());
        }
    }
}
