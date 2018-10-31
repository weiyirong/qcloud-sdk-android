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
     *
     * @param isHttps true: https请求； false: http请求
     * @param appid  用户的appid
     * @param bucket 存储桶 bucekt
     * @param region bucket所在的园区
     * @param cosPath 请求的cos路径
     * @return 返回下载 url
     * @throws CosXmlClientException
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
        if(bucket.endsWith("-" + appid)){
            urlBuilder.append(bucket).append(".");
        }else {
            urlBuilder.append(bucket).append("-").append(appid).append(".");
        }
        urlBuilder.append("cos").append(".").append(region).append(".")
                .append("myqcloud.com");
        if(!cosPath.startsWith("/")){
            cosPath = "/" + cosPath;
        }
        urlBuilder.append(URLEncodeUtils.cosPathEncode(cosPath));
        return urlBuilder.toString();
    }

    /**
     *  生成带签名的请求url
     * @param isHttps true: https请求； false: http请求
     * @param httpMethod 请求方法，如 put
     * @param headers  签名中需要验证的header, 不验证填写 null
     * @param queryParameters 签名中需要验证的url中的请求参数, 不验证填写 null
     * @param appid  用户的appid
     * @param bucket 存储桶 bucekt
     * @param region bucket所在的园区
     * @param cosPath 请求的cos路径
     * @param duration 签名的有效期
     * @param qCloudAPI 云api(密钥）
     * @return 返回带签名的请求url
     * @throws CosXmlClientException
     */
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

    /**
     * 生成带签名的下载请求 url
     * @param isHttps true: https请求； false: http请求
     * @param headers  签名中需要验证的header, 不验证填写 null
     * @param queryParameters 签名中需要验证的url中的请求参数, 不验证填写 null
     * @param appid  用户的appid
     * @param bucket 存储桶 bucekt
     * @param region bucket所在的园区
     * @param cosPath 请求的cos路径
     * @param duration 签名的有效期
     * @param qCloudAPI 云api(密钥）
     * @return 返回带签名的下载请求url
     * @throws CosXmlClientException
     */
    public static String getObjectUrlWithSign(boolean isHttps,
                                              Map<String, String> headers,
                                              Map<String, String> queryParameters,
                                              String appid, String bucket, String region, String cosPath,
                                              long duration, QCloudAPI qCloudAPI) throws CosXmlClientException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(getObjectUrl(isHttps, appid, bucket, region, cosPath));
        urlBuilder.append("?");

        String sign = getSign("get", headers, queryParameters, cosPath, duration, qCloudAPI);
