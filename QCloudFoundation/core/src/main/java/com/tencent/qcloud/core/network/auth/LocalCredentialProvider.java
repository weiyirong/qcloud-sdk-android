package com.tencent.qcloud.core.network.auth;


import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.util.QCHexUtils;
import com.tencent.qcloud.core.util.QCEncryptUtils;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@Deprecated
public class LocalCredentialProvider extends BasicLifecycleCredentialProvider {

    private String secretKey;
    private long duration;
    private String secretId;

    public LocalCredentialProvider(String secretId, String secretKey, long keyDuration) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.duration = keyDuration;
    }

    @Override
    protected QCloudLifecycleCredentials fetchNewCredentials() throws QCloudClientException  {
        long current = System.currentTimeMillis() / 1000;
        long expired = current + duration;
        String keyTime = current + ";" + expired;
        String signKey = secretKey2SignKey(secretKey, keyTime);

        return new BasicQCloudCredentials(secretId, signKey, keyTime);
    }

    private String secretKey2SignKey(String secretKey, String keyTime) {
        byte[] hmacSha1 = (QCEncryptUtils.hmacSha1(keyTime, secretKey));
        if (hmacSha1 != null) {
            return new String(QCHexUtils.encodeHex(hmacSha1)); // 用secretKey来加密keyTime
        }

        return null;
    }
}
