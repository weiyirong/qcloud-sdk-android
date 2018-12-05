package com.tencent.cos.xml.transfer;


import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.common.ClientErrorCode;
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
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;
import com.tencent.cos.xml.model.tag.ListParts;
import com.tencent.qcloud.core.common.QCloudTaskStateListener;
import com.tencent.qcloud.core.http.HttpTask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bradyxiao on 2018/9/19.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public final class COSXMLCopyTask extends COSXMLTask implements Runnable{

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
    private ListPartsRequest listPartsRequest;
    protected long sliceSize;
    private Map<Integer, CopyPartStruct> copyPartStructMap; //必须有序
    private List<UploadPartCopyRequest> uploadPartCopyRequestList;
    /** 完成所有上传分片 */
    private CompleteMultiUploadRequest completeMultiUploadRequest;
    private AtomicBoolean IS_EXIT;
    private AtomicInteger UPLOAD_PART_COUNT;
    private Object SYNC_UPLOAD_PART = new Object();
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private LargeCopyStateListener largeCopyStateListenerHandler = new LargeCopyStateListener(){
        @Override
        public void onInit() {
            uploadPartCopy(cosXmlService);
        }

        @Override
        public void onListParts() {
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
        this(cosXmlService, copyObjectRequest.getRegion(), copyObjectRequest.getBucket(), copyObjectRequest.getPath(cosXmlService.getConfig()),
                copyObjectRequest.getCopySource());
        this.queries = copyObjectRequest.getQueryString();
        this.headers = copyObjectRequest.getRequestHeaders();
        this.cosXmlSignSourceProvider = copyObjectRequest.getSignSourceProvider();
        this.isNeedMd5 = copyObjectRequest.isNeedMD5();
    }

    protected void copy(){
        executorService.submit(this);
    }

    private void smallFileCopy(){
        copyObjectRequest = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
        copyObjectRequest.setRegion(region);

        copyObjectRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            copyObjectRequest.setSign(onSignatureListener.onGetSign(copyObjectRequest));
        }else {
            copyObjectRequest.setSignSourceProvider(cosXmlSignSourceProvider);
        }

        copyObjectRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(state == HttpTask.STATE_EXECUTING){
                    updateState(TransferState.IN_PROGRESS); // running
                }
            }
        });

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
                if(exception != null && exception.getMessage().toUpperCase().contains("CANCELED")){
                    return;
                }else {
                    if(updateState(TransferState.FAILED)){
                        // failed -> error
//                           QCloudLogger.d(TAG, taskState.name());
                        mException = exception == null ? serviceException : exception;
                        if(cosXmlResultListener != null){
                            cosXmlResultListener.onFail(buildCOSXMLTaskRequest(request), exception, serviceException);
                        }
                    }
                }
            }
        });
    }

    private void largeFileCopy(CosXmlSimpleService cosXmlService){
        initCopyPart();
        if(uploadId == null){
            //重新跑
            initMultiUpload(cosXmlService);
        }else {
            //继续跑
            listMultiUpload(cosXmlService);
        }
    }

    private void initMultiUpload(CosXmlSimpleService cosXmlService){
        initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
        initMultipartUploadRequest.setRegion(region);

        initMultipartUploadRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            initMultipartUploadRequest.setSign(onSignatureListener.onGetSign(initMultipartUploadRequest));
        }else {
            initMultipartUploadRequest.setSignSourceProvider(cosXmlSignSourceProvider);
        }

        initMultipartUploadRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(state == HttpTask.STATE_EXECUTING){
                    updateState(TransferState.IN_PROGRESS); // running
                }
            }
        });

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

    private synchronized void initCopyPart() {
        int count = (int) (fileLength / sliceSize);
        int i = 1;
        for(; i < count; ++ i){
            CopyPartStruct slicePartStruct = new CopyPartStruct();
            slicePartStruct.isAlreadyUpload = false;
            slicePartStruct.partNumber = i;
            slicePartStruct.start = (i - 1) * sliceSize;
            slicePartStruct.end = i * sliceSize - 1;
            copyPartStructMap.put(i, slicePartStruct);
        }
        CopyPartStruct slicePartStruct = new CopyPartStruct();
        slicePartStruct.isAlreadyUpload = false;
        slicePartStruct.partNumber = i;
        slicePartStruct.start = (i - 1) * sliceSize;
        slicePartStruct.end = fileLength - 1;
        copyPartStructMap.put(i, slicePartStruct);
        UPLOAD_PART_COUNT.set(i);
    }

    private void listMultiUpload(CosXmlSimpleService cosXmlService){
        listPartsRequest = new ListPartsRequest(bucket, cosPath, uploadId);

        listPartsRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            listPartsRequest.setSign(onSignatureListener.onGetSign(listPartsRequest));
        }else {
            listPartsRequest.setSignSourceProvider(cosXmlSignSourceProvider);
        }

        listPartsRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(IS_EXIT.get())return;
                if(state == HttpTask.STATE_EXECUTING){
                    updateState(TransferState.IN_PROGRESS); // running
                }
            }
        });

        cosXmlService.listPartsAsync(listPartsRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                //update list part, then upload part.
                if(IS_EXIT.get())return;
                updateSlicePart((ListPartsResult)result);
                largeCopyStateListenerHandler.onListParts();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(IS_EXIT.get())return;
                largeCopyStateListenerHandler.onFailed(request, exception, serviceException);
            }
        });
    }

    private void updateSlicePart(ListPartsResult listPartsResult){
        if(listPartsResult != null && listPartsResult.listParts != null){
            List<ListParts.Part> parts = listPartsResult.listParts.parts;
            if(parts != null){
                for(ListParts.Part part : parts){
                    if(copyPartStructMap.containsKey(Integer.valueOf(part.partNumber))){
                        CopyPartStruct slicePartStruct = copyPartStructMap.get(Integer.valueOf(part.partNumber));
                        slicePartStruct.isAlreadyUpload = true;
                        slicePartStruct.eTag = part.eTag;
                        UPLOAD_PART_COUNT.decrementAndGet();
                    }
                }
            }
        }
    }

    private void uploadPartCopy(CosXmlSimpleService cosXmlService){
        boolean isCopyFinished = true;
        for(Map.Entry<Integer, CopyPartStruct> entry : copyPartStructMap.entrySet()){
            final CopyPartStruct copyPartStruct = entry.getValue();
            if(!copyPartStruct.isAlreadyUpload && !IS_EXIT.get()){
                isCopyFinished = false;
                UploadPartCopyRequest uploadPartCopyRequest = new UploadPartCopyRequest(bucket,
                        cosPath, copyPartStruct.partNumber, uploadId, copySourceStruct, copyPartStruct.start,
                        copyPartStruct.end);
                uploadPartCopyRequest.setRegion(region);

                uploadPartCopyRequest.setRequestHeaders(headers);

                if(onSignatureListener != null){
                    uploadPartCopyRequest.setSign(onSignatureListener.onGetSign(uploadPartCopyRequest));
                }else {
                    uploadPartCopyRequest.setSignSourceProvider(cosXmlSignSourceProvider);
                }

                uploadPartCopyRequest.setTaskStateListener(new QCloudTaskStateListener() {
                    @Override
                    public void onStateChanged(String taskId, int state) {
                        if(state == HttpTask.STATE_EXECUTING){
                            updateState(TransferState.IN_PROGRESS); // running
                        }
                    }
                });

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
        for(Map.Entry<Integer, CopyPartStruct> entry : copyPartStructMap.entrySet()){
            CopyPartStruct copyPartStruct = entry.getValue();
            completeMultiUploadRequest.setPartNumberAndETag(copyPartStruct.partNumber, copyPartStruct.eTag);
        }

        completeMultiUploadRequest.setNeedMD5(isNeedMd5);
        completeMultiUploadRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            completeMultiUploadRequest.setSign(onSignatureListener.onGetSign(completeMultiUploadRequest));
        }else {
            completeMultiUploadRequest.setSignSourceProvider(cosXmlSignSourceProvider);
        }

        completeMultiUploadRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(state == HttpTask.STATE_EXECUTING){
                    updateState(TransferState.IN_PROGRESS); // running
                }
            }
        });

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

    /**
     *  暂停、失败、取消调用
     * @param cosXmlService cosXmlSimpleService
     */
    private void cancelAllRequest(CosXmlSimpleService cosXmlService){
        if(headObjectRequest != null){
            cosXmlService.cancel(headObjectRequest);
            headObjectRequest = null;
        }
        if(copyObjectRequest != null){
            cosXmlService.cancel(copyObjectRequest);
            copyObjectRequest = null;
        }
        if(initMultipartUploadRequest != null){
            cosXmlService.cancel(initMultipartUploadRequest);
            initMultipartUploadRequest = null;
        }
        if(listPartsRequest != null){
            cosXmlService.cancel(listPartsRequest);
            listPartsRequest = null;
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

    /**
     * 取消时调用
     * @param cosXmlService CosXmlSimpleService
     */
    private void abortMultiUpload(CosXmlSimpleService cosXmlService){
        if(uploadId == null) return;
        //clear copy part list on cos
        AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest(bucket, cosPath,
                uploadId);

        if(onSignatureListener != null){
            abortMultiUploadRequest.setSign(onSignatureListener.onGetSign(abortMultiUploadRequest));
        }else {
            abortMultiUploadRequest.setSignSourceProvider(cosXmlSignSourceProvider);
        }

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

    //clear all
    private void clear(){
    }

    @Override
    public void pause() {
        if(updateState(TransferState.PAUSED)){
//            CosXmlClientException cosXmlClientException = new CosXmlClientException("paused by user");
//            mException = cosXmlClientException;
//            if(cosXmlResultListener != null){
//                cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), cosXmlClientException, null);
//            }
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
            CosXmlClientException cosXmlClientException = new CosXmlClientException(ClientErrorCode.USER_CANCELLED.getCode(), "canceled by user");
            mException = cosXmlClientException;
            if(cosXmlResultListener != null){
                cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), cosXmlClientException, null);
            }
            if(isLargeCopy){
                IS_EXIT.set(true);
                cancelAllRequest(cosXmlService);
                abortMultiUpload(cosXmlService);
                clear();
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

    @Override
    public void run() {
        updateState(TransferState.WAITING); // waiting
        headObjectRequest = new HeadObjectRequest(copySourceStruct.bucket, copySourceStruct.cosPath);
        headObjectRequest.setRegion(copySourceStruct.region);

        if(onSignatureListener != null){
            headObjectRequest.setSign(onSignatureListener.onGetSign(headObjectRequest));
        }else {
            headObjectRequest.setSignSourceProvider(cosXmlSignSourceProvider);
        }

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
                    if(IS_EXIT != null){
                        IS_EXIT.set(false);
                    }else {
                        IS_EXIT = new AtomicBoolean(false);
                    }
                    isLargeCopy = true;
                    if(copyPartStructMap != null){
                        copyPartStructMap.clear();
                    }else {
                        copyPartStructMap = new LinkedHashMap<>();
                    }
                    if(uploadPartCopyRequestList != null){
                        uploadPartCopyRequestList.clear();
                    }else {
                        uploadPartCopyRequestList = new ArrayList<>();
                    }
                    if(UPLOAD_PART_COUNT != null){
                        UPLOAD_PART_COUNT.set(0);
                    }else {
                        UPLOAD_PART_COUNT = new AtomicInteger(0);
                    }
                    largeFileCopy(cosXmlService);
                }else {
                    smallFileCopy();
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(exception != null && exception.getMessage().toUpperCase().contains("CANCELED")){
                    return;
                }else {
                    if(updateState(TransferState.FAILED)){
                        // failed -> error
//                           QCloudLogger.d(TAG, taskState.name());
                        mException = exception == null ? serviceException : exception;
                        if(cosXmlResultListener != null){
                            cosXmlResultListener.onFail(buildCOSXMLTaskRequest(request), exception, serviceException);
                        }
                    }
                }
            }
        });
    }

    private static interface LargeCopyStateListener{
        void onInit();
        void onListParts();
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
