package com.tencent.qcloud.core.auth;

import static com.tencent.qcloud.core.auth.Utils.handleTimeAccuracy;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class SessionQCloudCredentials implements QCloudLifecycleCredentials {

    private final String secretId;
    private final String signKey;
    private final String keyTime;
    private final String token;

    /**
     * Constructs a new SessionQCloudCredentials object
     *
     * @param secretId The QCloud secretId.
     * @param secretKey The QCloud temporary secretKey.
     * @param token The QCloud token.
     * @param expiredTime The expired time of the key.
     */
    public SessionQCloudCredentials(String secretId, String secretKey, String token, long expiredTime) {
        this(secretId, secretKey, token, System.currentTimeMillis() / 1000, expiredTime);
    }

    /**
     * Constructs a new SessionQCloudCredentials object
     *
     * @param secretId The QCloud secretId.
     * @param secretKey The QCloud temporary secretKey.
     * @param token The QCloud token.
     * @param beginTime The begin time of the key.
     * @param expiredTime The expired time of the key.
     */
    public SessionQCloudCredentials(String secretId, String secretKey, String token, long beginTime, long expiredTime) {
        if (secretId == null) {
            throw new IllegalArgumentException("secretId cannot be null.");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey cannot be null.");
        }
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null.");
        }
        if (beginTime >= expiredTime) {
            throw new IllegalArgumentException("beginTime must be less than expiredTime.");
        }

        this.secretId = secretId;
        this.keyTime = getKeyTime(beginTime, expiredTime);
        this.signKey = getSignKey(secretKey, keyTime);
        this.token = token;
    }

    /**
     * Constructs a new SessionQCloudCredentials object
     *
     * @param secretId The QCloud secretId.
     * @param secretKey The QCloud temporary secretKey.
     * @param token The QCloud token.
     * @param keyTime The QCloud keyTime.
     */
    public SessionQCloudCredentials(String secretId, String secretKey, String token, String keyTime) {
        if (secretId == null) {
            throw new IllegalArgumentException("secretId cannot be null.");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey cannot be null.");
        }
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null.");
        }
        if (keyTime == null) {
            throw new IllegalArgumentException("keyTime cannot be null.");
        }

        this.secretId = secretId;
        this.keyTime = keyTime;
        this.signKey = getSignKey(secretKey, keyTime);
        this.token = token;
    }

    private String getKeyTime(long beginTime, long expiredTime) {
        return handleTimeAccuracy(beginTime) + ";" + handleTimeAccuracy(expiredTime);
    }

    private String getSignKey(String secretKey, String keyTime) {
        byte[] hmacSha1 = (Utils.hmacSha1(keyTime, secretKey));
        if (hmacSha1 != null) {
            return new String(Utils.encodeHex(hmacSha1)); // 用secretKey来加密keyTime
        }
        return null;
    }

    public String getToken() {
        return token;
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
