package com.tencent.cos.xml.transfer;

import android.util.Log;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.http.HttpTaskMetrics;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by bradyxiao on 2018/8/23.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public abstract class COSXMLTask {
    /** CosXmlService */
    protected CosXmlSimpleService cosXmlService;
    protected String region;
    protected String bucket;
    protected String cosPath;
    /** 返回 cosXmlResult  or  throw Exception */
    protected CosXmlResult mResult;
    protected Exception mException;

    /** url query 属性 */
    protected Map<String, String> queries;

    /** header 属性 */
    protected Map<String, List<String>> headers;
    /** 是否需要计算 MD5 */
    protected boolean isNeedMd5 = true;
    /** register some callback */
    protected CosXmlProgressListener cosXmlProgressListener;
    protected CosXmlResultListener cosXmlResultListener;
    protected TransferStateListener transferStateListener;
    /** cosxml task state during the whole lifecycle */
    protected TransferState taskState  = TransferState.WAITING;

//    protected volatile boolean IS_CANCELED = false;

    /** 直接提供签名串 */
    protected OnSignatureListener onSignatureListener;

    /** 获取 http metrics */
    protected OnGetHttpTaskMetrics onGetHttpTaskMetrics;

    protected void setCosXmlService(CosXmlSimpleService cosXmlService){
        this.cosXmlService = cosXmlService;
    }

    public void setCosXmlProgressListener(CosXmlProgressListener cosXmlProgressListener){
        this.cosXmlProgressListener = cosXmlProgressListener;
    }

    public void setCosXmlResultListener(CosXmlResultListener cosXmlResultListener){
        this.cosXmlResultListener = cosXmlResultListener;
        if(cosXmlResultListener != null){
            if(mResult != null){
                cosXmlResultListener.onSuccess(buildCOSXMLTaskRequest(null), mResult);
            }
            if(mException != null){
                if(mException instanceof CosXmlClientException){
                    cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), (CosXmlClientException) mException, null);
                }else {
                    cosXmlResultListener.onFail(buildCOSXMLTaskRequest(null), null, (CosXmlServiceException) mException);
                }
            }
        }
    }

    public void setTransferStateListener(TransferStateListener transferStateListener){
        this.transferStateListener = transferStateListener;
        if(this.transferStateListener != null){
            this.transferStateListener.onStateChanged(taskState);
        }
    }

    public void setOnSignatureListener(OnSignatureListener onSignatureListener){
        this.onSignatureListener = onSignatureListener;
    }

    public void setOnGetHttpTaskMetrics(OnGetHttpTaskMetrics onGetHttpTaskMetrics){
        this.onGetHttpTaskMetrics = onGetHttpTaskMetrics;
    }

    protected void getHttpMetrics(CosXmlRequest cosXmlRequest, final String requestName){
        if(onGetHttpTaskMetrics != null){
            cosXmlRequest.attachMetrics(new HttpTaskMetrics(){
                @Override
                public void onDataReady() {
                    super.onDataReady();
                    onGetHttpTaskMetrics.onGetHttpMetrics(requestName, this);
                }
            });
        }
    }

    public abstract void pause();

    public abstract void cancel();

    public abstract void resume();

    public TransferState getTaskState() {
        return taskState;
    }

    public CosXmlResult getResult(){
        return mResult;
    }

    public Exception getException(){
        return mException;
    }

    protected abstract CosXmlRequest buildCOSXMLTaskRequest(CosXmlRequest sourceRequest); // 构造COSXMLTask返回的Request

    protected abstract CosXmlResult buildCOSXMLTaskResult(CosXmlResult sourceResult); //构造COSXMLTask返回的Result

    /**
     * waiting: 准备状态, 任何状态都可以转为它, task 准备执行.
     * in_progress: 运行状态，只能由waiting转为它, task 执行中.
     * complete: 完成状态，只能由 in_progress状态转为它, task 执行完.
     * failed: 失败状态，只能由waiting、in_progress状态转为它, task 执行失败.
     * pause: 暂停状态，只能由waiting、in_progress状态转为它, task 暂停执行.
     * cancel: 取消状态，除了complete之外，任何状态都可以转为它, task 取消，并释放资源.
     * resume_waiting: 恢复状态，只能由 pause、failed. 这是一个特殊的状态，会直接过渡到waiting, task 恢复执行.
     * @param newTaskState new state for operating
     * @return boolean
     */
    protected synchronized boolean updateState(TransferState newTaskState){
        switch (newTaskState){
            case WAITING:
                if(taskState != TransferState.WAITING){
                    taskState = TransferState.WAITING;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case IN_PROGRESS:
                if(taskState == TransferState.WAITING){
                    taskState = TransferState.IN_PROGRESS;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case COMPLETED:
                if(taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.COMPLETED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case FAILED:
                if(taskState == TransferState.WAITING
                        || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.FAILED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case PAUSED:
                if(taskState == TransferState.WAITING
                        || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.PAUSED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case CANCELED:
                if(taskState != TransferState.CANCELED
                        && taskState != TransferState.COMPLETED){
                    taskState = TransferState.CANCELED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case RESUMED_WAITING:
                if(taskState == TransferState.PAUSED
                        || taskState == TransferState.FAILED){
                    taskState = TransferState.RESUMED_WAITING;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Deprecated
    public interface OnSignatureListener {
        /**
         * @param cosXmlRequest request
         * @return String
         * @see com.tencent.cos.xml.model.object.HeadObjectRequest
         * @see PutObjectRequest
         * @see InitMultipartUploadRequest
         * @see ListPartsRequest
         * @see UploadPartRequest
         * @see CompleteMultiUploadRequest
         * @see AbortMultiUploadRequest
         */
        String onGetSign(CosXmlRequest cosXmlRequest);
    }

    public interface OnGetHttpTaskMetrics{
        void onGetHttpMetrics(String requestName, HttpTaskMetrics httpTaskMetrics);
    }
}
