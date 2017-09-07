package com.tencent.qcloud.network.auth;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class SessionCredential {

    private String secretId;

    private String secretKey;

    private String token;

    private long expire;

    public SessionCredential(String secretId, String secretKey, String token, long expire) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.token = token;
        this.expire = expire;
    }


    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
