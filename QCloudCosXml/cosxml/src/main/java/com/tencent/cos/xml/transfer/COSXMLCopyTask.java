package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;
import com.tencent.qcloud.core.common.QCloudTaskStateListener;
import com.tencent.qcloud.core.http.HttpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bradyxiao on 2018/9/19.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public final class COSXMLCopyTask extends COSXMLTask {

    /** 是否分片拷贝Limit */
    protected long multiCopySizeDivision;
    /** 拷贝的数据源 */
    private CopyObjectRequest.CopySourceStruct copySourceStruct;
    /** 数据源的长度 */
    private long fileLength;
    /** 获取源文件属性 */
    private HeadObjectRequest headObjectRequest;

    /** 小文件拷贝 */
    private CopyObjectRequest copyObjectRequest;

    /** 大文件拷贝 */
    private boolean isLargeCopy = false;
    /** 分片uploadId 属性 */
    private String uploadId;
    /** 初始化分片上传 */
    private InitMultipartUploadRequest initMultipartUploadRequest;
    protected long sliceSize;
    private List<CopyPartStruct> copyPartStructList; //必须有序
    private List<UploadPartCopyRequest> uploadPartCopyRequestList;
    /** 完成所有上传分片 */
    private CompleteMultiUploadRequest completeMultiUploadRequest;
    private AtomicBoolean IS_EXIT;
    private AtomicInteger UPLOAD_PART_COUNT;
    private Object SYNC_UPLOAD_PART = new Object();
    private LargeCopyStateListener largeCopyStateListenerHandler = new LargeCopyStateListener(){
        @Override
        public void onInit() {
            uploadPartCopy(cosXmlService);
        }

        @Override
        public void onUploadPartCopy() {
            completeMultiUpload(cosXmlService);
        }

        @Override
        public void onCompleted(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
            IS_EXIT.set(true);
            if(updateState(TransferState.COMPLETED)){
                mResult = buildCOSXMLTaskResult(cosXmlResult);
                if(cosXmlResultListener != null){
                    cosXmlResultListener.onSuccess(buildCOSXMLTaskRequest(cosXmlRequest), mResult);
                }
            }
        }

        @Override
        public void onFailed(CosXmlRequest cosXmlRequest, CosXmlClientException exception, CosXmlServiceException serviceException) {
            IS_EXIT.set(true);
            if(updateState(TransferState.FAILED)){
                mException = exception == null ? serviceException : exception;
                if(cosXmlResultListener != null){
                    cosXmlResultListener.onFail(buildCOSXMLTaskRequest(cosXmlRequest), exception, serviceException);
                }
                cancelAllRequest(cosXmlService);
            }
        }
    };

    COSXMLCopyTask( CosXmlSimpleService cosXmlService, String region,String bucket, String cosPath,
                          CopyObjectRequest.CopySourceStruct copySourceStruct){
        this.cosXmlService = cosXmlService;
        this.region = region;
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.copySourceStruct = copySourceStruct;
    }

    COSXMLCopyTask( CosXmlSimpleService cosXmlService, CopyObjectRequest copyObjectRequest){
        this(cosXmlService, copyObjectRequest.getRegion(), copyObjectRequest.getHostPrefix(), copyObjectRequest.getPath(null),
                copyObjectRequest.getCopySource());
        this.queries = copyObjectRequest.getQueryString();
        this.headers = copyObjectRequest.getRequestHeaders();
        this.cosXmlSignSourceProvider = copyObjectRequest.getSignSourceProvider();
        this.isNeedMd5 = copyObjectRequest.isNeedMD5();
    }

    protected void copy(){
        checkParameters();
        updateState(TransferState.WAITING); // waiting
        headObjectRequest = new HeadObjectRequest(copySourceStruct.bucket, copySourceStruct.cosPath);
        headObjectRequest.setRegion(copySourceStruct.region);
        headObjectRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(state == HttpTask.STATE_EXECUTING){
                    updateState(TransferState.IN_PROGRESS); // running
                }
            }
        });
        cosXmlService.headObjectAsync(headObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                List<String> contentLengths = result.headers.get("Content-Length");
                if(contentLengths != null && contentLengths.size() > 0){
                    fileLength = Long.parseLong(contentLengths.get(0));
                }
                if(fileLength > multiCopySizeDivision){
                    IS_EXIT = new AtomicBoolean(false);
                    isLargeCopy = true;
                    copyPartStructList = new ArrayList<>();
                    uploadPartCopyRequestList = new ArrayList<>();
                    UPLOAD_PART_COUNT = new AtomicInteger(0);
                    largeFileCopy(cosXmlService);
                }else {
                    smallFileCopy();
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(updateState(TransferState.FAILED)){
                    // failed -> error
                    mException = exception == null ? serviceException : exception;
                    if(cosXmlResultListener != null){
                        cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), exception, serviceException);
                    }
                }
            }
        });
    }

    private void smallFileCopy(){
        //updateState(TransferState.IN_PROGRESS); // running
        copyObjectRequest = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
        copyObjectRequest.setRegion(region);

        copyObjectRequest.setRequestHeaders(headers);
        copyObjectRequest.setSignSourceProvider(cosXmlSignSourceProvider);

        cosXmlService.copyObjectAsync(copyObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                if(updateState(TransferState.COMPLETED)){
                    // complete -> success
                    mResult = buildCOSXMLTaskResult(result);
                    if(cosXmlResultListener != null){
                        cosXmlResultListener.onSuccess(buildCOSXMLTaskRequest(request), mResult);
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(updateState(TransferState.FAILED)){
                    // failed -> error
                    mException = exception == null ? serviceException : exception;
                    if(cosXmlResultListener != null){
                        cosXmlResultListener.onFail(buildCOSXMLTaskRequest(request), exception, serviceException);
                    }
                }
            }
        });
    }

    private void largeFileCopy(CosXmlSimpleService cosXmlService){
        initCopyPart();
        initMultiUpload(cosXmlService);
    }

    private void initMultiUpload(CosXmlSimpleService cosXmlService){
        initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
        initMultipartUploadRequest.setRegion(region);

        initMultipartUploadRequest.setRequestHeaders(headers);
        initMultipartUploadRequest.setSignSourceProvider(cosXmlSignSourceProvider);

        cosXmlService.initMultipartUploadAsync(initMultipartUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                // notify -> upload part
                if(IS_EXIT.get())return;
                uploadId = ((InitMultipartUploadResult)result).initMultipartUpload.uploadId;
                largeCopyStateListenerHandler.onInit();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                // notify -> exit caused by failed
                if(IS_EXIT.get())return;
                largeCopyStateListenerHandler.onFailed(request, exception, serviceException);
            }
        });
    }

    private void initCopyPart() {
        int count = (int) (fileLength / sliceSize);
        int i = 1;
        for(; i < count; ++ i){
            CopyPartStruct slicePartStruct = new CopyPartStruct();
            slicePartStruct.isAlreadyUpload = false;
            slicePartStruct.partNumber = i;
            slicePartStruct.start = (i - 1) * sliceSize;
            slicePartStruct.end = i * sliceSize - 1;
            copyPartStructList.add(slicePartStruct);
        }
        CopyPartStruct slicePartStruct = new CopyPartStruct();
        slicePartStruct.isAlreadyUpload = false;
        slicePartStruct.partNumber = i;
        slicePartStruct.start = (i - 1) * sliceSize;
        slicePartStruct.end = fileLength - 1;
        copyPartStructList.add(slicePartStruct);
        UPLOAD_PART_COUNT.set(i);
        if(IS_EXIT.get())return;
    }

    private void uploadPartCopy(CosXmlSimpleService cosXmlService){
        boolean isCopyFinished = true;
        for(final CopyPartStruct copyPartStruct : copyPartStructList){
            if(!copyPartStruct.isAlreadyUpload && !IS_EXIT.get()){
                isCopyFinished = false;
                UploadPartCopyRequest uploadPartCopyRequest = new UploadPartCopyRequest(bucket,
                        cosPath, copyPartStruct.partNumber, uploadId, copySourceStruct, copyPartStruct.start,
                        copyPartStruct.end);
                uploadPartCopyRequest.setRegion(region);

                uploadPartCopyRequest.setRequestHeaders(headers);
                uploadPartCopyRequest.setSignSourceProvider(cosXmlSignSourceProvider);

                uploadPartCopyRequestList.add(uploadPartCopyRequest);

                cosXmlService.copyObjectAsync(uploadPartCopyRequest, new CosXmlResultListener() {
                    @Override
                    public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                        copyPartStruct.eTag = ((UploadPartCopyResult)result).copyObject.eTag;
                        copyPartStruct.isAlreadyUpload = true;
                        synchronized (SYNC_UPLOAD_PART){
                            UPLOAD_PART_COUNT.decrementAndGet();
                            if(UPLOAD_PART_COUNT.get() == 0){
                                if(IS_EXIT.get())return;
                                largeCopyStateListenerHandler.onUploadPartCopy();
                            }
                        }
                    }

                    @Override
                    public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                        if(IS_EXIT.get())return;//已经上报失败了
                        largeCopyStateListenerHandler.onFailed(request, exception, serviceException);
                    }
                });
            }
        }
        if(isCopyFinished && !IS_EXIT.get()){
            largeCopyStateListenerHandler.onUploadPartCopy();
        }
    }

    private void completeMultiUpload(CosXmlSimpleService cosXmlService){
        completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath,
                uploadId, null);
        for(CopyPartStruct copyPartStruct : copyPartStructList){
            completeMultiUploadRequest.setPartNumberAndETag(copyPartStruct.partNumber, copyPartStruct.eTag);
        }

        completeMultiUploadRequest.setNeedMD5(isNeedMd5);
        completeMultiUploadRequest.setRequestHeaders(headers);
        completeMultiUploadRequest.setSignSourceProvider(cosXmlSignSourceProvider);

        cosXmlService.completeMultiUploadAsync(completeMultiUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                if(IS_EXIT.get())return;
                largeCopyStateListenerHandler.onCompleted(request, result);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(IS_EXIT.get())return;
                largeCopyStateListenerHandler.onFailed(request, exception, serviceException);
            }
        });
    }

    private void cancelAllRequest(CosXmlSimpleService cosXmlService){
        if(headObjectRequest != null){
            cosXmlService.cancel(headObjectRequest);
            headObjectRequest = null;
        }
        if(copyObjectRequest != null){
            cosXmlService.cancel(copyObjectRequest);
            copyObjectRequest = null;
        }

        if(uploadPartCopyRequestList != null) {
            for (UploadPartCopyRequest uploadPartCopyRequest : uploadPartCopyRequestList) {
                cosXmlService.cancel(uploadPartCopyRequest);
            }
            uploadPartCopyRequestList.clear();
        }
        if(completeMultiUploadRequest != null){
            cosXmlService.cancel(completeMultiUploadRequest);
            completeMultiUploadRequest = null;
        }

    }
    private void abortMultiUpload(CosXmlSimpleService cosXmlService){
        if(uploadId == null) return;
        AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest(bucket, cosPath,
                uploadId);
        cosXmlService.abortMultiUploadAsync(abortMultiUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                // abort success
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                // abort failed
            }
        });
    }

    @Override
    public void pause() {
        if(updateState(TransferState.PAUSED)){
            CosXmlClientException cosXmlClientException = new CosXmlClientException("paused by user");
            mException = cosXmlClientException;
            if(cosXmlResultListener != null){
                cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), cosXmlClientException, null);
            }
            if(isLargeCopy){
                IS_EXIT.set(true);
                cancelAllRequest(cosXmlService);
            }else {
                cosXmlService.cancel(headObjectRequest);
                headObjectRequest = null;
                cosXmlService.cancel(copyObjectRequest);
                copyObjectRequest = null;
            }
        }
    }

    @Override
    public void cancel() {
        if(updateState(TransferState.CANCELED)){
            CosXmlClientException cosXmlClientException = new CosXmlClientException("cancelled by user");
            mException = cosXmlClientException;
            if(cosXmlResultListener != null){
                cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), cosXmlClientException, null);
            }
            if(isLargeCopy){
                IS_EXIT.set(true);
                cancelAllRequest(cosXmlService);
                abortMultiUpload(cosXmlService);
            }else {
                cosXmlService.cancel(headObjectRequest);
                headObjectRequest = null;
                cosXmlService.cancel(copyObjectRequest);
                copyObjectRequest = null;
            }
        }
    }

    @Override
    public void resume() {
        if(updateState(TransferState.RESUMED_WAITING)){
            copy();
        }
    }

    @Override
    protected CosXmlRequest buildCOSXMLTaskRequest(CosXmlRequest sourceRequest) {
        COSXMLCopyTaskRequest cosxmlCopyTaskRequest = new COSXMLCopyTaskRequest(region, bucket, cosPath, copySourceStruct,
                headers, queries);
        return cosxmlCopyTaskRequest;
    }

    @Override
    protected CosXmlResult buildCOSXMLTaskResult(CosXmlResult sourceResult) {
        COSXMLCopyTaskResult cosxmlCopyTaskResult = new COSXMLCopyTaskResult();
        if(sourceResult != null && sourceResult instanceof CopyObjectResult){
            CopyObjectResult copyObjectResult = (CopyObjectResult) sourceResult;
            cosxmlCopyTaskResult.httpCode = copyObjectResult.httpCode;
            cosxmlCopyTaskResult.httpMessage = copyObjectResult.httpMessage;
            cosxmlCopyTaskResult.headers = copyObjectResult.headers;
            cosxmlCopyTaskResult.eTag = copyObjectResult.copyObject.eTag;
            cosxmlCopyTaskResult.accessUrl = copyObjectResult.accessUrl;
        }else if(sourceResult != null && sourceResult instanceof CompleteMultiUploadResult){
            CompleteMultiUploadResult completeMultiUploadResult = (CompleteMultiUploadResult) sourceResult;
            cosxmlCopyTaskResult.httpCode = completeMultiUploadResult.httpCode;
            cosxmlCopyTaskResult.httpMessage = completeMultiUploadResult.httpMessage;
            cosxmlCopyTaskResult.headers = completeMultiUploadResult.headers;
            cosxmlCopyTaskResult.eTag = completeMultiUploadResult.completeMultipartUpload.eTag;
            cosxmlCopyTaskResult.accessUrl = completeMultiUploadResult.accessUrl;
        }
        return cosxmlCopyTaskResult;
    }

    public String getUploadId() {
        return uploadId;
    }

    private static interface LargeCopyStateListener{
        void onInit();
        void onUploadPartCopy();
        void onCompleted(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult);
        void onFailed(CosXmlRequest cosXmlRequest, CosXmlClientException exception, CosXmlServiceException serviceException);
    }

    private static class CopyPartStruct{
        public int partNumber;
        public boolean isAlreadyUpload;
        public long start;
        public long end;
        public String eTag;
    }

    public static class COSXMLCopyTaskResult extends CosXmlResult{
        protected COSXMLCopyTaskResult(){}
        public String eTag;
    }

    public static class COSXMLCopyTaskRequest extends CopyObjectRequest{

        protected COSXMLCopyTaskRequest(String region, String bucket, String cosPath, CopySourceStruct copySourceStruct, Map<String, List<String>>headers,
                                        Map<String, String> queryStr) {
            super(bucket, cosPath, copySourceStruct);
            this.setRegion(region);
            this.setRequestHeaders(headers);
            this.setQueryParameters(queryStr);

        }
    }
}
