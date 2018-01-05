package com.tencent.cos.xml.utils;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.network.auth.BasicQCloudCredentials;
import com.tencent.qcloud.core.network.auth.CredentialProviderConst;
import com.tencent.qcloud.core.network.auth.QCloudLifecycleCredentials;


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

    public static String getObjectUrlWithSign(boolean isHttps, String appid, String bucket, String region, String cosPath,
                                              long duration, QCloudAPI qCloudAPI) throws CosXmlClientException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(getObjectUrl(isHttps, appid, bucket, region, cosPath));
        urlBuilder.append("?sign=");

        String secretId = qCloudAPI.getSecretId();
        String secretKey = qCloudAPI.getSecretKey();
        String token = qCloudAPI.getSessionToken();
        long keyDuration = qCloudAPI.getKeyDuration();

        if(keyDuration <= 0) keyDuration = duration;
        long current = System.currentTimeMillis() / 1000;
        long expired = current + keyDuration;
        String keyTime = current + ";" + expired;
        String signKey = secretKey2SignKey(secretKey, keyTime);
        BasicQCloudCredentials basicQCloudCredentials = new BasicQCloudCredentials(secretId, signKey, keyTime);

        String sign = getSign(cosPath, duration, basicQCloudCredentials);
        sign = URLEncodeUtils.cosPathEncode(sign);
        urlBuilder.append(sign);

//        if(token != null){
//            urlBuilder.append("&token=").append(token);
//        }
        return urlBuilder.toString();
    }

    private static String secretKey2SignKey(String secretKey, String keyTime) throws CosXmlClientException {
        return DigestUtils.getHmacSha1(keyTime, secretKey);
    }

    private static String getSign(String cosPath, long duration, QCloudLifecycleCredentials qCloudLifecycleCredentials) throws CosXmlClientException {
        // 添加method
        StringBuilder formatString = new StringBuilder("get");
        formatString.append("\n");

        // 添加path
        if(!cosPath.startsWith("/")){
            cosPath = "/" + cosPath;
        }
        formatString.append(cosPath);
        formatString.append("\n");

        // 添加parameters
        formatString.append("\n");

        // 添加header，得到最终的formatString
        formatString.append("\n");

        StringBuilder stringToSign = new StringBuilder();
        // 追加 q-sign-algorithm
        stringToSign.append("sha1");
        stringToSign.append("\n");

        // 追加q-sign-time
        long currentTime = System.currentTimeMillis() / 1000;
        long expiredTime = currentTime + duration;
        String signTime = currentTime + ";" + expiredTime;
        stringToSign.append(signTime);
        stringToSign.append("\n");

        // 追加 sha1Hash(formatString)
        String formatStringSha1 = DigestUtils.getSha1(formatString.toString());
        stringToSign.append(formatStringSha1);
        stringToSign.append("\n");

        String sign = getSign(stringToSign.toString(), signTime, qCloudLifecycleCredentials);

        return sign;
    }

    private static String getSign(String source, String signTime, QCloudLifecycleCredentials qCloudLifecycleCredentials) throws CosXmlClientException {

        String signature = DigestUtils.getHmacSha1(source, qCloudLifecycleCredentials.getSignKey());
        StringBuilder authorization = new StringBuilder();

        authorization.append(CredentialProviderConst.Q_SIGN_ALGORITHM).append("=").append(CredentialProviderConst.SHA1).append("&")
                .append(CredentialProviderConst.Q_AK).append("=").append(qCloudLifecycleCredentials.getSecretId()).append("&")
                .append(CredentialProviderConst.Q_SIGN_TIME).append("=").append(signTime).append("&")
                .append(CredentialProviderConst.Q_KEY_TIME).append("=").append(qCloudLifecycleCredentials.getKeyTime()).append("&")
                .append(CredentialProviderConst.Q_HEADER_LIST).append("=").append("&")
                .append(CredentialProviderConst.Q_URL_PARAM_LIST).append("=").append("&")
                .append(CredentialProviderConst.Q_SIGNATURE).append("=").append(signature);

        return authorization.toString();
    }

    public  interface QCloudAPI{
        String getSecretKey();
        String getSecretId();
        long getKeyDuration();
        String getSessionToken();
    }
}
