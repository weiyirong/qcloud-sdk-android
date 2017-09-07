package com.tencent.qcloud.network.cosv4;

import android.util.Base64;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.QCloudEncryptTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@Deprecated
public class CosV4LocalCredentialProvider extends CosV4CredentialProvider {

    private Logger logger = LoggerFactory.getLogger(CosV4LocalCredentialProvider.class);

    private String secretKey;

    public CosV4LocalCredentialProvider(String appid, String secretId, String secretKey) {

        super(appid, secretId);
        this.secretKey = secretKey;
    }


    @Override
    public String encrypt(String source) throws QCloudException {

        String sign = "";
        byte[] hmacSha1 = QCloudEncryptTools.hmacSha1(source, secretKey);
        if (hmacSha1==null) {
            return "";
        }
        try {
            byte[] urlByte = source.getBytes("utf-8");
            byte[] signByte = new byte[hmacSha1.length+urlByte.length];
            System.arraycopy(hmacSha1, 0, signByte, 0, hmacSha1.length);
            System.arraycopy(urlByte, 0, signByte, hmacSha1.length, urlByte.length);
            sign = Base64.encodeToString(signByte, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new QCloudException(QCloudExceptionType.UNSUPPORTED_ENCODING, e.getMessage());
        }
        sign = sign.replaceAll("\n", "");
        QCloudLogger.info(logger, "encrypt "+sign);
        return sign;
    }

}
