package com.tencent.cos.xml.model.object;

import android.util.Log;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.common.ResumeData;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.tag.Part;
import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by bradyxiao on 2017/7/21.
 * author bradyxiao
 */
public class MultipartUpload {

    private static final String TAG = "MultipartUpload";
    private String cosPath;
    private String srcPath;
    private String uploadId = null;
    private String bucket;
    private Map<Integer,PartStruct> partStructMap;
    private int sliceSize = 1024 * 1024;
    private long fileLength;
    private CosXmlService cosXmlService;
    private ResumeData resumeData;
    private volatile int UPLOAD_PART_COUNT;
    private volatile long ALREADY_SEND_DATA_LEN;
    private volatile int ERROR_EXIT_FLAG; // -1(init), 0(normal error),1(normal exception),2(manual cancel),3(abort)
    private QCloudProgressListener progressListener;
    private byte[] objectSync = new byte[0];
    private CosXmlResult mCosXmlResult;
    private Exception mException;
    private Map<UploadPartRequest,Long> uploadPartRequestLongMap;
    private InitMultipartUploadRequest mInitMultipartUploadRequest;
    private ListPartsRequest mListPartsRequest;
    private CompleteMultiUploadRequest mCompleteMultiUploadRequest;
    private long signExpiredTime = 600;
    private ExecutorService mExecutorService;

    public MultipartUpload(CosXmlService cosXmlService, ResumeData resumeData){
        this.cosXmlService = cosXmlService;
       // mExecutorService = Executors.newFixedThreadPool(3);
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
        UPLOAD_PART_COUNT = 0;
        ALREADY_SEND_DATA_LEN = 0;
        ERROR_EXIT_FLAG = -1;
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
     * 1) init ==> get a uploadID
     * 2) list ==> just for checking whether having some part data already uploaded.
     * 3) upload part data ==> upload data
     * 4) complete ==> mark it been upload completely.
     */
    public CosXmlResult upload2() throws QCloudException {
        initPartNumber();
        int httpCode = -1;
        if(this.uploadId == null){
            InitMultipartUploadResult initMultipartUploadResult = initMultiUpload();
            httpCode = initMultipartUploadResult.getHttpCode();

            if(httpCode < 200 || httpCode >= 300){
                return initMultipartUploadResult;
            }
            uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
            resumeData.uploadId = uploadId;
        }else{
            ListPartsResult listPartsResult = listPartUpload();
            httpCode = listPartsResult.getHttpCode();
            if(httpCode < 200 || httpCode >= 300){
                return listPartsResult;
            }
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
                        partStructMap.get(entry.getKey()).eTag = cosXmlResult.getETag();
                        UPLOAD_PART_COUNT --;
                    }

                    @Override
                    public void onFail(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                        synchronized (objectSync){
                            int httpCode2 = cosXmlResult.getHttpCode();
                            if(httpCode2 < 200 || httpCode2 >= 300){
                                mCosXmlResult = cosXmlResult;
                                ERROR_EXIT_FLAG = 0;
                            }else{
                                mException = new QCloudException(QCloudExceptionType.
                                        getQCloudExceptionType(cosXmlResult.getHttpCode() - 2500),
                                        cosXmlResult.error.message);
                                ERROR_EXIT_FLAG = 1;
                            }
                        }

                    }
                });
            }
        }

        while(UPLOAD_PART_COUNT > 0 && ERROR_EXIT_FLAG < 0);
        if(ERROR_EXIT_FLAG >= 0){
            switch (ERROR_EXIT_FLAG){
                case 2:
                    realCancel();
                    throw new QCloudException(QCloudExceptionType.REQUEST_USER_CANCELLED,
                            "request is cancelled by manual cancel");
                case 3:
                    realCancel();
                    throw new QCloudException(QCloudExceptionType.REQUEST_USER_CANCELLED,
                            "request is cancelled by abort request");
                case 0:
                    realCancel();
                    return mCosXmlResult;
                default:
                    realCancel();
                    if(mException != null){
                        throw (QCloudException)mException;
                    }else{
                        throw new QCloudException(QCloudExceptionType.UNDEFINE, "unknown exception");
                    }
            }
        }

        CompleteMultiUploadResult completeMultiUploadResult = completeMultiUpload();
        return completeMultiUploadResult;
    }


