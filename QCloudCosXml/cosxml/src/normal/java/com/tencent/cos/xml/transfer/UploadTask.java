package com.tencent.cos.xml.transfer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.tencent.cos.xml.CosXmlService;
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
import java.util.HashMap;
import java.util.Iterator;
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

public class UploadTask extends Task{

    /** 满足分片上传的文件最小长度 */
    private static final long SIZE_LIMIT = 20 * 1024 * 1024;
    /** 分片大小 */
    private long sliceSize = 1024 * 1024 ;

    private String srcPath;
    private String uploadId;
    private UploadListener uploadListener;
    private long fileLength;
    private boolean isSliceUpload = false;

    private PutObjectRequest putObjectRequest;

    private InitMultipartUploadRequest initMultipartUploadRequest;
    private ListPartsRequest listPartsRequest;
    private CompleteMultiUploadRequest completeMultiUploadRequest;
    private Map<UploadPartRequest,Long> uploadPartRequestLongMap;
    private Map<Integer, SlicePartStruct> partStructMap;
    private AtomicInteger UPLOAD_PART_COUNT;
    private AtomicLong ALREADY_SEND_DATA_LEN;
    private AtomicBoolean IS_EXIT;
    private Object SYNC_UPLOAD_PART = new Object();
    private static final byte MULTI_UPLOAD_START = 0;
    private static final byte INIT_MULTI_UPLOAD = 1;
    private static final byte LIST_MULTI_UPLOAD = 2;
    private static final byte MULTI_UPLOAD_PART = 3;
    private static final byte COMPLETE_MULTI_UPLOAD = 4;
    private static final byte MULTI_UPLOAD_PAUSE = 6;
    private static final byte MULTI_UPLOAD_CANCEL = 7;
    private static final byte MULTI_UPLOAD_SUCCESS = 9;
    private static final byte MULTI_UPLOAD_FAILED = 10;
    private static Handler multiUploadHandler;
    private static HandlerThread multiUploadHandlerThread;

    public UploadTask(String region, String bucket, String cosPath, String srcPath, String uploadId, UploadListener uploadListener){
        this.region = region;
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.srcPath = srcPath;
        this.uploadId = uploadId;
        this.uploadListener = uploadListener;
    }

    public void upload(CosXmlService cosXmlService){
        updateState(TransferState.WAITING); // waiting
        File file = new File(srcPath);
        if(!file.exists() || file.isDirectory()){
            updateState(TransferState.FAILED);
            if(uploadListener != null){
                uploadListener.onError(taskId, new CosXmlClientException("srcPath not exists or is not a file"), null);
                onRemoveTaskListener.onRemove(Integer.valueOf(taskId));
                return;
            }
        }
        fileLength = file.length();
        if(fileLength < SIZE_LIMIT){
            simpleUpload(cosXmlService);
        }else {
            isSliceUpload = true;
            IS_EXIT = new AtomicBoolean(false);
            UPLOAD_PART_COUNT = new AtomicInteger(0);
            ALREADY_SEND_DATA_LEN = new AtomicLong(0);
            partStructMap = new HashMap<>();
            uploadPartRequestLongMap = new HashMap<>();
            multiUpload(cosXmlService);
        }
    }

