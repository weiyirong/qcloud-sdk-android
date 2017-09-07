package com.tencent.qcloud.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.qcloud.network.action.QCloudSignatureAction;
import com.tencent.qcloud.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.common.QCloudRequestConst;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudService {

    /**
     * 网络请求管理类
     */
    protected QCloudRequestManager requestManager;

    /**
     * 服务配置类
     */
    protected QCloudServiceConfig serviceConfig;


    /**
     * 签名管理类
     */
    protected QCloudCredentialProvider credentialProvider;

    protected Context context;

    public QCloudService(Context context, QCloudServiceConfig serviceConfig, QCloudCredentialProvider cloudCredentialProvider) {

        this.credentialProvider = cloudCredentialProvider;
        this.serviceConfig = serviceConfig;
        requestManager = new QCloudRequestManager(context, serviceConfig);
        this.context = context;

    }

    protected void buildRequest(QCloudHttpRequest request) throws QCloudException {

        request.getRequestOriginBuilder().scheme(serviceConfig.getHttpProtocol()); //
        request.getRequestOriginBuilder().hostAddFront(serviceConfig.getHttpHost());
        request.setSignatureAction(new QCloudSignatureAction(request, credentialProvider));
        String ua = serviceConfig.getUserAgent();
        if (!TextUtils.isEmpty(ua)) {
            request.getRequestOriginBuilder().header(QCloudRequestConst.USER_AGENT, ua);
        }
        request.build();
    }

    public QCloudServiceConfig getServiceConfig() {
        return serviceConfig;
    }


}