//    public CosXmlResult upload() throws QCloudException {
//        initPartNumber();
//        int httpCode = -1;
//        if(this.uploadId == null){
//            InitMultipartUploadResult initMultipartUploadResult = initMultiUpload();
//            httpCode = initMultipartUploadResult.getHttpCode();
//
//            if(httpCode < 200 || httpCode > 300){
//                return initMultipartUploadResult;
//            }
//            uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
//            resumeData.uploadId = uploadId;
//        }else{
//            ListPartsResult listPartsResult = listPartUpload();
//            httpCode = listPartsResult.getHttpCode();
//            if(httpCode < 200 || httpCode > 300){
//                return listPartsResult;
//            }
//            resumeData.uploadId = uploadId;
//            updatePartNumber(listPartsResult);
//        }
//
//        for(final Map.Entry<Integer, PartStruct> entry : partStructMap.entrySet()){
//            final PartStruct partStruct = entry.getValue();
//            if(!partStruct.alreadyUpload){
//                Log.w(TAG,"partStruct =" + partStruct.toString());
//                mExecutorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            UploadPartResult uploadPartResult = partUpload(partStruct.partNumber,
//                                    partStruct.offset,
//                                    partStruct.sliceSize);
//                            if(uploadPartResult != null){
//                                int httpCode2 = uploadPartResult.getHttpCode();
//                                if(httpCode2 < 200 || httpCode2 >= 300){
//                                    synchronized (objectSync){
//                                        mCosXmlResult = uploadPartResult;
//                                    }
//                                    ERROR_EXIT_FLAG = 0;
//                                }else {
//                                    partStructMap.get(entry.getKey()).eTag = uploadPartResult.getETag();
//                                    UPLOAD_PART_COUNT --;
//                                }
//                            }
//                        } catch (QCloudException e) {
//                            synchronized (objectSync){
//                                mException = e;
//                            }
//                            ERROR_EXIT_FLAG = 1;
//                        }
//                    }
//                });
//            }
//        }
//
//        while(UPLOAD_PART_COUNT > 0 && ERROR_EXIT_FLAG < 0);
//        if(ERROR_EXIT_FLAG >= 0){
//            mExecutorService.shutdownNow();
//            switch (ERROR_EXIT_FLAG){
//                case 2:
//                    realCancel();
//                    throw new QCloudException(QCloudExceptionType.REQUEST_USER_CANCELLED,
//                            "request is cancelled by manual cancel");
//                case 3:
//                    realCancel();
//                    throw new QCloudException(QCloudExceptionType.REQUEST_USER_CANCELLED,
//                            "request is cancelled by abort request");
//                case 0:
//                    realCancel();
//                    return mCosXmlResult;
//                default:
//                    realCancel();
//                    if(mException != null){
//                        throw (QCloudException)mException;
//                    }else{
//                        throw new QCloudException(QCloudExceptionType.UNDEFINE, "unknown exception");
//                    }
//            }
//        }
//
//        CompleteMultiUploadResult completeMultiUploadResult = completeMultiUpload();
//        return completeMultiUploadResult;
//    }

    protected InitMultipartUploadResult initMultiUpload() throws QCloudException {
        mInitMultipartUploadRequest = new InitMultipartUploadRequest();
        mInitMultipartUploadRequest.setCosPath(cosPath);
        mInitMultipartUploadRequest.setBucket(bucket);
        mInitMultipartUploadRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.initMultipartUpload(mInitMultipartUploadRequest);
    }

    protected ListPartsResult listPartUpload() throws QCloudException {
        mListPartsRequest = new ListPartsRequest();
        mListPartsRequest.setBucket(bucket);
        mListPartsRequest.setCosPath(cosPath);
        mListPartsRequest.setUploadId(uploadId);
        mListPartsRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.listParts(mListPartsRequest);
    }

    protected CompleteMultiUploadResult completeMultiUpload() throws QCloudException {
        mCompleteMultiUploadRequest = new CompleteMultiUploadRequest();
        mCompleteMultiUploadRequest.setBucket(bucket);
        mCompleteMultiUploadRequest.setUploadId(uploadId);
        mCompleteMultiUploadRequest.setCosPath(cosPath);
        for(Map.Entry<Integer,PartStruct> entry : partStructMap.entrySet()){
            PartStruct partStruct = entry.getValue();
            mCompleteMultiUploadRequest.setPartNumberAndETag(partStruct.partNumber,partStruct.eTag);
        }
        mCompleteMultiUploadRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.completeMultiUpload(mCompleteMultiUploadRequest);
    }

