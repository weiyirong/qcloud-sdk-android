package com.tencent.qcloud.core.network.auth;

import com.tencent.qcloud.core.util.QCHexUtils;
import com.tencent.qcloud.core.util.QCEncryptUtils;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class SessionQCloudCredentials implements QCloudLifecycleCredentials {

    private final String secretId;
    private final String signKey;
    private final String keyTime;
    private final String token;

    public SessionQCloudCredentials(String secretId, String secretKey, String token, long expire) {
        if (secretId == null) {
            throw new IllegalArgumentException("secretId cannot be null.");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey cannot be null.");
        }
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null.");
        }
        if (expire <= 0) {
            throw new IllegalArgumentException("expire time must be positive");
        }

        this.secretId = secretId;
        this.keyTime = getKeyTime(expire);
        this.signKey = getSignKey(secretKey, keyTime);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    private String getKeyTime(long expired) {
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime + ";" + expired;
    }

    private String getSignKey(String secretKey, String keyTime) {
        byte[] hmacSha1 = (QCEncryptUtils.hmacSha1(keyTime, secretKey));
        if (hmacSha1 != null) {
            return new String(QCHexUtils.encodeHex(hmacSha1)); // 用secretKey来加密keyTime
        }

        return null;
    }

    @Override
    public String getKeyTime() {
        return keyTime;
    }

    @Override
    public String getSecretId() {
        return secretId;
    }

    @Override
    public String getSignKey() {
        return signKey;
    }

}
