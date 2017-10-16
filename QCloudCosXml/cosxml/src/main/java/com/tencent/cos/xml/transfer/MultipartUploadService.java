package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXml;
import com.tencent.cos.xml.common.ResumeData;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.ListMultiUploadsRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.qcloud.core.network.QCloudProgressListener;


/**
 *
 * MultipartUploadHelper封装了底层分片上传的相关接口，给您提供了更加方便的分片上传方法。
 *
 * 分片上传文件
 *
 * <pre>
 *
 * MultipartUploadService helper = new MultipartUploadService(cosXmlService);
 * helper.setBucket(bucketName);
 * helper.setCosPath(cosPath); // 需要以 '/' 开头
 * helper.setSrcPath(srcPath); // 本地文件路径
 * helper.setProgressListener(new QCloudProgressListener() {
 *
 *   public void onProgress(long progress, long total) {
 *     Log.d("TAG", "progress is " + progress + ", total is " + total);
 *   }
 * });
 *
 * helper.upload();
 *
 * </pre>
 *
 * 暂停和恢复分片上传
 *
 * <pre>
 *
 * // 暂停任务
 * ResumeData resumeData = helper.cancel();
 *
 * // 恢复任务
 * CosXmlResult cosXmlResult = helper.resume(resumeData);
 *
 * // 删除任务
 * AbortMultiUploadResult abortMultiUploadResult = helper.abort();
 *
 * </pre>
 *
 * @see CosXml#initMultipartUpload(InitMultipartUploadRequest)
 * @see CosXml#uploadPart(UploadPartRequest)
 * @see CosXml#listMultiUploads(ListMultiUploadsRequest)
 * @see CosXml#completeMultiUpload(CompleteMultiUploadRequest)
 * @see CosXml#abortMultiUpload(AbortMultiUploadRequest)
 *
 */
public class MultipartUploadService {


    private static final String TAG = "MultipartUploadService";
    private MultipartUpload mCurrentMultipart;
    private long mSignExpiredTime = 600;
    private CosXmlService mCosXmlService;

    public MultipartUploadService(CosXmlService cosXmlService){
        this.mCosXmlService = cosXmlService;
        mCurrentMultipart = new MultipartUpload(cosXmlService, null);
    }

    public CosXmlService getCosXmlService() {
        return mCosXmlService;
    }

    /**
     * 设置Bucket
     *
     * @param bucket bucket名称
     */
    public void setBucket(String bucket) {
        mCurrentMultipart.setBucket(bucket);
    }

    /**
     * 获取用户设置的Bucket
     *
     * @return bucket名称
     */
    public String getBucket() {
        return mCurrentMultipart.getBucket();
    }

    /**
     * 设置上传的 COS 路径
     *
     * @param cosPath COS 路径
     */
    public void setCosPath(String cosPath){
       mCurrentMultipart.setCosPath(cosPath);
    }

    /**
     * 设置上传的本地文件路径
     *
     * @param srcPath 本地文件路径
     */
    public void setSrcPath(String srcPath){
        mCurrentMultipart.setSrcPath(srcPath);
    }

    /**
     * 获取用户设置的 COS 路径
     *
     * @return COS 路径
     */
    public String getCosPath() {
        return mCurrentMultipart.getCosPath();
    }

    /**
     * 获取上传的本地路径
     *
     * @return 上传的本地路径
     */
    public String getSrcPath() {
        return mCurrentMultipart.getSrcPath();
    }

    /**
     * 设置分片上传的分片大小
     *
     * @param sliceSize 分片大小
     */
    public void setSliceSize(int sliceSize) {
        mCurrentMultipart.setSliceSize(sliceSize);
    }

    /**
     * 获取用户设置的分片大小
     *
     * @return
     */
    public long getSliceSize() {
        return mCurrentMultipart.getSliceSize();
    }

    /**
     * 获取上传本地文件的大小
     *
     * @return 本地文件大小
     */
    public long getFileLength() {
        return mCurrentMultipart.getFileLength();
    }

    /**
     * 设置进度监听
     *
     * @param progressListener
     */
    public void setProgressListener(QCloudProgressListener progressListener) {
        mCurrentMultipart.setProgressListener(progressListener);
    }

    /**
     * 设置签名
     *
     * @param signExpiredTime 有效期，单位s
     */
    public void setSign(long signExpiredTime){
        mSignExpiredTime = signExpiredTime;
        mCurrentMultipart.setSign(signExpiredTime);
    }

    /**
     * 获取设置的进度监听
     *
     * @return 进度监听
     */
    public QCloudProgressListener getProgressListener() {
        return mCurrentMultipart.getProgressListener();
    }

    /**
     * 开始分片上传
     *
     * @return 分片上传结果
     * @throws CosXmlClientException 上传出错，抛出异常
     */
    public CosXmlResult upload() throws CosXmlClientException, CosXmlServiceException {
       return mCurrentMultipart.upload2();
    }

    /**
     * <p>
     * 取消分片上传
     * </p>
     * <p>
     * 调用{@link MultipartUploadService#cancel()} 后会返回{@link ResumeData}，然后可以调用{@link MultipartUploadService#resume(ResumeData)}恢复上传。
     * </p>
     *
     * @return 恢复分片上传时需要的ResumeData
     */
    public ResumeData cancel(){
        return mCurrentMultipart.cancel();
    }

    /**
     * <p>
     * 异步删除分片上传。
     * </p>
     *
     * @param cosXmlResultListener
     * @see MultipartUploadService#abortAsync(CosXmlResultListener)
     */
    public void abortAsync(CosXmlResultListener cosXmlResultListener){
        mCurrentMultipart.abortAsync(cosXmlResultListener);
    }

    /**
     * 同步删除分片上传。
     *
     * @return 分片上传删除结果
     * @throws CosXmlClientException
     * @see MultipartUploadService#abort()
     */
    public AbortMultiUploadResult abort() throws CosXmlClientException, CosXmlServiceException {
        return mCurrentMultipart.abort();
    }

    /**
     * 恢复分片上传
     *
     * @param resumeData 恢复参数
     * @return 分片上传的结果
     * @throws Exception
     */
    public CosXmlResult resume(ResumeData resumeData) throws Exception {
        if(resumeData != null){
            MultipartUpload mNextMultipartUpload = new MultipartUpload(mCosXmlService, resumeData);
            mNextMultipartUpload.setProgressListener(mCurrentMultipart.getProgressListener());
            mNextMultipartUpload.setSign(mSignExpiredTime);
            mCurrentMultipart = mNextMultipartUpload;
            return mNextMultipartUpload.upload2();
        }else {
            return null;
        }
    }

    public ResumeData getResumeDate(){
        return mCurrentMultipart.getResumeData();
    }
}
