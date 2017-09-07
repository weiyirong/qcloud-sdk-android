package com.tencent.qcloud.network.auth;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.HexUtils;
import com.tencent.qcloud.network.tools.QCloudStringTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class BasicSignatureSourceEncryptor implements QCloudSignatureSourceEncryptor {

    private Logger logger = LoggerFactory.getLogger(BasicSignatureSourceEncryptor.class);

    private String keyTime;

    private String signKey;

    public BasicSignatureSourceEncryptor(String signKey, String keyTime) {

        this.signKey = signKey;
        this.keyTime = keyTime;
    }

    /**
     *
     * @return
     */
    public String signature(String source) throws QCloudException {

        QCloudLogger.debug(logger, "source is \r\n{}", source);
        QCloudLogger.debug(logger, "sign key is {}", signKey);

        byte[] sha1Bytes = QCloudStringTools.hmacSha1(source, signKey);
        String signature = "";
        if (sha1Bytes != null) {
            signature = new String(HexUtils.encodeHex(sha1Bytes));
        }
        QCloudLogger.debug(logger, "signature is {}", signature);
        return signature;
    }

    public String getKeyTime() {
        return keyTime;
    }


    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public void setKeyTime(String keyTime) {
        this.keyTime = keyTime;
    }

}
