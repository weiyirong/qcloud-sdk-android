package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.common.ClientErrorCode;
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
import com.tencent.qcloud.core.common.QCloudTaskStateListener;
import com.tencent.qcloud.core.http.HttpTask;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bradyxiao on 2018/8/23.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public final class COSXMLUploadTask extends COSXMLTask {

    /** 满足分片上传的文件最小长度 */
    protected long multiUploadSizeDivision;
    /** 源文件的本地路径 */
    private String srcPath;
    /** 源文件的长度 */
    private long fileLength;

    /** 字节数组, COSXMLUploadTask 中只支持简单上传 */
    private byte[] bytes;

    /** 字节流，COSXMLUploadTask 中只支持简单上传 */
    private InputStream inputStream;

    /** 简单上传 */
    private PutObjectRequest putObjectRequest;

    /** 分片上传*/
    private boolean isSliceUpload = false;
    /** 分片大小 */
    protected long sliceSize;
    /** 分片上传 UploadId 属性 */
    private String uploadId;
    /** 初始化分片上传 */
    private InitMultipartUploadRequest initMultipartUploadRequest;
    /** 列举以上传的分片 */
    private ListPartsRequest listPartsRequest;
    /** 完成所有上传分片 */
    private CompleteMultiUploadRequest completeMultiUploadRequest;
    /** 上传分片块 */
    private Map<UploadPartRequest,Long> uploadPartRequestLongMap;
    private Map<Integer, SlicePartStruct> partStructMap;
    private AtomicInteger UPLOAD_PART_COUNT;
    private AtomicLong ALREADY_SEND_DATA_LEN;
    private AtomicBoolean IS_EXIT;
    private Object SYNC_UPLOAD_PART = new Object();
    private MultiUploadsStateListener multiUploadsStateListenerHandler = new MultiUploadsStateListener() {
        @Override
        public void onInit() {
            multiUploadPart(cosXmlService);
        }

        @Override
        public void onListParts() {
            multiUploadPart(cosXmlService);
        }

        @Override
        public void onUploadParts() {
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
                    cosXmlResultListener.onFail(buildCOSXMLTaskRequest(buildCOSXMLTaskRequest(cosXmlRequest)), exception, serviceException);
                }
                cancelAllRequest(cosXmlService);
            }
        }
    };

    COSXMLUploadTask(CosXmlSimpleService cosXmlService, String region, String bucket, String cosPath, String srcPath, String uploadId){
        this.region = region;
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.srcPath = srcPath;
        this.uploadId = uploadId;
        this.cosXmlService = cosXmlService;
    }

    COSXMLUploadTask(CosXmlSimpleService cosXmlService, String region, String bucket, String cosPath, byte[] bytes){
        this.region = region;
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.bytes = bytes;
        this.cosXmlService = cosXmlService;
    }

    COSXMLUploadTask(CosXmlSimpleService cosXmlService, String region, String bucket, String cosPath, InputStream inputStream){
        this.region = region;
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.inputStream = inputStream;
        this.cosXmlService = cosXmlService;
    }

    COSXMLUploadTask(CosXmlSimpleService cosXmlService, PutObjectRequest putObjectRequest, String uploadId){
        this(cosXmlService, putObjectRequest.getRegion(), putObjectRequest.getBucket(), putObjectRequest.getPath(cosXmlService.getConfig()),
                putObjectRequest.getSrcPath(), uploadId);
        this.queries = putObjectRequest.getQueryString();
        this.headers = putObjectRequest.getRequestHeaders();
        this.isNeedMd5 = putObjectRequest.isNeedMD5();
    }


    protected void upload(){
//        checkParameters();
//        executorService.submit(this);
        run();
    }

    private void simpleUpload(CosXmlSimpleService cosXmlService){
        if(bytes != null){
            putObjectRequest = new PutObjectRequest(bucket, cosPath, bytes);
        }else if(inputStream != null){
            putObjectRequest = new PutObjectRequest(bucket, cosPath, inputStream);
        }else {
            putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        }
        putObjectRequest.setRegion(region);
        putObjectRequest.setNeedMD5(isNeedMd5);
        putObjectRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            putObjectRequest.setSign(onSignatureListener.onGetSign(putObjectRequest));
        }
        getHttpMetrics(putObjectRequest, "PutObjectRequest");

        putObjectRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(state == HttpTask.STATE_EXECUTING){
                    updateState(TransferState.IN_PROGRESS); // running
                }
            }
        });
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                if(cosXmlProgressListener != null){
                    cosXmlProgressListener.onProgress(complete, target);
                }
            }
        });

        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                if(updateState(TransferState.COMPLETED)){
                    // complete -> success
                    mResult = buildCOSXMLTaskResult(result);
                    if(cosXmlResultListener != null){
                        cosXmlResultListener.onSuccess(buildCOSXMLTaskRequest(null), mResult);
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

    private void multiUpload(CosXmlSimpleService cosXmlService){
        initSlicePart(fileLength, 1);
        if(uploadId != null){
            listMultiUpload(cosXmlService);
        }else {
            initMultiUpload(cosXmlService);
        }
    }

    private void initMultiUpload(CosXmlSimpleService cosXmlService){
        initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
        initMultipartUploadRequest.setRegion(region);

        initMultipartUploadRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            initMultipartUploadRequest.setSign(onSignatureListener.onGetSign(initMultipartUploadRequest));
        }

        getHttpMetrics(initMultipartUploadRequest, "InitMultipartUploadRequest");

        initMultipartUploadRequest.setTaskStateListener(new QCloudTaskStateListener() {
            @Override
            public void onStateChanged(String taskId, int state) {
                if(IS_EXIT.get())return;
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
                multiUploadsStateListenerHandler.onInit();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                // notify -> exit caused by failed
                if(IS_EXIT.get())return;
                multiUploadsStateListenerHandler.onFailed(request, exception, serviceException);
            }
        });
    }

    private void listMultiUpload(CosXmlSimpleService cosXmlService){
        listPartsRequest = new ListPartsRequest(bucket, cosPath, uploadId);

        listPartsRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            listPartsRequest.setSign(onSignatureListener.onGetSign(listPartsRequest));
        }

        getHttpMetrics(listPartsRequest, "ListPartsRequest");

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
                multiUploadsStateListenerHandler.onListParts();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(IS_EXIT.get())return;
                multiUploadsStateListenerHandler.onFailed(request, exception, serviceException);
            }
        });
    }

    private void multiUploadPart(CosXmlSimpleService cosXmlService){
        //是否已上传完
        boolean isUploadFinished = true;
        // 循环进行
        for(final Map.Entry<Integer, SlicePartStruct> entry : partStructMap.entrySet()){
            final SlicePartStruct slicePartStruct = entry.getValue();
            //是否已经failed了，则就不要在继续了
            if(!slicePartStruct.isAlreadyUpload && !IS_EXIT.get()){
                isUploadFinished = false;
                final UploadPartRequest uploadPartRequest = new UploadPartRequest(bucket, cosPath, slicePartStruct.partNumber,
                        srcPath, slicePartStruct.offset, slicePartStruct.sliceSize,  uploadId);

                uploadPartRequest.setNeedMD5(isNeedMd5);
                uploadPartRequest.setRequestHeaders(headers);

                if(onSignatureListener != null){
                    uploadPartRequest.setSign(onSignatureListener.onGetSign(uploadPartRequest));
                }

                getHttpMetrics(uploadPartRequest, "UploadPartRequest");

                uploadPartRequestLongMap.put(uploadPartRequest, 0L);
                uploadPartRequest.setProgressListener(new CosXmlProgressListener() {
                    @Override
                    public void onProgress(long complete, long target) {
                        if(IS_EXIT.get())return;//已经上报失败了
                        try {
                            long dataLen = ALREADY_SEND_DATA_LEN.addAndGet(complete - uploadPartRequestLongMap.get(uploadPartRequest));
                            uploadPartRequestLongMap.put(uploadPartRequest, complete);
                            if(cosXmlProgressListener != null){
                                cosXmlProgressListener.onProgress(dataLen, fileLength);
                            }
                        }catch (Exception e){
                            //cause by cancel or pause
                        }
                    }
                });
                cosXmlService.uploadPartAsync(uploadPartRequest, new CosXmlResultListener() {
                    @Override
                    public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                        slicePartStruct.eTag = ((UploadPartResult)result).eTag;
                        slicePartStruct.isAlreadyUpload = true;
                        synchronized (SYNC_UPLOAD_PART){
                            UPLOAD_PART_COUNT.decrementAndGet();
                            if(UPLOAD_PART_COUNT.get() == 0){
                                if(IS_EXIT.get())return;
                                multiUploadsStateListenerHandler.onUploadParts();
                            }
                        }
                    }

                    @Override
                    public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                        if(IS_EXIT.get())return;//已经上报失败了
                        multiUploadsStateListenerHandler.onFailed(request, exception, serviceException);
                    }
                });
            }
        }
        if(isUploadFinished && !IS_EXIT.get()){
            if(cosXmlProgressListener != null){
                cosXmlProgressListener.onProgress(fileLength, fileLength);
            }
            multiUploadsStateListenerHandler.onUploadParts();
        }
    }

    private void completeMultiUpload(CosXmlSimpleService cosXmlService){
        completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath,
                uploadId, null);
        for(Map.Entry<Integer, SlicePartStruct> entry : partStructMap.entrySet()){
            SlicePartStruct slicePartStruct = entry.getValue();
            completeMultiUploadRequest.setPartNumberAndETag(slicePartStruct.partNumber, slicePartStruct.eTag);
        }

        completeMultiUploadRequest.setNeedMD5(isNeedMd5);
        completeMultiUploadRequest.setRequestHeaders(headers);

        if(onSignatureListener != null){
            completeMultiUploadRequest.setSign(onSignatureListener.onGetSign(completeMultiUploadRequest));
        }

        getHttpMetrics(completeMultiUploadRequest, "CompleteMultiUploadRequest");

        cosXmlService.completeMultiUploadAsync(completeMultiUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                if(IS_EXIT.get())return;
                multiUploadsStateListenerHandler.onCompleted(request, result);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(IS_EXIT.get())return;
                multiUploadsStateListenerHandler.onFailed(request, exception, serviceException);
            }
        });
    }

    private void cancelAllRequest(CosXmlSimpleService cosXmlService){
        if(putObjectRequest != null){
            cosXmlService.cancel(putObjectRequest);
            putObjectRequest = null;
        }
        if(initMultipartUploadRequest != null){
            cosXmlService.cancel(initMultipartUploadRequest);
            initMultipartUploadRequest = null;
        }
        if(listPartsRequest != null){
            cosXmlService.cancel(initMultipartUploadRequest);
            listPartsRequest = null;
        }
        if(uploadPartRequestLongMap != null){
            Set<UploadPartRequest> set = uploadPartRequestLongMap.keySet();
            Iterator<UploadPartRequest> iterator = set.iterator();
            while(iterator.hasNext()){
                cosXmlService.cancel(iterator.next());
            }
            uploadPartRequestLongMap.clear();
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

        if(onSignatureListener != null){
            abortMultiUploadRequest.setSign(onSignatureListener.onGetSign(abortMultiUploadRequest));
        }

        getHttpMetrics(abortMultiUploadRequest, "AbortMultiUploadRequest");

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
//            CosXmlClientException cosXmlClientException = new CosXmlClientException("paused by user");
//            mException = cosXmlClientException;
//            if(cosXmlResultListener != null){
//                cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), cosXmlClientException, null);
//            }
            if(isSliceUpload){
                IS_EXIT.set(true);
                cancelAllRequest(cosXmlService);
            }else {
                cosXmlService.cancel(putObjectRequest);
                putObjectRequest = null;
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
            if(isSliceUpload){
                IS_EXIT.set(true);
                cancelAllRequest(cosXmlService);
                abortMultiUpload(cosXmlService);
            }else {
                cosXmlService.cancel(putObjectRequest);
                putObjectRequest = null;
            }
        }
    }

    @Override
    public void resume() {
        if(updateState(TransferState.RESUMED_WAITING)){
            if(inputStream != null){
                CosXmlClientException cosXmlClientException = new CosXmlClientException(ClientErrorCode.SINK_SOURCE_NOT_FOUND.getCode(), "inputStream closed");
                mException = cosXmlClientException;
                if(cosXmlResultListener != null){
                    cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), cosXmlClientException, null);
                }
                return;
            }
            upload();
        }
    }

    @Override
    protected CosXmlRequest buildCOSXMLTaskRequest(CosXmlRequest sourceRequest) {
        COSXMLUploadTaskRequest cosxmlUploadTaskRequest = new COSXMLUploadTaskRequest(region, bucket,
                cosPath, srcPath, headers, queries);
        return cosxmlUploadTaskRequest;
    }

    @Override
    protected CosXmlResult buildCOSXMLTaskResult(CosXmlResult sourceResult) {
        COSXMLUploadTaskResult cosxmlUploadTaskResult = new COSXMLUploadTaskResult();
        if(sourceResult != null && sourceResult instanceof PutObjectResult){
            PutObjectResult putObjectResult = (PutObjectResult) sourceResult;
            cosxmlUploadTaskResult.httpCode = putObjectResult.httpCode;
            cosxmlUploadTaskResult.httpMessage = putObjectResult.httpMessage;
            cosxmlUploadTaskResult.headers = putObjectResult.headers;
            cosxmlUploadTaskResult.eTag = putObjectResult.eTag;
            cosxmlUploadTaskResult.accessUrl = putObjectResult.accessUrl;
        }else if(sourceResult != null && sourceResult instanceof CompleteMultiUploadResult){
            CompleteMultiUploadResult completeMultiUploadResult = (CompleteMultiUploadResult) sourceResult;
            cosxmlUploadTaskResult.httpCode = completeMultiUploadResult.httpCode;
            cosxmlUploadTaskResult.httpMessage = completeMultiUploadResult.httpMessage;
            cosxmlUploadTaskResult.headers = completeMultiUploadResult.headers;
            cosxmlUploadTaskResult.eTag = completeMultiUploadResult.completeMultipartUpload.eTag;
            cosxmlUploadTaskResult.accessUrl = completeMultiUploadResult.accessUrl;
        }
        return cosxmlUploadTaskResult;
    }

    public String getUploadId(){
        return uploadId;
    }

    /**
     * init slice part
     */
    private void initSlicePart(long fileLength, int startNumber){
        int count = (int) (fileLength / sliceSize);
        for(; startNumber < count; ++ startNumber){
            SlicePartStruct slicePartStruct = new SlicePartStruct();
            slicePartStruct.isAlreadyUpload = false;
            slicePartStruct.partNumber = startNumber;
            slicePartStruct.offset = (startNumber - 1) * sliceSize;
            slicePartStruct.sliceSize = sliceSize;
            partStructMap.put(startNumber, slicePartStruct);
        }
        SlicePartStruct slicePartStruct = new SlicePartStruct();
        slicePartStruct.isAlreadyUpload = false;
        slicePartStruct.partNumber = startNumber;
        slicePartStruct.offset = (startNumber - 1) * sliceSize;
        slicePartStruct.sliceSize = fileLength - slicePartStruct.offset;
        partStructMap.put(startNumber, slicePartStruct);
        UPLOAD_PART_COUNT.set(startNumber);
        if(IS_EXIT.get())return;
    }

    /**
     * 需要做如下判断，已上传的分片大小和请求设置分片大小是否一致
     * 1）若是一致，则可以乱序续传
     * 2）若不一致，则续传只能从开始partNumber = 1连续已上传的分片开始
     * 如何判断是否一致
     * 1）
     * @param listPartsResult
     */
    private void updateSlicePart(ListPartsResult listPartsResult){
        if(listPartsResult != null && listPartsResult.listParts != null){
            List<ListParts.Part> parts = listPartsResult.listParts.parts;
            if(parts != null && parts.size() > 0){
                if(isFixSliceSize(parts)){
                    for(ListParts.Part part : parts){
                        if(partStructMap.containsKey(Integer.valueOf(part.partNumber))){
                            SlicePartStruct slicePartStruct = partStructMap.get(Integer.valueOf(part.partNumber));
                            slicePartStruct.isAlreadyUpload = true;
                            slicePartStruct.eTag = part.eTag;
                            UPLOAD_PART_COUNT.decrementAndGet();
                            ALREADY_SEND_DATA_LEN.addAndGet(Long.parseLong(part.size));
                        }
                    }
                }else {
                    //不支持，则只能从partNumber = 1开始，获取连续块
                    //排序已上传块
                    Collections.sort(parts, new Comparator<ListParts.Part>() {
                        @Override
                        public int compare(ListParts.Part a, ListParts.Part b) {
                            int aNumb = Integer.valueOf(a.partNumber);
                            int bNumb = Integer.valueOf(b.partNumber);
                            if(aNumb > bNumb) return 1;
                            if(aNumb < bNumb) return -1;
                            return 0;
                        }
                    });
                    //只取连续块，且从partNumber =1开始
                    int index = getIndexOfParts(parts);
                    if(index < 0){
                        return;
                    }
                    partStructMap.clear();
                    long completed = 0L;
                    for(int i = 0; i <= index; i ++){
                        ListParts.Part part = parts.get(i);
                        SlicePartStruct slicePartStruct = new SlicePartStruct();
                        slicePartStruct.partNumber = i + 1;
                        slicePartStruct.offset = completed;
                        slicePartStruct.sliceSize = Long.parseLong(part.size);
                        slicePartStruct.eTag = part.eTag;
                        slicePartStruct.isAlreadyUpload = true;
                        completed += slicePartStruct.sliceSize;
                        partStructMap.put(i + 1, slicePartStruct);
                    }
                    //重新计算剩下的分片
                    ALREADY_SEND_DATA_LEN.addAndGet(completed);
                    initSlicePart(fileLength - completed, index + 2);
                    for(int i = 0; i <= index; i ++){
                        UPLOAD_PART_COUNT.decrementAndGet();
                    }
                }
            }
        }
    }

    private boolean isFixSliceSize(List<ListParts.Part> parts){
        boolean isTrue = true;
        for(ListParts.Part part : parts){
            if(partStructMap.containsKey(Integer.valueOf(part.partNumber))){
                SlicePartStruct slicePartStruct = partStructMap.get(Integer.valueOf(part.partNumber));
                if(slicePartStruct.sliceSize == Long.valueOf(part.size)) continue;
                else {
                    isTrue = false;
                    break;
                }
            }
        }
        return isTrue;
    }

    private int getIndexOfParts(List<ListParts.Part> parts){
        int index = -1;
        int currentPartNumber = 1;
        ListParts.Part firstPart = parts.get(0);
        if(Integer.valueOf(firstPart.partNumber) != 1){
            return index;
        }
        index = 0;
        ListParts.Part tmp;
        for(int i = 1, size = parts.size(); i < size; i ++){
            tmp = parts.get(i);
            if(Integer.valueOf(tmp.partNumber) != currentPartNumber + 1){
                break;
            }else {
                index = i;
                currentPartNumber = Integer.valueOf(tmp.partNumber);
            }
        }
        return index;
    }

    protected void run() {
        updateState(TransferState.WAITING); // waiting
        //bytes or inputStream using simple upload method
        if(bytes != null || inputStream != null){
            simpleUpload(cosXmlService);
            return;
        }

        File file = new File(srcPath);
        if(!file.exists() || file.isDirectory() || !file.canRead()){
            if(updateState(TransferState.FAILED)){
                mException = new CosXmlClientException(ClientErrorCode.INVALID_ARGUMENT.getCode(), srcPath + " is invalid");
                if(cosXmlResultListener != null){
                    cosXmlResultListener.onFail(buildCOSXMLTaskRequest(putObjectRequest), (CosXmlClientException) mException, null);
                }
            }
           return;
        }
        fileLength = file.length();
        if(fileLength < multiUploadSizeDivision){
            simpleUpload(cosXmlService);
        }else {
            isSliceUpload = true;
            IS_EXIT = new AtomicBoolean(false);
            UPLOAD_PART_COUNT = new AtomicInteger(0);
            ALREADY_SEND_DATA_LEN = new AtomicLong(0);
            partStructMap = new LinkedHashMap<>(); //必须有序
            uploadPartRequestLongMap = new LinkedHashMap<>();
            multiUpload(cosXmlService);
        }
    }

    private static class SlicePartStruct{
        public int partNumber;
        public boolean isAlreadyUpload;
        public long offset;
        public long sliceSize;
        public String eTag;
    }

    private static interface MultiUploadsStateListener{
        void onInit();
        void onListParts();
        void onUploadParts();
        void onCompleted(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult);
        void onFailed(CosXmlRequest cosXmlRequest, CosXmlClientException exception, CosXmlServiceException serviceException);
    }

    public static class COSXMLUploadTaskRequest extends PutObjectRequest{

        protected COSXMLUploadTaskRequest(String region, String bucket, String cosPath, String srcPath, Map<String, List<String>> headers,
                                          Map<String, String> queryStr) {
            super(bucket, cosPath, srcPath);
            this.setRegion(region);
            this.setRequestHeaders(headers);
            this.setQueryParameters(queryStr);
        }
    }

    public static class COSXMLUploadTaskResult extends CosXmlResult{
        protected COSXMLUploadTaskResult(){}
        public String eTag;
    }

}
