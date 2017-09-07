package com.tencent.qcloud.network.cosv4;

import android.text.TextUtils;

import com.tencent.qcloud.network.auth.KeyValuesHelper;
import com.tencent.qcloud.network.auth.QCloudSignatureSourceSerializer;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.QCloudStringTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.tencent.qcloud.network.cosv4.CosV4Const.APPID_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.BUCKET_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.CURRENT_TIME_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.EXPIRED_TIME_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.RAND_KEY;
import static com.tencent.qcloud.network.cosv4.CosV4Const.SECRET_ID_KEY;

/**
 *
 * Cos V4 签名算法
 *
 * 支持单次有效签名、多次有效签名（允许设置时间）
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CosV4SignatureSourceSerializer implements QCloudSignatureSourceSerializer {

    private Logger logger = LoggerFactory.getLogger(CosV4SignatureSourceSerializer.class);
    /**
     * 签名有效时间
     *
     * 大于0为多次有效签名，否则为单次有效签名
     */
    private long duration;

    private String bucket;

    private String cosPath;


    public CosV4SignatureSourceSerializer(String bucket, String cosPath, long duration) {

        this.duration = duration;
        this.bucket = bucket;
        this.cosPath = cosPath;
    }

    /**
     *
     */
    public CosV4SignatureSourceSerializer(long duration) {

        this(null, null, duration);
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 签名需要的参数包括： appid、bucket、secret_id、time、expire、rand、file_id
     *
     * 其中
     *
     *     appid、secret_id  为CosV4CredentialProvider的参数，由CosV4CredentialProvider负责
     *
     *     bucket、file_id 为CosV4SignatureSourceSerializer的参数，由CosV4SignatureSourceSerializer负责
     *
     *     time、expire、rand 可以直接计算，由CosV4SignatureSourceSerializer负责
     */
    public String source(Map<String, String> keyValues) {


        long currentTime = System.currentTimeMillis() / 1000;
        Random random = new Random(currentTime);
        if (duration > 0) {
            keyValues.put(EXPIRED_TIME_KEY, String.valueOf(currentTime + duration));
        } else {
            keyValues.put(EXPIRED_TIME_KEY, String.valueOf(0));
        }
        keyValues.put(CURRENT_TIME_KEY, String.valueOf(currentTime));
        keyValues.put(RAND_KEY, String.valueOf(Math.abs(random.nextInt()%10000)));
        keyValues.put(BUCKET_KEY, bucket);
        // 处理FileId
        String fileId = cosPath;

        if (!TextUtils.isEmpty(fileId)) {
            try {
                fileId = QCloudStringTools.urlEncode(fileId);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            fileId = String.format(Locale.ENGLISH, "/%s/%s%s", keyValues.get(CosV4Const.APPID_KEY),
                    keyValues.get(CosV4Const.BUCKET_KEY), fileId);
        } else {
            fileId = "";
        }
        QCloudLogger.debug(logger, "file id is {}", fileId);
        keyValues.put(CosV4Const.FILED_ID, fileId);


        return KeyValuesHelper.source(keyValues);
    }



}
