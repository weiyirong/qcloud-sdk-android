package com.tencent.qcloud.core.network;


import com.tencent.qcloud.core.network.action.QCloudRequestAction;
import com.tencent.qcloud.core.network.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.response.serializer.ResponseBodySerializer;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudHttpRequest<T extends QCloudResult> extends QCloudRequest<T> {

    /**
     * 初始化请求需要的一系列耗时任务，如获取签名、计算文件SHA1值等
     */
    protected List<QCloudRequestAction> requestActions;

    /**
     * 反序列化Response Body
     */
    protected ResponseBodySerializer<T> responseBodySerializer;

    /**
     * 上行请求的参数
     */
    protected QCloudHttpRequestBuilder requestOriginBuilder;

    protected QCloudSignSourceProvider signSourceProvider;

    private String signerType;

    private boolean isBuildSuccess;

    public QCloudHttpRequest() {
        requestActions = new LinkedList<>();
        requestOriginBuilder = new QCloudHttpRequestBuilder();
    }

    protected QCloudHttpRequest setSignerType(String signerType) {
        this.signerType = signerType;
        return this;
    }

    public String getSignerType() {
        return signerType;
    }

    void setBuildSuccess() {
        isBuildSuccess = true;
    }

    boolean isBuildSuccess() {
        return isBuildSuccess;
    }

    /**
     * 由子类实现，用于获取请求需要的部分参数
     */
    abstract protected void build() throws QCloudClientException;

    public QCloudSignSourceProvider getSignSourceProvider() {
        return signSourceProvider;
    }
}
