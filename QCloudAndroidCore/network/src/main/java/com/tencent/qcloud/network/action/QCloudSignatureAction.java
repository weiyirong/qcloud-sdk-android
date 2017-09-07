package com.tencent.qcloud.network.action;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.network.auth.QCloudSignatureSourceSerializer;
import com.tencent.qcloud.network.auth.LocalSessionCredentialProvider;
import com.tencent.qcloud.network.auth.SmartSessionCredentialProvider;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Request;

/**
 *
 * 这个
 *
 * 给请求添加签名签名
 *
 * 计算签名包含两个部分：
 *
 * 1、计算原始串： QCloudSignatureSourceSerializer可以由QCloudCredentialProvider统一设置，
 *               用户也可以给每个请求单独进行设置
 *
 * 2、加密原始串：统一由QCloudCredentialProvider的QCloudSignatureEncryptor进行加密。
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudSignatureAction extends QCloudRequestAction {

    private Logger logger = LoggerFactory.getLogger(QCloudSignatureAction.class);

    private QCloudCredentialProvider credentialProvider;

    public QCloudSignatureAction(QCloudHttpRequest httpRequest, QCloudCredentialProvider credentialProvider) {
        super(httpRequest);
        this.credentialProvider = credentialProvider;
    }

    @Override
    public void execute() throws Exception {

        if (credentialProvider == null) {
            return ;
        }
        QCloudSignatureSourceSerializer credentialSourceSerializer = credentialProvider.getSourceSerializer();
        QCloudSignatureSourceSerializer requestSourceSerializer = httpRequest.getSourceSerializer();

        if (requestSourceSerializer != null) {

            credentialProvider.setSourceSerializer(requestSourceSerializer);
        }

        if (credentialProvider.getSourceSerializer() != null) {

            String signature = credentialProvider.signature(httpRequest);
            Request.Builder builder = httpRequest.getHttpRequest().newBuilder();
            builder.addHeader(QCloudNetWorkConst.HTTP_HEADER_AUTHORIZATION, signature);

            if (credentialProvider instanceof SmartSessionCredentialProvider) {
                SmartSessionCredentialProvider smartSessionCredentialProvider = (SmartSessionCredentialProvider) credentialProvider;
                builder.addHeader(QCloudNetWorkConst.HTTP_HEADER_SESSION_TOKEN, smartSessionCredentialProvider.getToken());
            }
            httpRequest.setHttpRequest(builder.build());
        }



        credentialProvider.setSourceSerializer(credentialSourceSerializer);

    }

    @Override
    public String toString() {
        return "Signature Action";
    }
}
