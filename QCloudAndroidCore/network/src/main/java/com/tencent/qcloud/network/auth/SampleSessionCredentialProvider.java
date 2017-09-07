package com.tencent.qcloud.network.auth;

import android.util.Base64;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.tools.QCloudEncryptTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
public class SampleSessionCredentialProvider extends SmartSessionCredentialProvider {

    private static final String CLIENT = "COS_XML_1.0";

    private String secretId;
    private String secretKey;
    private String appid;

    public SampleSessionCredentialProvider(String secretId, String secretKey, String appid) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.appid = appid;
    }

    @Override
    public SessionCredential sessionCredential() throws QCloudException {
        SessionCredential sessionCredential = null;
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

                        sessionCredential = new SessionCredential(tmpSecretId, tmpSecretKey, sessionToken, expiredTime);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return sessionCredential;
    }

    private String getSession(String secretId, String secretKey, String appid) {
        String requestHost = "sts.api.qcloud.com";
        String requestPath = "/v2/index.php";
        String requestMethod = "GET";

        TreeMap<String, Object> params = new TreeMap<String, Object>();

        String policy = String.format("{\"statement\": [{\"action\": [\"name/cos:*\"],\"effect\": \"allow\"," +
                        "\"resource\":[\"qcs::cos:cn-south:uid/%s:prefix//%s/*\"]}],\"version\": \"2.0\"}",
                appid, appid);
        params.put("policy", policy);
        params.put("name", "Rabbitliu");
        params.put("Action", "GetFederationToken");
        params.put("SecretId", secretId);
        params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));
        params.put("Timestamp", System.currentTimeMillis() / 1000);
        params.put("RequestClient", CLIENT);

        String plainText = makeSignPlainText(params, requestMethod,
                requestHost, requestPath);

        try {
            params.put("Signature", Base64.encodeToString(QCloudEncryptTools.hmacSha1(plainText, secretKey), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
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
        String retStr = "";
        for(String key: requestParams.keySet()) {
            if (retStr.length()==0) {
                retStr += '?';
            } else {
                retStr += '&';
            }
            retStr += key.replace("_", ".") + '=' + requestParams.get(key).toString();

        }
        return retStr;
    }

    private String sendRequest(String url, Map<String, Object> requestParams, String requestMethod) {
        String result = "";
        BufferedReader in = null;
        String paramStr = "";

        for (String key : requestParams.keySet()) {
            if (!paramStr.isEmpty()) {
                paramStr += '&';
            }
            try {
                paramStr += key + '='
                        + URLEncoder.encode(requestParams.get(key).toString(),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {

            if (requestMethod.equals("GET")) {
                if (url.indexOf('?') > 0) {
                    url += '&' + paramStr;
                } else {
                    url += '?' + paramStr;
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
                    connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return result;
    }
}