//    protected UploadPartResult partUpload(int partNumber, long offset, long fileContentLength) throws QCloudException {
//        final UploadPartRequest uploadPartRequest = new UploadPartRequest();
//        uploadPartRequest.setCosPath(cosPath);
//        uploadPartRequest.setBucket(bucket);
//        uploadPartRequest.setSign(signExpiredTime,null,null);
//        uploadPartRequest.setUploadId(uploadId);
//        uploadPartRequest.setPartNumber(partNumber);
//        uploadPartRequest.setSrcPath(srcPath,offset,fileContentLength);
//        uploadPartRequestLongMap.put(uploadPartRequest,0L);
//        uploadPartRequest.setProgressListener(new QCloudProgressListener() {
//            @Override
//            public void onProgress(long progress, long max) {
//                synchronized (objectSync){
//                    ALREADY_SEND_DATA_LEN = ALREADY_SEND_DATA_LEN  + (progress - uploadPartRequestLongMap.get(uploadPartRequest));
//                    uploadPartRequestLongMap.put(uploadPartRequest,progress);
//                    if(progressListener != null){
//                        progressListener.onProgress(ALREADY_SEND_DATA_LEN,fileLength);
//                    }
//                }
//            }
//        });
//        return cosXmlService.uploadPart(uploadPartRequest);
//    }

    protected void partUpload(final int partNumber, long offset, long fileContentLength, CosXmlResultListener resultListener) throws QCloudException {
        final UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setCosPath(cosPath);
        uploadPartRequest.setBucket(bucket);
        uploadPartRequest.setSign(signExpiredTime,null,null);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setPartNumber(partNumber);
        uploadPartRequest.setSrcPath(srcPath,offset,fileContentLength);
        uploadPartRequestLongMap.put(uploadPartRequest,0L);
        uploadPartRequest.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                synchronized (objectSync){
                    ALREADY_SEND_DATA_LEN = ALREADY_SEND_DATA_LEN  + (progress - uploadPartRequestLongMap.get(uploadPartRequest));
                    uploadPartRequestLongMap.put(uploadPartRequest,progress);
                    if(progressListener != null){
                        progressListener.onProgress(ALREADY_SEND_DATA_LEN,fileLength);
                    }
                }
            }
        });
        cosXmlService.uploadPartAsync(uploadPartRequest,resultListener);
    }

    //init -> partNumber
    protected void initPartNumber() throws QCloudException{
        if(srcPath != null){
            File file = new File(srcPath);
            if(!file.exists()){
                throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT,
                        "upload file does not exist");
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
            UPLOAD_PART_COUNT = count;
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
                        UPLOAD_PART_COUNT --;
                        ALREADY_SEND_DATA_LEN = ALREADY_SEND_DATA_LEN + Long.parseLong(part.size);
                    }
                }
            }
        }
    }

    private synchronized void realCancel(){
        if(mInitMultipartUploadRequest != null){
            cosXmlService.cancel(mInitMultipartUploadRequest);
        }
        mInitMultipartUploadRequest = null;
        if(mListPartsRequest != null){
            cosXmlService.cancel(mListPartsRequest);
        }
        mListPartsRequest = null;
        if(uploadPartRequestLongMap != null){
            Set<UploadPartRequest> set = uploadPartRequestLongMap.keySet();
            Iterator<UploadPartRequest> iterator = set.iterator();
            while(iterator.hasNext()){
                cosXmlService.cancel(iterator.next());
            }
            uploadPartRequestLongMap.clear();
        }
        uploadPartRequestLongMap = null;
        if(mCompleteMultiUploadRequest != null){
            cosXmlService.cancel(mCompleteMultiUploadRequest);
        }
        mCompleteMultiUploadRequest = null;
        if(partStructMap != null){
            partStructMap.clear();
        }
        partStructMap = null;
    }

    public ResumeData cancel(){
        ERROR_EXIT_FLAG = 2;
        return resumeData;
    }

    public void abortAsync(CosXmlResultListener cosXmlResultListener){
        ERROR_EXIT_FLAG = 3;
        final AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest();
        abortMultiUploadRequest.setBucket(bucket);
        abortMultiUploadRequest.setCosPath(cosPath);
        abortMultiUploadRequest.setUploadId(uploadId);
        abortMultiUploadRequest.setSign(signExpiredTime,null,null);
        cosXmlService.abortMultiUploadAsync(abortMultiUploadRequest, cosXmlResultListener);
    }

    public AbortMultiUploadResult abort() throws QCloudException {
        ERROR_EXIT_FLAG = 3;
        final AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest();
        abortMultiUploadRequest.setBucket(bucket);
        abortMultiUploadRequest.setCosPath(cosPath);
        abortMultiUploadRequest.setUploadId(uploadId);
        abortMultiUploadRequest.setSign(signExpiredTime,null,null);
        return cosXmlService.abortMultiUpload(abortMultiUploadRequest);
    }

    /**
     * Part struct for upload part object
     */
    protected class PartStruct{
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
}
