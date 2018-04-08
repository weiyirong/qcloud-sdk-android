package com.tencent.qcloud.core.auth;

import android.util.Base64;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpRequest;
import com.tencent.qcloud.core.http.QCloudHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by wjielai on 2017/8/31.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class SessionCredentialProvider extends ShortTimeCredentialProvider {

    private String secretId;
    private String secretKey;
    private String appid;
    private String userAgent;
    private String region;

    @Deprecated
    public SessionCredentialProvider(String secretId, String secretKey, String appid, String region,
                                     String userAgent) {
        super(secretId, secretKey, 0);
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.appid = appid;
        this.userAgent = userAgent;
        this.region = region;
    }

    public SessionCredentialProvider(HttpRequest<String> httpRequest) {
        super(httpRequest);
    }

    @Override
    QCloudLifecycleCredentials onGetCredentialFromLocal(String secretId, String secretKey) throws QCloudClientException {
        // 使用本地永久秘钥，通过 CAM 获取临时秘钥
        try {
            httpRequest = getRequestByKey();
            String json = QCloudHttpClient.getDefault().resolveRequest(httpRequest).executeNow().content();
            return parseCAMResponse(json);
        } catch (QCloudServiceException e) {
            throw new QCloudClientException("get session json fails", e);
        }
    }

    /**
     * 默认行为是解析 CAM 的标准返回格式
     *
     * @param jsonContent 返回json数据
     * @return 临时签名
     * @throws QCloudClientException 获取签名出错的异常
     */
    @Override
    protected QCloudLifecycleCredentials onRemoteCredentialReceived(String jsonContent) throws QCloudClientException {
        return parseCAMResponse(jsonContent);
    }

    private QCloudLifecycleCredentials parseCAMResponse(String jsonContent) throws QCloudClientException {
        if (jsonContent != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);
                JSONObject data = jsonObject.optJSONObject("data");
                if (data == null) {
                    data = jsonObject;
                }
                JSONObject credentials = data.optJSONObject("credentials");
                long expiredTime = data.optLong("expiredTime");
                if (credentials != null) {
                    String sessionToken = credentials.optString("sessionToken");
                    String tmpSecretId = credentials.optString("tmpSecretId");
                    String tmpSecretKey = credentials.optString("tmpSecretKey");
                    return new SessionQCloudCredentials(tmpSecretId, tmpSecretKey, sessionToken, expiredTime);
                }
            } catch (JSONException e) {
                throw new QCloudClientException("parse session json fails", e);
            }
        }

        return null;
    }

    private HttpRequest<String> getRequestByKey() {
        String requestHost = "sts.api.qcloud.com";
        String requestPath = "/v2/index.php";
        String requestMethod = "GET";

        Map<String, String> params = new TreeMap<>();

        String policy = String.format("{\"statement\": [{\"action\": [\"name/cos:*\"],\"effect\": \"allow\"," +
                        "\"resource\":[\"qcs::cos:%s:uid/%s:prefix//%s/*\"]}],\"version\": \"2.0\"}",
                region, appid, appid);
        params.put("policy", policy);
        params.put("name", "Rabbitliu");
        params.put("Action", "GetFederationToken");
        params.put("SecretId", secretId);
        params.put("Nonce", "" + new Random().nextInt(Integer.MAX_VALUE));
        params.put("Timestamp", "" + System.currentTimeMillis() / 1000);
        params.put("RequestClient", userAgent);

        String plainText = makeSignPlainText(params, requestMethod,
                requestHost, requestPath);

        byte[] hmacSha1 = Utils.hmacSha1(plainText, secretKey);
        if (hmacSha1 != null) {
            params.put("Signature", Base64.encodeToString(hmacSha1, Base64.DEFAULT));
        }

        return new HttpRequest.Builder<String>()
                .scheme("https")
                .host(requestHost)
                .path(requestPath)
                .method(requestMethod)
                .query(params)
                .build();
    }

    private String makeSignPlainText(Map<String, String> requestParams, String requestMethod,
                                            String requestHost, String requestPath) {
        String retStr = "";
        retStr += requestMethod;
        retStr += requestHost;
        retStr += requestPath;
        retStr += buildParamStr(requestParams);

        return retStr;
    }

    private String buildParamStr(Map<String, String> requestParams) {
        StringBuilder retStr = new StringBuilder();
        for(Map.Entry<String, String> entry : requestParams.entrySet()) {
            if (retStr.length()==0) {
                retStr.append('?');
            } else {
                retStr.append('&');
            }
            retStr.append(entry.getKey().replace("_", ".")).append('=').append(entry.getValue().toString());

        }
        return retStr.toString();
    }
}