    private void simpleUpload(CosXmlService cosXmlService){
        putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        putObjectRequest.setRegion(region);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                if(uploadListener != null){
                    uploadListener.onProgressChanged(taskId, complete, target);
                }
            }
        });
        updateState(TransferState.IN_PROGRESS); // running
        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                if(updateState(TransferState.COMPLETED)){
                    // complete -> success
                    uploadListener.onSuccess(taskId, result);
                    onRemoveTaskListener.onRemove(Integer.valueOf(taskId));
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(updateState(TransferState.FAILED)){
                   // failed -> error
                   uploadListener.onError(taskId, exception, serviceException);
                   onRemoveTaskListener.onRemove(Integer.valueOf(taskId));
               }
            }
        });
    }

    private void multiUpload(final CosXmlService cosXmlService){
        synchronized (UploadTask.class){
            if(multiUploadHandler == null){
                multiUploadHandlerThread = new HandlerThread("UPLOAD_TASK_HANDLER_THREAD");
                multiUploadHandlerThread.start();
                multiUploadHandler = new Handler(multiUploadHandlerThread.getLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what){
                            case MULTI_UPLOAD_START:
                                initSlicePart();
                                break;
                            case INIT_MULTI_UPLOAD:
                                initMultiUpload(cosXmlService);
                                break;
                            case LIST_MULTI_UPLOAD:
                                listMultiUpload(cosXmlService);
                                break;
                            case MULTI_UPLOAD_PART:
                                // get uploadId
                                multiUploadPart(cosXmlService);
                                break;
                            case COMPLETE_MULTI_UPLOAD:
                                completeMultiUpload(cosXmlService);
                                break;
                            case MULTI_UPLOAD_SUCCESS:
                                /**
                                 * 退出消息循环
                                 */
                                IS_EXIT.set(true);
                                multiUploadHandler.removeCallbacksAndMessages(null);
                                if(updateState(TransferState.COMPLETED)){
                                    if(uploadListener != null){
                                        CompleteMultiUploadResult completeMultiUploadResult = (CompleteMultiUploadResult) msg.obj;
                                        PutObjectResult putObjectResult = new PutObjectResult();
                                        putObjectResult.headers = completeMultiUploadResult.headers;
                                        putObjectResult.eTag = completeMultiUploadResult.completeMultipartUpload.eTag;
                                        putObjectResult.httpCode = completeMultiUploadResult.httpCode;
                                        putObjectResult.httpMessage = completeMultiUploadResult.httpMessage;
                                        uploadListener.onSuccess(taskId, putObjectResult);
                                    }
                                    onRemoveTaskListener.onRemove(Integer.valueOf(taskId));
                                }
                                multiUploadHandler.getLooper().quit();
                                terminate();
                                break;
                            case MULTI_UPLOAD_FAILED:
                                /**
                                 * 1、取消所有请求
                                 * 2、退出消息循环
                                 */
                                IS_EXIT.set(true);
                                multiUploadHandler.removeCallbacksAndMessages(null);
                                cancelAllRequest(cosXmlService);
                                abortMultiUpload(cosXmlService);
                                if(updateState(TransferState.FAILED)){
                                    if(uploadListener != null){
                                        if(msg.obj instanceof  CosXmlClientException){
                                            uploadListener.onError(taskId,(CosXmlClientException)msg.obj, null);
                                        } else {
                                            uploadListener.onError(taskId,null, (CosXmlServiceException)msg.obj);
                                        }
                                    }
                                    onRemoveTaskListener.onRemove(Integer.valueOf(taskId));
                                }
                                multiUploadHandler.getLooper().quit();
                                terminate();
                                break;
                            case MULTI_UPLOAD_PAUSE:
                                /**
                                 * 1、取消所有请求
                                 * 2、退出消息循环
                                 */
                                IS_EXIT.set(true);
                                multiUploadHandler.removeCallbacksAndMessages(null);
                                cancelAllRequest(cosXmlService);
                                multiUploadHandler.getLooper().quit();
                                terminate();
                                break;
                            case MULTI_UPLOAD_CANCEL:
                                /**
                                 * 1、取消所有请求
                                 * 2、发送abort请求
                                 * 3、退出消息循环
                                 */
                                IS_EXIT.set(true);
                                multiUploadHandler.removeCallbacksAndMessages(null);
                                cancelAllRequest(cosXmlService);
                                abortMultiUpload(cosXmlService);
                                multiUploadHandler.getLooper().quit();
                                terminate();
                                break;
                        }
                    }
                };
            }
        }
        updateState(TransferState.IN_PROGRESS);
        multiUploadHandler.sendEmptyMessage(MULTI_UPLOAD_START);
    }

    private void initMultiUpload(CosXmlService cosXmlService){
        initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
        initMultipartUploadRequest.setRegion(region);
        cosXmlService.initMultipartUploadAsync(initMultipartUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                // notify -> upload part
                if(IS_EXIT.get())return;
                uploadId = ((InitMultipartUploadResult)result).initMultipartUpload.uploadId;
                multiUploadHandler.sendEmptyMessage(MULTI_UPLOAD_PART);
                if(uploadListener != null){
                    uploadListener.onGetUploadId(taskId, uploadId);
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                // notify -> exit caused by failed
                if(IS_EXIT.get())return;
                Message message = multiUploadHandler.obtainMessage();
                message.what = MULTI_UPLOAD_FAILED;
                if(exception != null){
                    message.obj = exception;
                }else {
                    message.obj = serviceException;
                }
                multiUploadHandler.handleMessage(message);
            }
        });
    }

    private void listMultiUpload(CosXmlService cosXmlService){
        listPartsRequest = new ListPartsRequest(bucket, cosPath, uploadId);
        cosXmlService.listPartsAsync(listPartsRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                //update list part, then upload part.
                if(IS_EXIT.get())return;
                updateSlicePart((ListPartsResult)result);
                multiUploadHandler.sendEmptyMessage(MULTI_UPLOAD_PART);
                if(uploadListener != null){
                    uploadListener.onGetUploadId(taskId, uploadId);
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(IS_EXIT.get())return;
                Message message = multiUploadHandler.obtainMessage();
                message.what = MULTI_UPLOAD_FAILED;
                if(exception != null){
                    message.obj = exception;
                }else {
                    message.obj = serviceException;
                }
                multiUploadHandler.sendMessage(message);
            }
        });
    }

    private void multiUploadPart(CosXmlService cosXmlService){
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
                uploadPartRequestLongMap.put(uploadPartRequest, 0L);
                uploadPartRequest.setProgressListener(new CosXmlProgressListener() {
                    @Override
                    public void onProgress(long complete, long target) {
                        if(IS_EXIT.get())return;//已经上报失败了
                        try {
                            long dataLen = ALREADY_SEND_DATA_LEN.addAndGet(complete - uploadPartRequestLongMap.get(uploadPartRequest));
                            uploadPartRequestLongMap.put(uploadPartRequest, complete);
                            if(uploadListener != null){
                                uploadListener.onProgressChanged(taskId, dataLen, fileLength);
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
                                multiUploadHandler.sendEmptyMessage(COMPLETE_MULTI_UPLOAD);
                            }
                        }
                    }

                    @Override
                    public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                        if(IS_EXIT.get())return;//已经上报失败了
                        Message message = multiUploadHandler.obtainMessage();
                        message.what = MULTI_UPLOAD_FAILED;
                        if(exception != null){
                            message.obj = exception;
                        }else {
                            message.obj = serviceException;
                        }
                        multiUploadHandler.sendMessage(message);
                    }
                });
            }
        }
        if(isUploadFinished && !IS_EXIT.get()){
            multiUploadHandler.sendEmptyMessage(COMPLETE_MULTI_UPLOAD);
            if(uploadListener != null){
                uploadListener.onProgressChanged(taskId, fileLength, fileLength);
            }
        }
    }

    private void completeMultiUpload(CosXmlService cosXmlService){
        completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath,
                uploadId, null);
        for(Map.Entry<Integer, SlicePartStruct> entry : partStructMap.entrySet()){
            SlicePartStruct slicePartStruct = entry.getValue();
            completeMultiUploadRequest.setPartNumberAndETag(slicePartStruct.partNumber, slicePartStruct.eTag);
        }
        cosXmlService.completeMultiUploadAsync(completeMultiUploadRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Message message = multiUploadHandler.obtainMessage();
                message.what = MULTI_UPLOAD_SUCCESS;
                message.obj = result;
                multiUploadHandler.sendMessage(message);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if(IS_EXIT.get())return;
                Message message = multiUploadHandler.obtainMessage();
                message.what = MULTI_UPLOAD_FAILED;
                if(exception != null){
                    message.obj = exception;
                }else {
                    message.obj = serviceException;
                }
                multiUploadHandler.sendMessage(message);
            }
        });
    }

    private void cancelAllRequest(CosXmlService cosXmlService){
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
    private void abortMultiUpload(CosXmlService cosXmlService){
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

    private void terminate(){
        if(multiUploadHandlerThread != null && !multiUploadHandlerThread.isInterrupted()){
            multiUploadHandlerThread.interrupt();
        }
        multiUploadHandlerThread = null;
        multiUploadHandler = null;
    }


    @Override
    protected void pause(CosXmlService cosXmlService) {
        if(updateState(TransferState.PAUSED)){
            if(uploadListener != null){
                uploadListener.onError(taskId, new CosXmlClientException("paused by user"), null);
            }
            if(isSliceUpload){
                multiUploadHandler.sendEmptyMessage(MULTI_UPLOAD_PAUSE);
            }else {
                cosXmlService.cancel(putObjectRequest);
            }
        }
    }

    @Override
    protected void cancel(CosXmlService cosXmlService) {
        if(updateState(TransferState.CANCELED)){
            if(uploadListener != null){
                uploadListener.onError(taskId, new CosXmlClientException("cancelled by user"), null);
            }
            if(isSliceUpload){
                multiUploadHandler.sendEmptyMessage(MULTI_UPLOAD_CANCEL);
            }else {
                cosXmlService.cancel(putObjectRequest);
            }
            onRemoveTaskListener.onRemove(Integer.valueOf(taskId));
        }

    }

    @Override
    protected void resume(CosXmlService cosXmlService) {
        if(updateState(TransferState.RESUMED_WAITING)){
            upload(cosXmlService);
        }
    }



    private synchronized boolean updateState(TransferState newTaskState){
        switch (newTaskState){
            case WAITING:
                if(taskState != TransferState.WAITING && taskState != TransferState.COMPLETED
                        && taskState != TransferState.FAILED && taskState != TransferState.CANCELED){
                    taskState = TransferState.WAITING;
                    if(uploadListener != null){
                        uploadListener.onStateChanged(taskId, taskState);
                    }
                    return true;
                }
                return false;
            case IN_PROGRESS:
                if(taskState != TransferState.IN_PROGRESS){
                    taskState = TransferState.IN_PROGRESS;
                    if(uploadListener != null){
                        uploadListener.onStateChanged(taskId, taskState);
                    }
                    return true;
                }
                return false;
            case COMPLETED:
                if(taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.COMPLETED;
                    if(uploadListener != null){
                        uploadListener.onStateChanged(taskId, taskState);
                    }
                    return true;
                }
                return false;
            case FAILED:
                if(taskState == TransferState.WAITING || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.FAILED;
                    if(uploadListener != null){
                        uploadListener.onStateChanged(taskId, taskState);
                    }
                    return true;
                }
                return false;
            case PAUSED:
                if(taskState == TransferState.WAITING || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.PAUSED;
                    if(uploadListener != null){
                        uploadListener.onStateChanged(taskId, taskState);
                    }
                    return true;
                }
                return false;
            case CANCELED:
                if(taskState == TransferState.WAITING || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.CANCELED;
                    if(uploadListener != null){
                        uploadListener.onStateChanged(taskId, taskState);
                    }
                    return true;
                }
                return false;
            case RESUMED_WAITING:
                if(taskState == TransferState.PAUSED){
                    return true;
                }
                default:
                    return false;
        }
    }

    /**
     * init slice part
     */
    private void initSlicePart(){
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
            if(IS_EXIT.get())return;
            if(uploadId == null){
                multiUploadHandler.sendEmptyMessage(INIT_MULTI_UPLOAD);
            }else {
                multiUploadHandler.sendEmptyMessage(LIST_MULTI_UPLOAD);
            }
        }else {
            if(IS_EXIT.get())return;
            Message message = multiUploadHandler.obtainMessage();
            message.what = MULTI_UPLOAD_FAILED;
            message.obj = new CosXmlClientException("file size or slice size less than 0");
            multiUploadHandler.sendMessage(message);
        }
    }

    private void updateSlicePart(ListPartsResult listPartsResult){
        if(listPartsResult != null && listPartsResult.listParts != null){
            List<ListParts.Part> parts = listPartsResult.listParts.parts;
            if(parts != null){
                for(ListParts.Part part : parts){
                    if(partStructMap.containsKey(Integer.valueOf(part.partNumber))){
                        SlicePartStruct slicePartStruct = partStructMap.get(Integer.valueOf(part.partNumber));
                        slicePartStruct.isAlreadyUpload = true;
                        slicePartStruct.eTag = part.eTag;
                        UPLOAD_PART_COUNT.decrementAndGet();
                        ALREADY_SEND_DATA_LEN.addAndGet(Long.parseLong(part.size));
                    }
                }
            }
        }
    }

    private static class SlicePartStruct{
        public int partNumber;
        public boolean isAlreadyUpload;
        public long offset;
        public long sliceSize;
        public String eTag;
    }


}
