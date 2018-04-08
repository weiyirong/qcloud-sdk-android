package com.tencent.qcloud.core.auth;


import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpRequest;
import com.tencent.qcloud.core.http.QCloudHttpClient;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ShortTimeCredentialProvider extends BasicLifecycleCredentialProvider {

    private String secretKey;
    private long duration;
    private String secretId;

    protected HttpRequest<String> httpRequest;

    @Deprecated
    public ShortTimeCredentialProvider(String secretId, String secretKey, long keyDuration) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.duration = keyDuration;
    }

    public ShortTimeCredentialProvider(HttpRequest<String> httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    protected QCloudLifecycleCredentials fetchNewCredentials() throws QCloudClientException  {
        if (secretId != null && secretKey != null) {
            return onGetCredentialFromLocal(secretId, secretKey);
        } else if (httpRequest != null) {
            try {
                String json = QCloudHttpClient.getDefault().resolveRequest(httpRequest).executeNow().content();
                return onRemoteCredentialReceived(json);
            } catch (QCloudServiceException e) {
                throw new QCloudClientException("get session json fails", e);
            }
        }

        return null;
    }

    QCloudLifecycleCredentials onGetCredentialFromLocal(String secretId, String secretKey) throws QCloudClientException {
        // 使用本地永久秘钥计算得到临时秘钥
        long current = System.currentTimeMillis() / 1000;
        long expired = current + duration;
        String keyTime = current + ";" + expired;
        String signKey = secretKey2SignKey(secretKey, keyTime);

        return new BasicQCloudCredentials(secretId, signKey, keyTime);
    }

    protected QCloudLifecycleCredentials onRemoteCredentialReceived(String jsonContent) throws QCloudClientException {
        return null;
    }

    private String secretKey2SignKey(String secretKey, String keyTime) {
        byte[] hmacSha1 = (Utils.hmacSha1(keyTime, secretKey));
        if (hmacSha1 != null) {
            return new String(Utils.encodeHex(hmacSha1)); // 用secretKey来加密keyTime
        }

        return null;
    }
}
