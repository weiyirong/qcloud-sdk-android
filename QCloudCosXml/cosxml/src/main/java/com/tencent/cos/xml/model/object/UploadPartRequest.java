package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.request.serializer.RequestByteArraySerializer;
import com.tencent.qcloud.core.network.request.serializer.RequestFileBodySerializer;
import com.tencent.qcloud.core.network.request.serializer.RequestStreamBodySerializer;


import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>
 * 上传单个分块。
 * </p>
 *
 * <p>
 * 支持的块的数量为1到10000，块的大小为1 MB 到5 GB。
 * </p>
 * <p>
 * 当传入 uploadId 和 partNumber 都相同的时候，后传入的块将覆盖之前传入的块。当 uploadId 不存在时会返回 404 错误，NoSuchUpload.
 * </p>
 */
final public class UploadPartRequest extends CosXmlRequest {
    private int partNumber;
    private String uploadId;
    private String cosPath;
    private String srcPath;
    private byte[] data;
    private InputStream inputStream;
    private long fileLength;
    private long fileOffset;
    private long fileContentLength;

    private QCloudProgressListener progressListener;

    public UploadPartRequest(String bucket, String cosPath, int partNumber, String srcPath, String uploadId){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.partNumber = partNumber;
        this.srcPath = srcPath;
        this.uploadId = uploadId;
        contentType = QCloudNetWorkConstants.ContentType.TEXT_PLAIN;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        fileOffset = -1L;
        fileContentLength = -1L;
    }

    public UploadPartRequest(String bucket, String cosPath, int partNumber, String srcPath, long offset, long length,
                             String uploadId){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.partNumber = partNumber;
        setSrcPath(srcPath, offset, length);
        this.uploadId = uploadId;
        contentType = QCloudNetWorkConstants.ContentType.TEXT_PLAIN;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    public UploadPartRequest(String bucket, String cosPath,int partNumber, byte[] data, String uploadId){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.partNumber = partNumber;
        this.data = data;
        this.uploadId = uploadId;
        contentType = QCloudNetWorkConstants.ContentType.TEXT_PLAIN;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        fileOffset = -1L;
        fileContentLength = -1L;
    }

    public UploadPartRequest(String bucket, String cosPath, int partNumber, InputStream inputStream, long sendLength, String uploadId){
        setBucket(bucket);
        this.cosPath = cosPath;
        this.partNumber = partNumber;
        this.inputStream = inputStream;
        this.fileLength = sendLength;
        this.uploadId = uploadId;
        contentType = QCloudNetWorkConstants.ContentType.TEXT_PLAIN;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        fileOffset = -1L;
        fileContentLength = -1L;
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
            RequestFileBodySerializer requestFileBodySerializer = new RequestFileBodySerializer(srcPath,"text/plain", fileOffset, fileContentLength);
            requestFileBodySerializer.setProgressListener(progressListener);
            requestOriginBuilder.body(requestFileBodySerializer);
        }else if(data != null){
            RequestByteArraySerializer requestByteArraySerializer = new RequestByteArraySerializer(data,"text/plain");
            requestByteArraySerializer.setProgressListener(progressListener);
            requestOriginBuilder.body(requestByteArraySerializer);
        }else if(inputStream != null){
            RequestStreamBodySerializer requestStreamBodySerializer = new RequestStreamBodySerializer(inputStream, fileLength, "text/plain");
            requestStreamBodySerializer.setProgressListener(progressListener);
            requestOriginBuilder.body(requestStreamBodySerializer);
        }

        responseBodySerializer = new ResponseXmlS3BodySerializer(UploadPartResult.class);
    }

    /**
     * 设置上传的分块数
     *
     * @param partNumber 上传的分块数
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 获取用户设置的上传分块数
     *
     * @return 上传的分块数
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置分块上传的UploadId号
     *
     * @param uploadId 分块上传的UploadId
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 获取用户设置分块上传的UploadId号
     *
     * @return 分块上传的UploadId
     */
    public String getUploadId() {
        return uploadId;
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("partNumber", String.valueOf(partNumber));
        requestQueryParams.put("uploadID", uploadId);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
        if(partNumber <= 0){
            throw new CosXmlClientException("partNumber must be >= 1");
        }
        if(uploadId == null){
            throw new CosXmlClientException("uploadID must not be null");
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
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT ;
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
     * 设置分块上传的 COS 路径。
     *
     * @param cosPath COS上的路径
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
     * @see UploadPartRequest#setData(byte[])
     * @see UploadPartRequest#setInputStream(InputStream, long)
     */
    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * <p>
     * 设置上传的本地文件路径和上传范围
     * </p>
     *
     * @see UploadPartRequest#setSrcPath(String)
     */
    public void setSrcPath(String srcPath, long fileOffset, long contentLength) {
        this.srcPath = srcPath;
        this.fileOffset = fileOffset;
        this.fileContentLength = contentLength;
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
        if(inputStream != null){
            return fileLength;
        }

        if(data != null){
            return data.length;
        }

        if(srcPath != null){
            if(fileOffset != -1L){
                return  fileContentLength - fileOffset;
            }else{
                return new File(srcPath).length();
            }
        }

        return -1;
    }

    /**
     * 获取用户设置的进度监听
     *
     */
    public void setProgressListener(QCloudProgressListener progressListener){
        this.progressListener = progressListener;
    }
}
