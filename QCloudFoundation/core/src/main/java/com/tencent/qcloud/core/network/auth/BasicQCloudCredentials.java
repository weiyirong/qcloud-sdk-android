package com.tencent.qcloud.core.network.auth;

/**
 * Created by wjielai on 2017/9/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class BasicQCloudCredentials implements QCloudLifecycleCredentials {

    private final String secretId;
    private final String signKey;
    private final String keyTime;

    /**
     * Constructs a new BasicQCloudCredentials object
     *
     * @param secretId The QCloud secretId.
     * @param signKey The QCloud signKey.
     * @param keyTime The QCloud keyTime.
     */
    public BasicQCloudCredentials(String secretId, String signKey, String keyTime) {
        if (secretId == null) {
            throw new IllegalArgumentException("secretId cannot be null.");
        }
        if (signKey == null) {
            throw new IllegalArgumentException("signKey cannot be null.");
        }
        if (keyTime == null) {
            throw new IllegalArgumentException("keyTime cannot be null.");
        }

        this.secretId = secretId;
        this.signKey = signKey;
        this.keyTime = keyTime;
    }

    @Override
    public String getKeyTime() {
        return keyTime;
    }

    @Override
    public String getSignKey() {
        return signKey;
    }

    @Override
    public String getSecretId() {
        return secretId;
    }
}
