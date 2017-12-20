package com.tencent.qcloud.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bradyxiao on 2017/7/21.
 * author bradyxiao
 */
public class QCDigestUtils {

    public static final String DIGEST_ALGORITHM_MD5 = "MD5";
    public static final String DIGEST_ALGORITHM_SHA1 = "SHA-1";
    public static final String DIGEST_ALGORITHM_SHA256 = "SHA-256";
    public static final String DIGEST_ALGORITHM_SHA384 = "SHA-384";
    public static final String DIGEST_ALGORITHM_SHA512 = "SHA-512";

    private static final int STREAM_BUFFER_LENGTH = 1024;

    public static byte[] digest(byte[] data, String algorithm) {
        MessageDigest messageDigest = getDigest(algorithm);
        return messageDigest.digest(data);
    }

    public static byte[] digest(String data, String algorithm) {
        return digest(QCStringUtils.getBytesUTF8(data), algorithm);
    }

    public static byte[] digest(InputStream data, String algorithm) throws IOException {
        MessageDigest messageDigest = getDigest(algorithm);
        return updateDigest(messageDigest, data).digest();
    }

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[STREAM_BUFFER_LENGTH];

        for(int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH); read > -1; read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)) {
            digest.update(buffer, 0, read);
        }

        return digest;
    }
}
