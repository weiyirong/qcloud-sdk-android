package com.tencent.qcloud.core.network.action;

import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.network.auth.QCloudCredentials;
import com.tencent.qcloud.core.network.auth.QCloudSigner;
import com.tencent.qcloud.core.network.auth.SignerFactory;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

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

public class QCloudSignatureAction implements QCloudRequestAction {

    private final QCloudCredentialProvider credentialProvider;
    private final String signerType;

    public QCloudSignatureAction(QCloudCredentialProvider credentialProvider, String signerType) {
        this.credentialProvider = credentialProvider;
        this.signerType = signerType;
    }

    @Override
    public Request execute(QCloudRealCall request) throws QCloudClientException {
        QCloudCredentials credentials = credentialProvider.getCredentials();
        QCloudSigner signer = SignerFactory.getSigner(signerType);

        if (credentials == null) {
            throw new QCloudClientException("can't get credentials for provider.");
        }
        if (signer == null) {
            throw new QCloudClientException("can't get signer for type : " + signerType) ;
        }

        signer.sign(request, credentials);

        return request.getHttpRequest();
    }


}
