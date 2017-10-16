package com.tencent.qcloud.core.network.auth;

import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.util.QCHexUtils;
import com.tencent.qcloud.core.util.QCEncryptUtils;

import okhttp3.Request;

/**
 * Created by wjielai on 2017/9/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class COSXmlSigner implements QCloudSigner {

    private final static String COS_SESSION_TOKEN = "x-cos-security-token";

    /**
     * 计算签名
     *
     * @throws QCloudClientException
     */
    @Override
    public void sign(QCloudRealCall realCall, QCloudCredentials credentials) throws QCloudClientException{
        COSXmlSignSourceProvider sourceProvider = (COSXmlSignSourceProvider) realCall.getSignSourceProvider();

        StringBuilder authorization = new StringBuilder();

        QCloudLifecycleCredentials lifecycleCredentials = (QCloudLifecycleCredentials) credentials;

        String signature = signature(sourceProvider.source(realCall), lifecycleCredentials.getSignKey());

        authorization.append(CredentialProviderConst.Q_SIGN_ALGORITHM).append("=").append(CredentialProviderConst.SHA1).append("&")
                .append(CredentialProviderConst.Q_AK).append("=")
                .append(credentials.getSecretId()).append("&")
                .append(CredentialProviderConst.Q_SIGN_TIME).append("=")
                .append(sourceProvider.getSignTime()).append("&")
                .append(CredentialProviderConst.Q_KEY_TIME).append("=")
                .append(lifecycleCredentials.getKeyTime()).append("&")
                .append(CredentialProviderConst.Q_HEADER_LIST).append("=")
                .append(sourceProvider.getRealHeaderList().toLowerCase()).append("&")
                .append(CredentialProviderConst.Q_URL_PARAM_LIST).append("=")
                .append(sourceProvider.getRealParameterList().toLowerCase()).append("&")
                .append(CredentialProviderConst.Q_SIGNATURE).append("=").append(signature);


        Request.Builder builder = realCall.getHttpRequest().newBuilder();
        builder.addHeader(QCloudNetWorkConstants.HttpHeader.AUTHORIZATION, authorization.toString());

        if (credentials instanceof SessionQCloudCredentials) {
            SessionQCloudCredentials sessionCredentials = (SessionQCloudCredentials) credentials;
            builder.addHeader(COS_SESSION_TOKEN, sessionCredentials.getToken());
        }

        realCall.setHttpRequest(builder.build());
    }

    private String signature(String source, String signKey) throws QCloudClientException {
        byte[] sha1Bytes = QCEncryptUtils.hmacSha1(source, signKey);
        String signature = "";
        if (sha1Bytes != null) {
            signature = new String(QCHexUtils.encodeHex(sha1Bytes));
        }
        return signature;
    }
}
