package com.tencent.cos.xml.transfer;


import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.tag.ListParts;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bradyxiao on 2017/12/4.
 */

public class UploadService {

    private CosXmlSimpleService cosXmlService;
    private String bucket;
    private String cosPath;
    private String srcPath;
    private long sliceSize = 1024 * 1024 * 2;
    private String uploadId;
    private long fileLength;
    private static final long SIZE_LIMIT = 2 * 1024 * 1024;
    private CosXmlProgressListener cosXmlProgressListener;
    private Map<Integer, SlicePartStruct> partStructMap;
    private AtomicInteger UPLOAD_PART_COUNT;
    private AtomicLong ALREADY_SEND_DATA_LEN;
    private volatile int ERROR_EXIT_FLAG; //  0(init),1(normal exception),2(manual pause),3(abort)
    private byte[] objectSync = new byte[0];
    private Exception mException;
    private Map<UploadPartRequest,Long> uploadPartRequestLongMap;
    private InitMultipartUploadRequest initMultipartUploadRequest;
    private ListPartsRequest listPartsRequest;
    private CompleteMultiUploadRequest completeMultiUploadRequest;
    private PutObjectRequest putObjectRequest;
    private UploadServiceResult uploadServiceResult;

    public UploadService(CosXmlSimpleService cosXmlService, ResumeData resumeData){
        this.cosXmlService = cosXmlService;
        init(resumeData);
    }

    private void init(ResumeData resumeData){
        bucket = resumeData.bucket;
        cosPath = resumeData.cosPath;
        srcPath = resumeData.srcPath;
        sliceSize = resumeData.sliceSize;
        uploadId = resumeData.uploadId;
        UPLOAD_PART_COUNT = new AtomicInteger(0);
        ALREADY_SEND_DATA_LEN = new AtomicLong(0);
        ERROR_EXIT_FLAG = 0;
        partStructMap = new LinkedHashMap<Integer, SlicePartStruct>();
        uploadPartRequestLongMap = new LinkedHashMap<UploadPartRequest, Long>();
    }

    private void checkParameter() throws CosXmlClientException {
        if(srcPath != null){
            File file = new File(srcPath);
            if(file.exists()){
                fileLength = file.length();
                return;
            }
        }
        throw new CosXmlClientException("srcPath :" + srcPath + " is invalid or is not exist");
    }
    public UploadServiceResult upload() throws CosXmlClientException, CosXmlServiceException {
        checkParameter();
        if(fileLength < SIZE_LIMIT){
            return putObject(bucket, cosPath, srcPath);
        }else {
            return multiUploadParts();
        }
    }

    public CosXmlResult resume(ResumeData resumeData) throws CosXmlServiceException, CosXmlClientException {
        init(resumeData);
        return upload();
    }

    public ResumeData pause(){
        ERROR_EXIT_FLAG = 2;
        ResumeData resumeData = new ResumeData();
        resumeData.bucket = bucket;
        resumeData.cosPath = cosPath;
        resumeData.sliceSize = sliceSize;
        resumeData.srcPath = srcPath;
        resumeData.uploadId = uploadId;
        return resumeData;
    }

    public void abort(CosXmlResultListener cosXmlResultListener){
        ERROR_EXIT_FLAG = 3;
        abortMultiUpload(cosXmlResultListener);
    }

    private void clear(){
        putObjectRequest = null;
        initMultipartUploadRequest = null;
        listPartsRequest = null;
        completeMultiUploadRequest = null;
        partStructMap.clear();
        uploadPartRequestLongMap.clear();
    }

    public void setProgressListener(CosXmlProgressListener cosXmlProgressListener){
        this.cosXmlProgressListener = cosXmlProgressListener;
    }

