package com.tencent.cos.xml.transfer;

import android.util.Log;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.common.ResumeData;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.tag.Part;
import com.tencent.qcloud.core.network.QCloudProgressListener;


import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bradyxiao on 2017/7/21.
 * author bradyxiao
 */
public class MultipartUpload {

    private static final String TAG = "MultipartUpload";
    private String cosPath;
    private String srcPath;
    private String uploadId;
    private String bucket;
    private Map<Integer,PartStruct> partStructMap;
    private int sliceSize = 1024 * 1024;
    private long fileLength;
    private CosXmlService cosXmlService;
    private ResumeData resumeData;
    private AtomicInteger UPLOAD_PART_COUNT;
    private AtomicLong ALREADY_SEND_DATA_LEN;
    private volatile int ERROR_EXIT_FLAG; //  0(init),1(normal exception),2(manual cancel),3(abort)
    private QCloudProgressListener progressListener;
    private byte[] objectSync = new byte[0];
    private CosXmlResult mCosXmlResult;
    private Exception mException;
    private Map<UploadPartRequest,Long> uploadPartRequestLongMap;
    private InitMultipartUploadRequest mInitMultipartUploadRequest;
    private ListPartsRequest mListPartsRequest;
    private CompleteMultiUploadRequest mCompleteMultiUploadRequest;
    private long signExpiredTime = 600;

    public MultipartUpload(CosXmlService cosXmlService, ResumeData resumeData){
        this.cosXmlService = cosXmlService;
        if(resumeData != null){
            this.resumeData = resumeData;
            this.bucket = resumeData.bucket;
            this.cosPath = resumeData.cosPath;
            this.uploadId = resumeData.uploadId;
            this.srcPath = resumeData.srcPath;
            this.sliceSize = resumeData.sliceSize;
            File file = new File(this.srcPath);
            fileLength = file.length();
        }else{
            this.resumeData = new ResumeData();
        }
        UPLOAD_PART_COUNT = new AtomicInteger(0);
        ALREADY_SEND_DATA_LEN = new AtomicLong(0);
        ERROR_EXIT_FLAG = 0;
        partStructMap = new LinkedHashMap<Integer, PartStruct>();
        uploadPartRequestLongMap = new LinkedHashMap<UploadPartRequest,Long>();
    }

