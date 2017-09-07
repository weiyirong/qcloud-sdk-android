package com.tencent.qcloud.network.auth;

import android.text.TextUtils;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Request;

/**
 * Cos XML 接口签名类
 *
 * CosXmlCredentialProvider是一个抽象类，需要在子类中去获取签名时加密需要的signKey和keyTime。
 *
 * SDK中CosXmlLocalCredentialProvider类实现了该类，但是需要用户设置secretKey，有泄漏secretKey信息的风险。
 *
 * 该类有三个成员变量
 *
 * 1、secretId : 用户初始化CosXmlCredentialProvider时指定，对于每个请求都是一致的。
 *
 * 2、BasicSignatureSourceSerializer : 用户在初始化请求时，给请求指定SourceSerializer，本类会直接读取请求的SourceSerializer，每个请求并不一致。
 *
 * 3、BasicSignatureSourceEncryptor : 这个对于所有请求均一致，直接由CosXmlCredentialProvider进行初始化
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 *
 * <a herf https://www.qcloud.com/document/product/436/7778></a>
 */

public abstract class BasicCredentialProvider extends QCloudCredentialProvider {

    private Logger logger = LoggerFactory.getLogger(BasicCredentialProvider.class);

    protected String secretId;

    private volatile BasicSignaturePair basicSignaturePair;

    private volatile ReentrantLock lock = new ReentrantLock();

    /**
     * construction method for BasicCredentialProvider
     * @param secretId, <a herf https://www.qcloud.com/document/product/436/6225></a>
     */
    public BasicCredentialProvider(String secretId) {

        this.secretId = secretId;
    }

    /**
     * signature Pair, users must be @overrider it for getting signature pair
     * @return BasicSignaturePair, {@link BasicSignaturePair}
     * @throws QCloudException, {@link QCloudException}
     */
    public abstract BasicSignaturePair signaturePair() throws QCloudException;

    private boolean needUpdateSignaturePair() {

        if (basicSignaturePair == null) {
            QCloudLogger.debug(logger, "basic signature is null");
            return true;
        }

        QCloudLogger.debug(logger, basicSignaturePair.toString());

        String keyTime = basicSignaturePair.getKeyTime(); // timestamp:expireTime;
        if (TextUtils.isEmpty(keyTime)) {
            return true;
        }
        String[] times = keyTime.split(";");
        if (times.length != 2) {
            return true;
        }
        String expire = times[1];
        long expireTime = Long.valueOf(expire);
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime > expireTime - 60) {
            return true;
        }
        return false;
    }

    /**
     * 计算签名
     *
     * @return get sign for request
     * @throws QCloudException, {@link QCloudException}
     */
    @Override
    public String signature(QCloudHttpRequest httpRequest) throws QCloudException {

        boolean locked;
        try {
            locked = lock.tryLock(20, TimeUnit.SECONDS);

            if (!locked) { //
                throw new QCloudException(QCloudExceptionType.CALCULATE_SIGNATURE_FAILED, "");
            }

        } catch (InterruptedException e) {
            throw new QCloudException(QCloudExceptionType.CALCULATE_SIGNATURE_FAILED, "try lock interrupted exception");
        }

        try {

            // 1、获取签名秘钥信息
            if (needUpdateSignaturePair()) {
                QCloudLogger.debug(logger, "should update signature pair");
                basicSignaturePair = signaturePair();
            } else {
                QCloudLogger.debug(logger, "no need to update signature pair");
            }
            //BasicSignaturePair signaturePair = signaturePair();

            if (needUpdateSignaturePair()) {
                throw new QCloudException(QCloudExceptionType.CALCULATE_SIGNATURE_FAILED, "update wrong signature pair");
            }
            // 2、初始化原始串拼凑器和加密器
            sourceEncryptor = new BasicSignatureSourceEncryptor(basicSignaturePair.getSignKey(), basicSignaturePair.getKeyTime());
            sourceSerializer = httpRequest.getSourceSerializer(); // 由用户直接给请求设置
            if (sourceSerializer == null) {
                QCloudLogger.debug(logger, "source serializer is null");
                return "";
            } else {
                QCloudLogger.debug(logger, "source serializer is not null");
            }

            BasicSignatureSourceSerializer xmlSourceSerializer = (BasicSignatureSourceSerializer) sourceSerializer;
            BasicSignatureSourceEncryptor xmlSourceEncryptor = (BasicSignatureSourceEncryptor) sourceEncryptor;
            xmlSourceSerializer.setHttpRequest(httpRequest);

            // 3、计算签名
            StringBuilder authorization = new StringBuilder();

            String signature = xmlSourceEncryptor.signature(xmlSourceSerializer.source());

            authorization.append(CredentialProviderConst.Q_SIGN_ALGORITHM).append("=").append(CredentialProviderConst.SHA1).append("&")
                    .append(CredentialProviderConst.Q_AK).append("=").append(secretId).append("&")
                    .append(CredentialProviderConst.Q_SIGN_TIME).append("=").append(xmlSourceSerializer.getSignTime()).append("&")
                    .append(CredentialProviderConst.Q_KEY_TIME).append("=").append(xmlSourceEncryptor.getKeyTime()).append("&")
                    .append(CredentialProviderConst.Q_HEADER_LIST).append("=").append(xmlSourceSerializer.getRealHeaderList().toLowerCase()).append("&")
                    .append(CredentialProviderConst.Q_URL_PARAM_LIST).append("=").append(xmlSourceSerializer.getRealParameterList().toLowerCase()).append("&")
                    .append(CredentialProviderConst.Q_SIGNATURE).append("=").append(signature);

            QCloudLogger.debug(logger, "authorization is " + authorization.toString());

            return authorization.toString();

        } catch (QCloudException exception) {

            throw exception;

        }finally {

            lock.unlock();

        }
    }
}