    /**
     * small file using put object api
     */
    private UploadServiceResult putObject(final String bucket, String cosPath, String srcPath) throws CosXmlClientException, CosXmlServiceException {
        UPLOAD_PART_COUNT.set(1);
        putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        putObjectRequest.setProgressListener(cosXmlProgressListener);
        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                synchronized (objectSync){
                    PutObjectResult putObjectResult = (PutObjectResult) result;
                    if(uploadServiceResult == null)uploadServiceResult = new UploadServiceResult();
                    uploadServiceResult.httpCode = putObjectResult.httpCode;
                    uploadServiceResult.httpMessage = putObjectResult.httpMessage;
                    uploadServiceResult.headers = putObjectResult.headers;
                    uploadServiceResult.eTag = putObjectResult.eTag;
                }
                UPLOAD_PART_COUNT.decrementAndGet();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
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

        //wait upload parts complete.
        while (UPLOAD_PART_COUNT.get() > 0 && ERROR_EXIT_FLAG == 0);
        //if error throw exception
        if(ERROR_EXIT_FLAG > 0){
            switch (ERROR_EXIT_FLAG){
                case 2:
                    realCancel();
                    clear();
                    throw new CosXmlClientException("request is cancelled by manual pause");
                case 3:
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

        uploadServiceResult.accessUrl = cosXmlService.getAccessUrl(putObjectRequest);
        return uploadServiceResult;
    }

    private UploadServiceResult multiUploadParts() throws CosXmlClientException, CosXmlServiceException {
        initSlicePart();
        if(uploadId != null){
            ListPartsResult listPartsResult = listPart();
            //breakpoint transmission
            updateSlicePart(listPartsResult);
        }else {
            InitMultipartUploadResult initMultipartUploadResult = initMultiUpload();
            uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
        }
        for(final Map.Entry<Integer, SlicePartStruct> entry : partStructMap.entrySet()){
            final SlicePartStruct slicePartStruct = entry.getValue();
            if(!slicePartStruct.isAlreadyUpload){
                uploadPart(slicePartStruct.partNumber, slicePartStruct.offset, slicePartStruct.sliceSize,
                        new CosXmlResultListener() {
                            @Override
                            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                                synchronized (objectSync){
                                    slicePartStruct.eTag = ((UploadPartResult)result).eTag;
                                    slicePartStruct.isAlreadyUpload = true;
                                }
                                UPLOAD_PART_COUNT.decrementAndGet();
                            }

                            @Override
                            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
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

        //wait upload parts complete.
        while (UPLOAD_PART_COUNT.get() > 0 && ERROR_EXIT_FLAG == 0);
        //if error throw exception
        if(ERROR_EXIT_FLAG > 0){
            switch (ERROR_EXIT_FLAG){
                case 2:
                    realCancel();
                    clear();
                    throw new CosXmlClientException("request is cancelled by manual pause");
                case 3:
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
        if(uploadServiceResult == null)uploadServiceResult = new UploadServiceResult();
        uploadServiceResult.httpCode = completeMultiUploadResult.httpCode;
        uploadServiceResult.httpMessage = completeMultiUploadResult.httpMessage;
        uploadServiceResult.headers = completeMultiUploadResult.headers;
        uploadServiceResult.eTag = completeMultiUploadResult.completeMultipartUpload.eTag;
        uploadServiceResult.accessUrl = cosXmlService.getAccessUrl(completeMultiUploadRequest);
        return uploadServiceResult;
    }

    /**
     * init multi,then get uploadId
     */
    private InitMultipartUploadResult initMultiUpload() throws CosXmlServiceException, CosXmlClientException {
        initMultipartUploadRequest = new InitMultipartUploadRequest(bucket,
                cosPath);
        return cosXmlService.initMultipartUpload(initMultipartUploadRequest);
    }

    /**
     * List Parts, check which parts have been uploaded.
     */
    private ListPartsResult listPart() throws CosXmlServiceException, CosXmlClientException {
        listPartsRequest = new ListPartsRequest(bucket, cosPath, uploadId);
        return cosXmlService.listParts(listPartsRequest);
    }

    /**
     * upload Part,  concurrence upload file parts.
     */
    private void uploadPart(final int partNumber, long offset, long contentLength, CosXmlResultListener cosXmlResultListener){
        final UploadPartRequest uploadPartRequest = new UploadPartRequest(bucket, cosPath, partNumber,
                srcPath, offset, contentLength, uploadId);
        uploadPartRequestLongMap.put(uploadPartRequest, 0L);
        uploadPartRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                synchronized (objectSync){
                    long dataLen = ALREADY_SEND_DATA_LEN.addAndGet(complete - uploadPartRequestLongMap.get(uploadPartRequest));
                    uploadPartRequestLongMap.put(uploadPartRequest, complete);
                    if(cosXmlProgressListener != null){
                        cosXmlProgressListener.onProgress(dataLen,fileLength);
                    }
                }
            }
        });
        cosXmlService.uploadPartAsync(uploadPartRequest, cosXmlResultListener);
    }

    /**
     * complete multi upload.
     */
    private CompleteMultiUploadResult completeMultiUpload() throws CosXmlServiceException, CosXmlClientException {
        completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath,
                uploadId, null);
        for(Map.Entry<Integer, SlicePartStruct> entry : partStructMap.entrySet()){
            SlicePartStruct slicePartStruct = entry.getValue();
            completeMultiUploadRequest.setPartNumberAndETag(slicePartStruct.partNumber, slicePartStruct.eTag);
        }
        return cosXmlService.completeMultiUpload(completeMultiUploadRequest);
    }

    /**
     * abort multi upload
     */
    private void abortMultiUpload(final CosXmlResultListener cosXmlResultListener){
        if(uploadId == null) return;
        AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest(bucket, cosPath,
                uploadId);
        cosXmlService.abortMultiUploadAsync(abortMultiUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                cosXmlResultListener.onSuccess(request, result);
                realCancel();
                clear();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                cosXmlResultListener.onFail(request, exception, serviceException);
                realCancel();
                clear();
            }
        });
    }

    private void realCancel(){
        cosXmlService.cancel(putObjectRequest);
        cosXmlService.cancel(initMultipartUploadRequest);
        cosXmlService.cancel(listPartsRequest);
        cosXmlService.cancel(completeMultiUploadRequest);
        if(uploadPartRequestLongMap != null){
            Set<UploadPartRequest> set = uploadPartRequestLongMap.keySet();
            Iterator<UploadPartRequest> iterator = set.iterator();
            while(iterator.hasNext()){
                cosXmlService.cancel(iterator.next());
            }
        }
    }

    /**
     * init slice part
     */
    private void initSlicePart() throws CosXmlClientException {
        if(srcPath != null){
            File file = new File(srcPath);
            if(!file.exists()){
                throw new CosXmlClientException("upload file does not exist");
            }
            fileLength = file.length();
        }
        if(fileLength > 0 && sliceSize > 0){
            int count = (int) (fileLength / sliceSize);
            int i = 1;
            for(; i < count; ++ i){
                SlicePartStruct slicePartStruct = new SlicePartStruct();
                slicePartStruct.isAlreadyUpload = false;
                slicePartStruct.partNumber = i;
                slicePartStruct.offset = (i - 1) * sliceSize;
                slicePartStruct.sliceSize = sliceSize;
                partStructMap.put(i, slicePartStruct);
            }
            SlicePartStruct slicePartStruct = new SlicePartStruct();
            slicePartStruct.isAlreadyUpload = false;
            slicePartStruct.partNumber = i;
            slicePartStruct.offset = (i - 1) * sliceSize;
            slicePartStruct.sliceSize = fileLength - slicePartStruct.offset;
            partStructMap.put(i, slicePartStruct);
            UPLOAD_PART_COUNT.set(i);
            return;
        }
        throw new CosXmlClientException("file size or slice size less than 0");
    }

    private void updateSlicePart(ListPartsResult listPartsResult){
        if(listPartsResult != null && listPartsResult.listParts != null){
            List<ListParts.Part> parts = listPartsResult.listParts.parts;
            if(parts != null){
                for(ListParts.Part part : parts){
                    if(partStructMap.containsKey(part.partNumber)){
                        SlicePartStruct slicePartStruct = partStructMap.get(part.partNumber);
                        slicePartStruct.isAlreadyUpload = true;
                        slicePartStruct.eTag = part.eTag;
                        UPLOAD_PART_COUNT.decrementAndGet();
                        ALREADY_SEND_DATA_LEN.addAndGet(Long.parseLong(part.size));
                    }
                }
            }
        }
    }

    public static class ResumeData {
        public String bucket;
        public String cosPath;
        public String srcPath;
        public String uploadId;
        public long sliceSize;
    }

    private static class SlicePartStruct{
        public int partNumber;
        public boolean isAlreadyUpload;
        public long offset;
        public long sliceSize;
        public String eTag;
    }

    public static class UploadServiceResult extends CosXmlResult{
        public String eTag;
        public String accessUrl;

        @Override
        public String printResult() {
            return super.printResult() + "\n"
                    + "eTag:" + eTag + "\n"
                    + "accessUrl:" + accessUrl;
        }
    }
}
