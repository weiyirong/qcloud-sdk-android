package com.tencent.qcloud.network.auth;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.tools.HexUtils;
import com.tencent.qcloud.network.tools.QCloudEncryptTools;


/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@Deprecated
public class BasicLocalCredentialProvider extends BasicCredentialProvider {

    private String secretKey;

    private long duration;

    public BasicLocalCredentialProvider(String secretId, String secretKey, long keyDuration) {
        super(secretId);
        this.secretKey = secretKey;
        this.duration = keyDuration;
    }

    @Override
    public BasicSignaturePair signaturePair() throws QCloudException {

        long current = System.currentTimeMillis() / 1000;
        long expired = current + duration;
        String keyTime = current+";"+expired;

        return new BasicSignaturePair(secretKey2SignKey(secretKey, keyTime), keyTime);
    }

    private String secretKey2SignKey(String secretKey, String keyTime) {


        return new String(HexUtils.encodeHex(QCloudEncryptTools.hmacSha1(keyTime, secretKey))); // 用secretKey来加密keyTime
    }
}
