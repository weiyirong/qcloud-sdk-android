package com.tencent.qcloud.network.tools;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudEncryptTools {

    public static byte[] hmacSha1(String url, String secreteKey) {

        byte[] hmacSha1 = null;
        try {
            byte[] byteKey = secreteKey.getBytes("utf-8");
            SecretKey hmacKey = new SecretKeySpec(byteKey, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(hmacKey);
            hmacSha1 = mac.doFinal(url.getBytes("utf-8"));

        } catch (UnsupportedEncodingException e) {

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return hmacSha1;
    }
}
