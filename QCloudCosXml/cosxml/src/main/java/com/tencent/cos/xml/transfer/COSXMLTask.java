package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;

import java.util.List;
import java.util.Map;

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
    /** 设置 QCloudSignSourceProvider */
    protected QCloudSignSourceProvider cosXmlSignSourceProvider;
    /** 是否需要计算 MD5 */
    protected boolean isNeedMd5 = false;
    /** register some callback */
    protected CosXmlProgressListener cosXmlProgressListener;
    protected CosXmlResultListener cosXmlResultListener;
    protected TransferStateListener transferStateListener;
    /** cosxml task state during the whole lifecycle */
    protected TransferState taskState  = TransferState.WAITING;

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

    protected void checkParameters(){
        if(bucket == null){
            throw new IllegalArgumentException("bucket is null");
        }
        if(cosPath == null){
            throw new IllegalArgumentException("cosPath is null");
        }
    }

    protected synchronized boolean updateState(TransferState newTaskState){
        switch (newTaskState){
            case WAITING:
                if(taskState != TransferState.WAITING && taskState != TransferState.COMPLETED
                        && taskState != TransferState.FAILED && taskState != TransferState.CANCELED){
                    taskState = TransferState.WAITING;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case IN_PROGRESS:
                if(taskState != TransferState.IN_PROGRESS){
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
                if(taskState == TransferState.WAITING || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.FAILED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case PAUSED:
                if(taskState == TransferState.WAITING || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.PAUSED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case CANCELED:
                if(taskState == TransferState.WAITING || taskState == TransferState.IN_PROGRESS){
                    taskState = TransferState.CANCELED;
                    if(transferStateListener != null){
                        transferStateListener.onStateChanged(taskState);
                    }
                    return true;
                }
                return false;
            case RESUMED_WAITING:
                if(taskState == TransferState.PAUSED){
                    taskState = TransferState.RESUMED_WAITING;
                    return true;
                }
            default:
                return false;
        }
    }
}
