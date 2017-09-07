package com.tencent.qcloud.network.auth;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.tools.HexUtils;
import com.tencent.qcloud.network.tools.QCloudEncryptTools;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class SmartSessionCredentialProvider extends BasicCredentialProvider {


    protected SessionCredential sessionCredential;

    public SmartSessionCredentialProvider() {

        super(null);
    }

    @Override
    public BasicSignaturePair signaturePair() throws QCloudException {

        long currentTime = System.currentTimeMillis() / 1000; // current time in second
        String keyTime = "";
        sessionCredential = sessionCredential();

        if (sessionCredential == null) {
            throw new QCloudException(QCloudExceptionType.CALCULATE_SIGNATURE_FAILED, "SmartSessionCredentialProvider get SessionCredential failed");
        } else {
            secretId = sessionCredential.getSecretId();
        }

        keyTime = currentTime+";"+sessionCredential.getExpire();

        return new BasicSignaturePair(secretKey2SignKey(sessionCredential.getSecretKey(), keyTime), keyTime);
    }

    protected abstract SessionCredential sessionCredential() throws QCloudException;

    private String secretKey2SignKey(String secretKey, String keyTime) {


        return new String(HexUtils.encodeHex(QCloudEncryptTools.hmacSha1(keyTime, secretKey))); // 用secretKey来加密keyTime
    }

    public String getToken() {

        return sessionCredential != null ? sessionCredential.getToken() : "";
    }

    public void setSessionCredential(SessionCredential sessionCredential) {
        this.sessionCredential = sessionCredential;
    }
}