package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.common.ResumeData;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.qcloud.network.QCloudProgressListener;

import com.tencent.qcloud.network.QCloudResultListener;
import com.tencent.qcloud.network.exception.QCloudException;


/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
public class MultipartUploadHelper {
    private static final String TAG = "MultipartUploadHelper";
    private MultipartUpload mCurrentMultipart;
    private long mSignExpiredTime = 600;
    private CosXmlService mCosXmlService;

    public MultipartUploadHelper(CosXmlService cosXmlService){
        this.mCosXmlService = cosXmlService;
        mCurrentMultipart = new MultipartUpload(cosXmlService, null);
    }

    public CosXmlService getCosXmlService() {
        return mCosXmlService;
    }

    public void setBucket(String bucket) {
        mCurrentMultipart.setBucket(bucket);
    }

    public String getBucket() {
        return mCurrentMultipart.getBucket();
    }

    public void setCosPath(String cosPath){
       mCurrentMultipart.setCosPath(cosPath);
    }

    public void setSrcPath(String srcPath){
        mCurrentMultipart.setSrcPath(srcPath);
    }

    public String getCosPath() {
        return mCurrentMultipart.getCosPath();
    }

    public String getSrcPath() {
        return mCurrentMultipart.getSrcPath();
    }

    public void setSliceSize(int sliceSize) {
        mCurrentMultipart.setSliceSize(sliceSize);
    }

    public long getSliceSize() {
        return mCurrentMultipart.getSliceSize();
    }

    public long getFileLength() {
        return mCurrentMultipart.getFileLength();
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        mCurrentMultipart.setProgressListener(progressListener);
    }

    public void setSign(long signExpiredTime){
        mSignExpiredTime = signExpiredTime;
        mCurrentMultipart.setSign(signExpiredTime);
    }

    public QCloudProgressListener getProgressListener() {
        return mCurrentMultipart.getProgressListener();
    }

    public CosXmlResult upload() throws QCloudException {
       return mCurrentMultipart.upload2();
    }

    public ResumeData cancel(){
        return mCurrentMultipart.cancel();
    }

    public void abortAsync(CosXmlResultListener cosXmlResultListener){
        mCurrentMultipart.abortAsync(cosXmlResultListener);
    }

    public AbortMultiUploadResult abort() throws QCloudException {
        return mCurrentMultipart.abort();
    }

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
}
