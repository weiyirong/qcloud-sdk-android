package com.tencent.cos.xml.utils;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.auth.AuthConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by bradyxiao on 2017/12/29.
 */

public class GenerateGetObjectURLUtils {

    /**
     * generate public read type url for get object request
     */
    public static String getObjectUrl(boolean isHttps, String appid, String bucket, String region, String cosPath) throws CosXmlClientException {
        StringBuilder urlBuilder = new StringBuilder();
        if(StringUtils.isEmpty(appid) || StringUtils.isEmpty(bucket) || StringUtils.isEmpty(region)
                || StringUtils.isEmpty(cosPath)){
            throw new CosXmlClientException("appid or bucket or or region or cosPath must not be null");
        }

        if(isHttps){
            urlBuilder.append("https").append("://");
        }else {
            urlBuilder.append("http").append("://");
        }
        urlBuilder.append(bucket).append("-").append(appid).append(".");
        urlBuilder.append("cos").append(".").append(region).append(".")
                .append("myqcloud.com");
        if(!cosPath.startsWith("/")){
            cosPath = "/" + cosPath;
        }
        urlBuilder.append(URLEncodeUtils.cosPathEncode(cosPath));
        return urlBuilder.toString();
    }

    public static String getRequestUrlWithSign(boolean isHttps,
                                              String httpMethod,
                                              Map<String, String> headers,
                                              Map<String, String> queryParameters,
                                              String appid, String bucket, String region, String cosPath,
                                              long duration, QCloudAPI qCloudAPI) throws CosXmlClientException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(getObjectUrl(isHttps, appid, bucket, region, cosPath));
        urlBuilder.append("?");
        String sign = getSign(httpMethod, headers, queryParameters, cosPath, duration, qCloudAPI);
        urlBuilder.append(sign);
        return urlBuilder.toString();
    }

    public static String getObjectUrlWithSign(boolean isHttps,
                                              Map<String, String> headers,
                                              Map<String, String> queryParameters,
                                              String appid, String bucket, String region, String cosPath,
                                              long duration, QCloudAPI qCloudAPI) throws CosXmlClientException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(getObjectUrl(isHttps, appid, bucket, region, cosPath));
        urlBuilder.append("?sign=");

        String sign = getSign("get", headers, queryParameters, cosPath, duration, qCloudAPI);
        sign = URLEncodeUtils.cosPathEncode(sign);
        urlBuilder.append(sign);

        return urlBuilder.toString();
    }

    private static String getSign(String httpMethod,
                                        Map<String, String> headers,
                                        Map<String, String> queryParameters,
                                        String cosPath, long signDuration, QCloudAPI qCloudAPI) throws CosXmlClientException {
        String secretId = qCloudAPI.getSecretId();
        String secretKey = qCloudAPI.getSecretKey();
        long keyDuration = qCloudAPI.getKeyDuration();

        if(keyDuration <= 0) keyDuration = signDuration;
        long current = System.currentTimeMillis() / 1000;
        long expired = current + keyDuration;
        String keyTime = current + ";" + expired;
        String signKey = DigestUtils.getHmacSha1(keyTime, secretKey);

        // 添加method
        StringBuilder formatString = new StringBuilder(httpMethod.trim().toLowerCase());
        formatString.append("\n");

        // 添加path
        if(!cosPath.startsWith("/")){
            cosPath = "/" + cosPath;
        }
        formatString.append(cosPath);
        formatString.append("\n");


        String[] sortQueryParameters = sort(queryParameters);

        // 添加parameters
        if(sortQueryParameters != null){
            formatString.append(sortQueryParameters[1]);
        }
        formatString.append("\n");

        String[] sortHeaders = sort(headers);

        // 添加header，得到最终的formatString
        if(headers != null){
            formatString.append(sortHeaders[1]);
        }
        formatString.append("\n");

        StringBuilder stringToSign = new StringBuilder();
        // 追加 q-sign-algorithm
        stringToSign.append("sha1");
        stringToSign.append("\n");

        // 追加q-sign-time
        long currentTime = System.currentTimeMillis() / 1000;
        long expiredTime = currentTime + signDuration;
        String signTime = currentTime + ";" + expiredTime;
        stringToSign.append(signTime);
        stringToSign.append("\n");

        // 追加 sha1Hash(formatString)
        String formatStringSha1 = DigestUtils.getSha1(formatString.toString());
        stringToSign.append(formatStringSha1);
        stringToSign.append("\n");

        String signature = DigestUtils.getHmacSha1(stringToSign.toString(), signKey);

        StringBuilder authorization = new StringBuilder();
        authorization.append(AuthConstants.Q_SIGN_ALGORITHM).append("=").append(AuthConstants.SHA1).append("&")
                .append(AuthConstants.Q_AK).append("=").append(secretId).append("&")
                .append(AuthConstants.Q_SIGN_TIME).append("=").append(signTime).append("&")
                .append(AuthConstants.Q_KEY_TIME).append("=").append(keyTime).append("&")
                .append(AuthConstants.Q_HEADER_LIST).append("=").append("&")
                .append(AuthConstants.Q_URL_PARAM_LIST).append("=").append("&")
                .append(AuthConstants.Q_SIGNATURE).append("=").append(signature);

        return authorization.toString();
    }

    private static String[] sort(Map<String, String> maps) throws CosXmlClientException {
        if(maps == null) return null;
        Map<String, Object> temp = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            temp.put(entry.getKey().toLowerCase().trim(), URLEncodeUtils.cosPathEncode(entry.getValue().trim()));
        }

        List<Map.Entry<String, Object>> list = new ArrayList<>(temp.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        StringBuilder keyStringBuilder = new StringBuilder();
        StringBuilder valueStringBuilder = new StringBuilder();

        for (Map.Entry<String, Object> entry : list) {
            keyStringBuilder.append(entry.getKey()).append(";");
            valueStringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        keyStringBuilder.deleteCharAt(keyStringBuilder.length() - 1);
        valueStringBuilder.deleteCharAt(valueStringBuilder.length() - 1);

        String[] strings = new String[2];
        strings[0] = keyStringBuilder.toString();
        strings[1] = valueStringBuilder.toString();
        return strings;
    }

    public  interface QCloudAPI{
        String getSecretKey();
        String getSecretId();
        long getKeyDuration();
        String getSessionToken();
    }
}
