package com.tencent.qcloud.network.tools;

import android.text.TextUtils;

import com.tencent.qcloud.network.assist.ContentRange;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudStringTools {

    public static byte[] hmacSha1(String url, String secreteKey) {

        byte[] hmacSha1 = null;
        try {
            byte[] byteKey = secreteKey.getBytes("utf-8");
            SecretKey hmacKey = new SecretKeySpec(byteKey, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(hmacKey);
            hmacSha1 = mac.doFinal(url.getBytes("utf-8"));

        } catch (UnsupportedEncodingException e) {

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return hmacSha1;
    }

    // 对非 '/' 字符进行URLEncoder编码
    public static String urlEncode(String fileId) throws UnsupportedEncodingException {

        if (!(TextUtils.isEmpty(fileId) && !fileId.equals("/"))) {
            // 去掉前置 "/"，好进行统一处理，最后的"/"不影响结果
            if (fileId.startsWith("/")) { // 则必然至少包含两个字符
                fileId = fileId.substring(1);
            }

            String[] paras = fileId.split("/");
            for (int i = 0; i < paras.length; i++) {
                paras[i] = URLEncoder.encode(paras[i], "utf-8");
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String para : paras) {
                stringBuilder.append("/");
                stringBuilder.append(para);
            }
            if (fileId.endsWith("/")) {
                stringBuilder.append("/");
            }
            fileId = stringBuilder.toString();
        }

        return fileId;
    }


    public static ContentRange contentRange(String contentRange) {

        if (TextUtils.isEmpty(contentRange)) {
            return null;
        }
        int lastBlankIndex = contentRange.lastIndexOf(" ");
        int acrossIndex = contentRange.indexOf("-");
        int slashIndex = contentRange.indexOf("/");
        if (lastBlankIndex == -1 || acrossIndex == -1 || slashIndex == -1) {
            return null;
        }

        long start = Long.parseLong(contentRange.substring(lastBlankIndex+1, acrossIndex));
        long end = Long.parseLong(contentRange.substring(acrossIndex+1, slashIndex));
        long max = Long.parseLong(contentRange.substring(slashIndex+1));

        return new ContentRange(start, end, max);
    }
}
