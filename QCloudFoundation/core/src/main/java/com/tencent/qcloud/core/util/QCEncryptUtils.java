package com.tencent.qcloud.core.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCEncryptUtils {

    private static final String HMAC_ALGORITHM = "HmacSHA1";

    public static byte[] hmacSha1(String source, String secretKey) {
        byte[] hmacSha1 = null;
        try {
            byte[] byteKey = QCStringUtils.getBytesUTF8(secretKey);
            SecretKey hmacKey = new SecretKeySpec(byteKey, HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(hmacKey);
            hmacSha1 = mac.doFinal(QCStringUtils.getBytesUTF8(source));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return hmacSha1;
    }
}
