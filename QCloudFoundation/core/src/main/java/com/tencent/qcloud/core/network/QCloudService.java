package com.tencent.qcloud.core.network;

import android.content.Context;

import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.network.action.QCloudSignatureAction;
import com.tencent.qcloud.core.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestStreamBodySerializer;
import com.tencent.qcloud.core.util.QCStringUtils;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudService {

    /**
     * 网络请求管理类
     */
    private final QCloudRequestManager requestManager;

    /**
     * 服务配置类
     */
    private final QCloudServiceConfig serviceConfig;

    /**
     * credential provider
     */
    private final QCloudCredentialProvider credentialProvider;

    /**
     * 配置metadata
     */
    private final QCloudMetadata metadata;

    /**
     * app context
     */
    private final Context context;

    public static final class Builder {
        private QCloudServiceConfig serviceConfig;
        private QCloudMetadata metadata;
        private QCloudCredentialProvider credentialProvider;
        private Context context;

        public Builder(Context context) {
            metadata = QCloudMetadata.with(context);
            QCloudLogger.setUp(context);
            this.context = context.getApplicationContext();
        }

        public Builder serviceConfig(QCloudServiceConfig serviceConfig) {
            this.serviceConfig = serviceConfig;
            QCloudLogger.setDebuggable(serviceConfig.debuggable);
            return this;
        }

        public Builder credentialProvider(QCloudCredentialProvider credentialProvider) {
            this.credentialProvider = credentialProvider;
            return this;
        }

        public QCloudService build() {
            if (serviceConfig == null) {
                throw new IllegalArgumentException("no service config");
            }
            if (credentialProvider == null) {
                throw new IllegalArgumentException("no credential provider");
            }
            return new QCloudService(this);
        }
    }

    QCloudService(Builder builder) {
        this.metadata = builder.metadata;
        this.serviceConfig = builder.serviceConfig;
        this.credentialProvider = builder.credentialProvider;
        this.context = builder.context;

        this.requestManager = new QCloudRequestManager(serviceConfig);
    }

    public QCloudMetadata metadata() {
        return metadata;
    }

    public QCloudServiceConfig serviceConfig() {
        return serviceConfig;
    }

    public <T extends QCloudResult> T execute(QCloudHttpRequest<T> request) throws QCloudClientException {
        buildRequest(request);
        return requestManager.send(request);
    }

    public <T extends QCloudResult> QCloudCall enqueue(QCloudHttpRequest<T> request,
                                                       QCloudResultListener<QCloudHttpRequest<T>, T> listener) {
        try {
            buildRequest(request);
            return requestManager.send(request, listener);
        } catch (QCloudClientException e) {
            listener.onFailed(request, e, null);
        }

        return null;
    }

    public void cancelAll() {
        requestManager.cancelAll();
    }

    public void cancel(QCloudHttpRequest request) {
        requestManager.cancel(request);
    }

    public void release() {
        requestManager.release();
    }

    private void buildRequest(QCloudHttpRequest request) throws QCloudClientException {
        if (!request.isBuildSuccess()) {
            request.build();
            request.requestOriginBuilder.header(QCloudNetWorkConstants.HttpHeader.HOST, request.requestOriginBuilder.getHost());

            String ua = serviceConfig.getUserAgent();
            if (!QCStringUtils.isEmpty(ua)) {
                request.requestOriginBuilder.header(QCloudNetWorkConstants.HttpHeader.USER_AGENT, ua);
            }

            if (request.signSourceProvider != null) {
                request.requestActions.add(new QCloudSignatureAction(credentialProvider, request.getSignerType()));
            }

            if (request.requestOriginBuilder.requestBodySerializer instanceof RequestStreamBodySerializer) {
                RequestStreamBodySerializer serializer = (RequestStreamBodySerializer) request.requestOriginBuilder.requestBodySerializer;
                serializer.setTmpFileDir(context.getExternalCacheDir());
            }

            request.setBuildSuccess();
        }
    }
}