    public CosXmlService getCosXmlService() {
        return cosXmlService;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
        resumeData.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

    public void setCosPath(String cosPath){
        this.cosPath = cosPath;
        resumeData.cosPath = cosPath;
    }

    public void setSrcPath(String srcPath){
        this.srcPath = srcPath;
        resumeData.srcPath = srcPath;
    }

    public String getCosPath() {
        return cosPath;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSliceSize(int sliceSize) {
        this.sliceSize = sliceSize;
        resumeData.sliceSize = sliceSize;
    }

    public long getSliceSize() {
        return sliceSize;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setSign(long signExpiredTime){
        this.signExpiredTime = signExpiredTime;
    }

    public QCloudProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * 1) init ==&gt;get a uploadID
     * 2) list ==&gt;just for checking whether having some part data already uploaded.
     * 3) upload part data ==&gt; upload data
     * 4) complete ==&gt; mark it been upload completely.
     */
    public CosXmlResult upload2() throws CosXmlClientException, CosXmlServiceException {
        initPartNumber();
        if(this.uploadId == null){
            InitMultipartUploadResult initMultipartUploadResult = initMultiUpload();
            uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
            resumeData.uploadId = uploadId;
        }else{
            ListPartsResult listPartsResult = listPartUpload();
            resumeData.uploadId = uploadId;
            updatePartNumber(listPartsResult);
        }
        for(final Map.Entry<Integer, PartStruct> entry : partStructMap.entrySet()){
            PartStruct partStruct = entry.getValue();
            if(!partStruct.alreadyUpload){
                Log.w(TAG,"partStruct =" + partStruct.toString());
                partUpload(partStruct.partNumber, partStruct.offset, partStruct.sliceSize, new CosXmlResultListener() {
                    @Override
                    public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                        synchronized (objectSync){
                            partStructMap.get(entry.getKey()).eTag = cosXmlResult.getETag();
                        }
                        UPLOAD_PART_COUNT.decrementAndGet();
                    }

                    @Override
                    public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException exception, CosXmlServiceException serviceException) {
                        synchronized (objectSync){
                            if(exception != null){
                                mException = exception;
                            }else{
                                mException = serviceException;
                            }
                            ERROR_EXIT_FLAG = 1;
                        }
                    }
                });
            }
        }

        while(UPLOAD_PART_COUNT.get() > 0 && ERROR_EXIT_FLAG == 0);

        if(ERROR_EXIT_FLAG > 0){
            switch (ERROR_EXIT_FLAG){
                case 2:
                    realCancel();
                    throw new CosXmlClientException("request is cancelled by manual cancel");
                case 3:
                    realCancel();
                    throw new CosXmlClientException("request is cancelled by abort request");
                case 1:
                    realCancel();
                    if(mException != null){
                        if(mException instanceof CosXmlClientException){
                            throw (CosXmlClientException)mException;
                        }
                        if(mException instanceof CosXmlServiceException){
                            throw (CosXmlServiceException)mException;
                        }
                    }else{
                        throw new CosXmlClientException("unknown exception");
                    }
            }
        }

        CompleteMultiUploadResult completeMultiUploadResult = completeMultiUpload();
        return completeMultiUploadResult;
    }



    protected InitMultipartUploadResult initMultiUpload() throws CosXmlClientException, CosXmlServiceException {
        mInitMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
        mInitMultipartUploadRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.initMultipartUpload(mInitMultipartUploadRequest);
    }

    protected ListPartsResult listPartUpload() throws CosXmlClientException, CosXmlServiceException {
        mListPartsRequest = new ListPartsRequest(bucket, cosPath, uploadId);
        mListPartsRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.listParts(mListPartsRequest);
    }

    protected CompleteMultiUploadResult completeMultiUpload() throws CosXmlClientException, CosXmlServiceException {
        mCompleteMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath, uploadId, null);
        for(Map.Entry<Integer,PartStruct> entry : partStructMap.entrySet()){
            PartStruct partStruct = entry.getValue();
            mCompleteMultiUploadRequest.setPartNumberAndETag(partStruct.partNumber,partStruct.eTag);
        }
        mCompleteMultiUploadRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.completeMultiUpload(mCompleteMultiUploadRequest);
    }


    protected void partUpload(final int partNumber, long offset, long fileContentLength, CosXmlResultListener resultListener) throws CosXmlClientException {
        final UploadPartRequest uploadPartRequest = new UploadPartRequest(bucket, cosPath, partNumber, srcPath, offset,
                fileContentLength, uploadId);
        uploadPartRequest.setSign(signExpiredTime,null,null);
        uploadPartRequestLongMap.put(uploadPartRequest,0L);
        uploadPartRequest.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                synchronized (objectSync){
                    long dataLen = ALREADY_SEND_DATA_LEN.addAndGet(progress - uploadPartRequestLongMap.get(uploadPartRequest));
                    uploadPartRequestLongMap.put(uploadPartRequest,progress);
                    if(progressListener != null){
                        progressListener.onProgress(dataLen,fileLength);
                    }
                }
            }
        });
        cosXmlService.uploadPartAsync(uploadPartRequest,resultListener);
    }

    //init -> partNumber
    protected void initPartNumber() throws CosXmlClientException {
        if(srcPath != null){
            File file = new File(srcPath);
            if(!file.exists()){
                throw new CosXmlClientException("upload file does not exist");
            }
            fileLength = file.length();
        }
        if(fileLength > 0 && sliceSize > 0){
            int count = (int) (fileLength/sliceSize);
            int i = 1;
            for(; i < count; ++ i){
                PartStruct partStruct = new PartStruct();
                partStruct.partNumber = i;
                partStruct.alreadyUpload = false;
                partStruct.offset = (i - 1) * sliceSize;
                partStruct.sliceSize = sliceSize;
                partStructMap.put(i, partStruct);
            }
            PartStruct partStruct = new PartStruct();
            partStruct.partNumber = i;
            partStruct.alreadyUpload = false;
            partStruct.offset = (i - 1) * sliceSize;
            partStruct.sliceSize = (int) (fileLength - partStruct.offset);
            partStructMap.put(i, partStruct);
            UPLOAD_PART_COUNT.set(i);
        }
    }

    //update -> partNumber by ListPartResult
    protected void updatePartNumber(ListPartsResult listPartsResult){
        if(listPartsResult != null && listPartsResult.listParts != null){
            if(listPartsResult.listParts.parts != null){
                List<Part> list = listPartsResult.listParts.parts;
                int size = list.size();
                Part part;
                for(int i = 0; i < size; ++ i){
                    part = list.get(i);
                    if(partStructMap.containsKey(Integer.valueOf(part.partNumber))){
                        PartStruct partStruct = partStructMap.get(Integer.valueOf(part.partNumber));
                        partStruct.alreadyUpload = true;
                        partStruct.eTag = part.eTag;
                        UPLOAD_PART_COUNT.decrementAndGet();
                        ALREADY_SEND_DATA_LEN.addAndGet(Long.parseLong(part.size));
                    }
                }
            }
        }
    }

    private synchronized void realCancel(){
        if(mInitMultipartUploadRequest != null){
            cosXmlService.cancel(mInitMultipartUploadRequest);
        }
        if(mListPartsRequest != null){
            cosXmlService.cancel(mListPartsRequest);
        }
        if(uploadPartRequestLongMap != null){
            Set<UploadPartRequest> set = uploadPartRequestLongMap.keySet();
            Iterator<UploadPartRequest> iterator = set.iterator();
            while(iterator.hasNext()){
                cosXmlService.cancel(iterator.next());
            }
        }

        if(mCompleteMultiUploadRequest != null){
            cosXmlService.cancel(mCompleteMultiUploadRequest);
        }
    }

    public ResumeData cancel(){
        ERROR_EXIT_FLAG = 2;
        return resumeData;
    }

    public void abortAsync(CosXmlResultListener cosXmlResultListener){
        ERROR_EXIT_FLAG = 3;
        final AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest(bucket,
                cosPath, uploadId);
        abortMultiUploadRequest.setSign(signExpiredTime,null,null);
        cosXmlService.abortMultiUploadAsync(abortMultiUploadRequest, cosXmlResultListener);
    }

    public AbortMultiUploadResult abort() throws CosXmlClientException, CosXmlServiceException {
        ERROR_EXIT_FLAG = 3;
        final AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest(bucket, cosPath, uploadId);
        abortMultiUploadRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.abortMultiUpload(abortMultiUploadRequest);
    }

    /**
     * Part struct for upload part object
     */
    protected static class PartStruct{
        public int partNumber;
        public boolean alreadyUpload;
        public String eTag;
        public long offset;
        public long sliceSize;

        @Override
        public String toString(){
            return "{partNumber :" + partNumber + "," +
                    "alreadyUpload :" + alreadyUpload + "," +
                    "eTag :" + eTag + "," +
                    "offset :" + offset + "," +
                    "sliceSize :" + sliceSize +
                    "}";
        }
    }

    public ResumeData getResumeData(){
        return resumeData;
    }
}
