package com.tencent.qcloud.network.auth;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class BasicSignaturePair {

    // secretKey
    private String signKey;

    // expect duration for secretKey, in seconds.
    private String keyTime;

    /**
     * construct method for BasicSignaturePair
     * @param signKey, <a herf https://www.qcloud.com/document/product/436/6225></a>
     * @param keyTime, in seconds, <a herf https://www.qcloud.com/document/product/436/7778></a>
     */
    public BasicSignaturePair(String signKey, String keyTime) {

        this.signKey = signKey;
        this.keyTime = keyTime;
    }

    /**
     *
     * @param keyTime
     */
    public void setKeyTime(String keyTime) {
        this.keyTime = keyTime;
    }

    /**
     *
     * @param signKey
     */
    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    /**
     *
     * @return signKey
     */
    public String getSignKey() {
        return signKey;
    }

    /**
     *
     * @return keyTime
     */
    public String getKeyTime() {
        return keyTime;
    }

    @Override
    public String toString() {
        return "BasicSignaturePair{" +
                "signKey='" + signKey + '\'' +
                ", keyTime='" + keyTime + '\'' +
                '}';
    }
}
