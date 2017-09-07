package com.tencent.qcloud.network.cosv4;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.auth.KeyValuesHelper;
import com.tencent.qcloud.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.network.auth.QCloudSignatureSourceEncryptor;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.QCloudStringTools;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.tencent.qcloud.network.cosv4.CosV4Const.APPID_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.CURRENT_TIME_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.EXPIRED_TIME_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.RAND_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.SECRET_ID_KEY;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class CosV4CredentialProvider extends QCloudCredentialProvider {

    private String appid;

    private String secretId;

    /**
     * CosV4CredentialProvider对所有的请求提供了默认的原始串拼凑方式和原始串加密方式。
     *
     * 其中用户可以为请求单独修改原始串的拼凑方式来修改签名，
     * 如将请求的sourceSerializer设置为CosV4SignatureOnceSourceSerializer，则该请求会采用单次签名的方式进行签名
     *
     */
    public CosV4CredentialProvider(String appid, String secretId) {

        this.appid = appid;
        this.secretId = secretId;
    }

    public abstract String encrypt(String source) throws QCloudException;

    @Override
    public String signature(QCloudHttpRequest request) throws QCloudException {

        Map<String, String> keyValues = new HashMap<>();
        keyValues.put(APPID_KEY, appid);
        keyValues.put(SECRET_ID_KEY, secretId);

        if (sourceSerializer != null) {

            CosV4SignatureSourceSerializer cosV4SignatureSourceSerializer = (CosV4SignatureSourceSerializer) sourceSerializer;
            return encrypt(cosV4SignatureSourceSerializer.source(keyValues));
        }
        return null;
    }

}