//        sign = URLEncodeUtils.cosPathEncode(sign);
        urlBuilder.append(sign);

        return urlBuilder.toString();
    }

    public static String getSign(String httpMethod,String cosPath,
                                 Map<String, String> headers, Map<String, String> queryParameters,
                                 String signTime, String keyTime,
                                 String secretId, String signKey) throws CosXmlClientException {
        // 添加method
        StringBuilder formatString = new StringBuilder(httpMethod.trim().toLowerCase());
        formatString.append("\n");

        // 添加path
        if(!cosPath.startsWith("/")){
            cosPath = "/" + cosPath;
        }
        formatString.append(cosPath);
        formatString.append("\n");

        // 添加parameters
        String[] sortQueryParameters = sort(queryParameters, false);
        if(sortQueryParameters != null){
            formatString.append(sortQueryParameters[1]);
        }
        formatString.append("\n");

        // 添加header，得到最终的formatString
        String[] sortHeaders = sort(headers, true);
        if(headers != null){
            formatString.append(sortHeaders[1]);
        }
        formatString.append("\n");

        StringBuilder stringToSign = new StringBuilder();

        // 追加 q-sign-algorithm
        stringToSign.append("sha1");
        stringToSign.append("\n");

        // 追加q-sign-time
        stringToSign.append(signTime);
        stringToSign.append("\n");

        // 追加 sha1Hash(formatString)
        String formatStringSha1 = DigestUtils.getSha1(formatString.toString());
        stringToSign.append(formatStringSha1);
        stringToSign.append("\n");

        //得到signature
        String signature = DigestUtils.getHmacSha1(stringToSign.toString(), signKey);

        //得到sign
        StringBuilder authorization = new StringBuilder();
        authorization.append(AuthConstants.Q_SIGN_ALGORITHM).append("=").append(AuthConstants.SHA1).append("&")
                .append(AuthConstants.Q_AK).append("=").append(secretId).append("&")
                .append(AuthConstants.Q_SIGN_TIME).append("=").append(signTime).append("&")
                .append(AuthConstants.Q_KEY_TIME).append("=").append(keyTime).append("&")
                .append(AuthConstants.Q_HEADER_LIST).append("=").append(sortHeaders != null ? sortHeaders[0] : "").append("&")
                .append(AuthConstants.Q_URL_PARAM_LIST).append("=").append(sortQueryParameters != null ? sortQueryParameters[0] : "").append("&")
                .append(AuthConstants.Q_SIGNATURE).append("=").append(signature);

        return authorization.toString();
    }

    private static String getSign(String httpMethod,
                                        Map<String, String> headers,
                                        Map<String, String> queryParameters,
                                        String cosPath, long signDuration, QCloudAPI qCloudAPI) throws CosXmlClientException {
        String secretId = qCloudAPI.getSecretId();
        String secretKey = qCloudAPI.getSecretKey();
        long keyDuration = qCloudAPI.getKeyDuration();
        String token = qCloudAPI.getSessionToken();

        if(keyDuration <= 0) keyDuration = signDuration;
        long current = System.currentTimeMillis() / 1000;
        long expired = current + keyDuration;
        String keyTime = current + ";" + expired;
        String signKey = DigestUtils.getHmacSha1(keyTime, secretKey);

//        // 添加method
//        StringBuilder formatString = new StringBuilder(httpMethod.trim().toLowerCase());
//        formatString.append("\n");
//
//        // 添加path
//        if(!cosPath.startsWith("/")){
//            cosPath = "/" + cosPath;
//        }
//        formatString.append(cosPath);
//        formatString.append("\n");
//
//
//        String[] sortQueryParameters = sort(queryParameters, false);
//
//        // 添加parameters
//        if(sortQueryParameters != null){
//            formatString.append(sortQueryParameters[1]);
//        }
//        formatString.append("\n");
//
//        String[] sortHeaders = sort(headers, true);
//
//        // 添加header，得到最终的formatString
//        if(headers != null){
//            formatString.append(sortHeaders[1]);
//        }
//        formatString.append("\n");
//
//        StringBuilder stringToSign = new StringBuilder();
//        // 追加 q-sign-algorithm
//        stringToSign.append("sha1");
//        stringToSign.append("\n");

        // 追加q-sign-time
        long currentTime = System.currentTimeMillis() / 1000;
        long expiredTime = currentTime + signDuration;
        String signTime = currentTime + ";" + expiredTime;
//        stringToSign.append(signTime);
//        stringToSign.append("\n");

//        // 追加 sha1Hash(formatString)
//        String formatStringSha1 = DigestUtils.getSha1(formatString.toString());
//        stringToSign.append(formatStringSha1);
//        stringToSign.append("\n");
//
//        String signature = DigestUtils.getHmacSha1(stringToSign.toString(), signKey);
//
//        StringBuilder authorization = new StringBuilder();
//        authorization.append(AuthConstants.Q_SIGN_ALGORITHM).append("=").append(AuthConstants.SHA1).append("&")
//                .append(AuthConstants.Q_AK).append("=").append(secretId).append("&")
//                .append(AuthConstants.Q_SIGN_TIME).append("=").append(signTime).append("&")
//                .append(AuthConstants.Q_KEY_TIME).append("=").append(keyTime).append("&")
//                .append(AuthConstants.Q_HEADER_LIST).append("=").append(sortHeaders != null ? sortHeaders[0] : "").append("&")
//                .append(AuthConstants.Q_URL_PARAM_LIST).append("=").append(sortQueryParameters != null ? sortQueryParameters[0] : "").append("&")
//                .append(AuthConstants.Q_SIGNATURE).append("=").append(signature);
//
//        return authorization.toString();
//        return getSign(httpMethod, cosPath, headers, queryParameters, signTime, keyTime, secretId, signKey);
        String sign = getSign(httpMethod, cosPath, headers, queryParameters, signTime, keyTime, secretId, signKey);
        if(token != null){
            sign += "&x-cos-security-token=" + token;
        }
        return sign;
    }

    private static String[] sort(Map<String, String> maps, boolean isHeader) throws CosXmlClientException {
        if(maps == null) return null;
        Map<String, Object> temp = new LinkedHashMap<>();
        if(isHeader){
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                temp.put(entry.getKey().toLowerCase().trim(), URLEncodeUtils.cosPathEncode(entry.getValue().trim()));
            }
        }else {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                temp.put(entry.getKey().toLowerCase().trim(), entry.getValue().toLowerCase().trim());
            }
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
        String getSecretKey(); // 密钥 secretKey
        String getSecretId(); // 密钥 secretId
        long getKeyDuration(); // secretKey的有效期
        @Deprecated
        String getSessionToken();
    }
}
