package com.tencent.qcloud.core.network.auth;

import android.util.Base64;

import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.util.QCHttpParameterUtils;
import com.tencent.qcloud.core.util.QCStringUtils;
import com.tencent.qcloud.core.util.QCEncryptUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by wjielai on 2017/8/31.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@Deprecated
public class LocalSessionCredentialProvider extends BasicLifecycleCredentialProvider {

    private String secretId;
    private String secretKey;
    private String appid;
    private String userAgent;
    private String region;

    public LocalSessionCredentialProvider(String secretId, String secretKey, String appid, String region,
                                          String userAgent) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.appid = appid;
        this.userAgent = userAgent;
        this.region = region;
    }

    @Override
    protected QCloudLifecycleCredentials fetchNewCredentials() throws QCloudClientException {
        SessionQCloudCredentials sessionCredential = null;
        try {
            String json = getSession(secretId, secretKey, appid);
            if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        JSONObject credentials = data.optJSONObject("credentials");
                        long expiredTime = data.optLong("expiredTime");
                        if (credentials != null) {
                            String sessionToken = credentials.optString("sessionToken");
                            String tmpSecretId = credentials.optString("tmpSecretId");
                            String tmpSecretKey = credentials.optString("tmpSecretKey");

                            sessionCredential = new SessionQCloudCredentials(tmpSecretId, tmpSecretKey, sessionToken, expiredTime);
                        }
                    }
                } catch (JSONException e) {
                    throw new QCloudClientException("parse session json fails", e);
                }

            }
        } catch (IOException e) {
            throw new QCloudClientException("get session json fails", e);

        }

        return sessionCredential;
    }

    private String getSession(String secretId, String secretKey, String appid) throws IOException {
        String requestHost = "sts.api.qcloud.com";
        String requestPath = "/v2/index.php";
        String requestMethod = "GET";

        TreeMap<String, Object> params = new TreeMap<String, Object>();

        String policy = String.format("{\"statement\": [{\"action\": [\"name/cos:*\"],\"effect\": \"allow\"," +
                        "\"resource\":[\"qcs::cos:%s:uid/%s:prefix//%s/*\"]}],\"version\": \"2.0\"}",
                region, appid, appid);
        params.put("policy", policy);
        params.put("name", "Rabbitliu");
        params.put("Action", "GetFederationToken");
        params.put("SecretId", secretId);
        params.put("Nonce", new Random().nextInt(Integer.MAX_VALUE));
        params.put("Timestamp", System.currentTimeMillis() / 1000);
        params.put("RequestClient", userAgent);

        String plainText = makeSignPlainText(params, requestMethod,
                requestHost, requestPath);

        byte[] hmacSha1 = QCEncryptUtils.hmacSha1(plainText, secretKey);
        if (hmacSha1 != null) {
            params.put("Signature", Base64.encodeToString(hmacSha1, Base64.DEFAULT));
        }

        String url = "https://" + requestHost + requestPath;

        return sendRequest(url, params, requestMethod);
    }

    private String makeSignPlainText(TreeMap<String, Object> requestParams, String requestMethod,
                                            String requestHost, String requestPath) {
        String retStr = "";
        retStr += requestMethod;
        retStr += requestHost;
        retStr += requestPath;
        retStr += buildParamStr(requestParams);

        return retStr;
    }

    private String buildParamStr(TreeMap<String, Object> requestParams) {
        StringBuilder retStr = new StringBuilder();
        for(Map.Entry<String, Object> entry : requestParams.entrySet()) {
            if (retStr.length()==0) {
                retStr.append('?');
            } else {
                retStr.append('&');
            }
            retStr.append(entry.getKey().replace("_", ".")).append('=').append(entry.getValue().toString());

        }
        return retStr.toString();
    }

    private String sendRequest(String url, Map<String, Object> requestParams, String requestMethod) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        StringBuilder paramStr = new StringBuilder();

        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            if (paramStr.length() != 0) {
                paramStr.append('&');
            }
            paramStr.append(entry.getKey()).append('=')
                    .append(QCHttpParameterUtils.urlEncodeString(entry.getValue().toString()));
        }

        try {

            if (requestMethod.equals("GET")) {
                if (url.indexOf('?') > 0) {
                    url += '&' + paramStr.toString();
                } else {
                    url += '?' + paramStr.toString();
                }
            }
            URL realUrl = new URL(url);
            URLConnection connection = null;
            if (url.toLowerCase().startsWith("https")) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) realUrl
                        .openConnection();

                connection = httpsConn;
            } else {
                connection = realUrl.openConnection();
            }

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 设置链接主机超时时间
            connection.setConnectTimeout(1000);

            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

        } finally {
            if (in != null) {
                in.close();
            }
        }

        return result.toString();
    }
}
