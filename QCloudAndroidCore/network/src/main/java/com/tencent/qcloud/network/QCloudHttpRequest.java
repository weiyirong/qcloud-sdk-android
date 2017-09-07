package com.tencent.qcloud.network;


import android.util.Log;

import com.tencent.qcloud.network.action.QCloudSignatureAction;
import com.tencent.qcloud.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.action.QCloudActionManager;
import com.tencent.qcloud.network.action.QCloudActionResultListener;
import com.tencent.qcloud.network.action.QCloudRequestAction;
import com.tencent.qcloud.network.auth.QCloudSignatureSourceSerializer;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.request.QCloudOkHttpRequestBuilder;
import com.tencent.qcloud.network.request.serializer.QCloudHttpRequestOrigin;
import com.tencent.qcloud.network.request.serializer.body.RequestBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseBodySerializer;
import com.tencent.qcloud.network.response.serializer.http.ResponseSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudHttpRequest extends QCloudRequest {

    private static final Logger logger = LoggerFactory.getLogger(QCloudHttpRequest.class);

    /**
     * 初始化请求需要的一系列耗时任务，如获取签名、计算文件SHA1值等
     */
    protected List<QCloudRequestAction> requestActions;

    /**
     * 序列化Request请求Body的步骤
     */
    protected RequestBodySerializer requestBodySerializer;

    // Guard by this
    int blockTaskSuccessNumber;

    /**
     * 反序列化Response请求
     */
    protected ResponseSerializer responseSerializer;

    /**
     * 反序列化Response Body
     */
    protected ResponseBodySerializer responseBodySerializer;

    /**
     * 上行请求的参数
     */
    protected QCloudOkHttpRequestBuilder requestOriginBuilder;

    /**
     * OkHttp3网络请求
     */
    Request httpRequest;

    protected Map<String, String> signKeyValues;

    /**
     * Guard by this
     */
    volatile private Call call;

    protected QCloudSignatureSourceSerializer sourceSerializer;


    public QCloudHttpRequest() {

        requestActions = new LinkedList<>();
        requestBodySerializer = null;
        responseBodySerializer = null;
        responseSerializer = null;

        requestOriginBuilder = new QCloudOkHttpRequestBuilder();

        signKeyValues = new HashMap<>();

        call = null;
        blockTaskSuccessNumber = 0;
    }

    /**
     * 由子类实现，用于获取请求需要的部分参数
     */
    abstract public void build();

    /**
     * 由RequestManager调用，调用后完成后业务请求才真正的构造完毕
     * <p>
     * 异步操作
     */

    void serialize(final QCloudActionManager actionManager, QCloudServiceConfig serviceConfig, final HttpRequestSerializerListener serializerListener) {
        // 1、构建基本任务
        if (requestBodySerializer != null) {
            requestOriginBuilder.body(requestBodySerializer.serialize());
        }
        httpRequest = requestOriginBuilder.build();

        // 2、执行其他耗时任务
        executeBlockActions(actionManager, serviceConfig, new QCloudActionResultListener() {
            @Override
            public void onSuccess(QCloudRequestAction action) {

                QCloudLogger.debug(logger, "serialize onSuccess");
                // 3、最后计算签名信息
                if (signatureAction != null) {
                    signature(actionManager, serializerListener);
                } else {
                    serializerListener.onSuccess(QCloudHttpRequest.this);
                }
            }

            @Override
            public void onFailed(QCloudRequestAction action, QCloudException exception) {
                QCloudLogger.debug(logger, "serialize onFailed");
                serializerListener.onFailed(QCloudHttpRequest.this, exception);
            }
        });

    }

    private QCloudRequestAction signatureAction;


    /**
     * 执行除计算签名外所有的耗时操作
     */
    private void executeBlockActions(QCloudActionManager actionManager, QCloudServiceConfig serviceConfig, final QCloudActionResultListener resultListener) {


        // 先执行一系列必要的耗时动作
        if (requestActions.size() > 0) { // 如果有需要执行的阻塞任务

            for (QCloudRequestAction action : requestActions) {
                QCloudLogger.debug(logger, "actions " +action);
            }

            for (QCloudRequestAction action : requestActions) {
                action.setActionResultListener(new QCloudActionResultListener() {
                    @Override
                    public void onSuccess(QCloudRequestAction action) {
                        QCloudLogger.debug(logger, "block task success");

                        if (hasFinishAllBlockTask(requestActions.size())) {  // 如果所有阻塞任务均已经执行成功，则继续执行发送任务，
                            QCloudLogger.debug(logger, "all block task has success");
                            resultListener.onSuccess(action);

                        } else {
                            QCloudLogger.debug(logger, "not all block task has success");
                        }
                    }

                    @Override
                    public void onFailed(QCloudRequestAction action, QCloudException exception) { // 只要有一个阻塞任务执行失败，则取消所有的阻塞任务，且不再继续执行发送任务
                        QCloudLogger.debug(logger, "block task failed");
                        cancelAllBlockTask(); //
                        resultListener.onFailed(action, exception);
                    }
                });
                Log.i("TAG", "action manager execute");
                actionManager.execute(action);
            }
        } else {

            QCloudLogger.debug(logger, "block task is null");
            resultListener.onSuccess(null);
        }
    }

    /**
     * 异步执行签名
     */
    void signature(QCloudActionManager actionManager, final HttpRequestSerializerListener serializerListener) {


        signatureAction.setActionResultListener(new QCloudActionResultListener() {
            @Override
            public void onSuccess(QCloudRequestAction action) {

                QCloudLogger.debug(logger, "calculate signature is success");

                QCloudLogger.debug(logger, "http request is build success");

                serializerListener.onSuccess(QCloudHttpRequest.this);
            }

            @Override
            public void onFailed(QCloudRequestAction action, QCloudException exception) {
                QCloudLogger.debug(logger, "calculate signature failed");
                serializerListener.onFailed(QCloudHttpRequest.this, new QCloudException(QCloudExceptionType.CALCULATE_SIGNATURE_FAILED));
            }
        });
        actionManager.execute(signatureAction);
    }

    void cancelAllBlockTask() {

        for (QCloudRequestAction requestAction : requestActions) {

            if (requestAction != null) {
                requestAction.cancel();
            }
        }
        if (signatureAction != null) {
            signatureAction.cancel();
        }
    }

    public void setSign(String sign) {

        requestOriginBuilder.header(QCloudNetWorkConst.HTTP_HEADER_AUTHORIZATION, sign);
    }

    synchronized boolean httpStart() {

        if (!super.start()) {

            return false;
        }
        //setCall(call);
        return true;
    }

    // 只会在启动任务后调用一次
    void setCall(Call call) {

        this.call = call;
    }

    Call getCall() {
        return call;
    }

    synchronized boolean httpCancel() {

        if (!super.cancel()) {
            return false;
        }

        cancelAllBlockTask();
        cancelCall();
        return true;
    }

    // 只会在调用取消操作后调用一次
    // 危险的地方在于 先发送请求，然后马上取消请求，有可能取消时call==null，然后没有调用cancel方法，然后call才开始赋值，导致取消失败

    // 于是将cancelCall置于cancel的保护下，调用若是出现上面的情况，则必不会调用setCall
    private void cancelCall() {

        if (call != null) {
            call.cancel();
        }
    }


    public Map<String, String> getSignKeyValues() {
        return signKeyValues;
    }

    public QCloudOkHttpRequestBuilder getRequestOriginBuilder() {
        return requestOriginBuilder;
    }

    public List<QCloudRequestAction> getRequestActions() {
        return requestActions;
    }

    synchronized boolean hasFinishAllBlockTask(int max) {

        blockTaskSuccessNumber++;
        if (blockTaskSuccessNumber >= max) {
            return true;
        }
        return false;
    }

    public ResponseSerializer getResponseSerializer() {
        return responseSerializer;
    }

    public ResponseBodySerializer getResponseBodySerializer() {
        return responseBodySerializer;
    }

    public QCloudSignatureSourceSerializer getSourceSerializer() {
        return sourceSerializer;
    }

    public void setSourceSerializer(QCloudSignatureSourceSerializer sourceSerializer) {
        this.sourceSerializer = sourceSerializer;
    }

    public void setSignatureAction(QCloudRequestAction signatureAction) {
        this.signatureAction = signatureAction;
    }

    public Request getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(Request httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setRequestBodySerializer(RequestBodySerializer requestBodySerializer) {
        this.requestBodySerializer = requestBodySerializer;
    }

    public RequestBodySerializer getRequestBodySerializer() {
        return requestBodySerializer;
    }
}
