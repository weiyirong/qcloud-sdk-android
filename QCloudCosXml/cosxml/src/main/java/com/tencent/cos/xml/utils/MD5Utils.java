package com.tencent.cos.xml.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bradyxiao on 2017/7/18.
 * author bradyxiao
 */
public class MD5Utils {
    public static String getMD5FromBytes(byte[] data, int offset, int len) throws Exception{
        if(data == null || len <= 0 || offset < 0){
            throw new IllegalArgumentException("data == null | len <= 0 |" +
                    "offset < 0 |offset >= len");
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(data,offset,len);
            return StringUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }catch (OutOfMemoryError e){
            throw e;
        }
    }

    public static String getMD5FromString(String content) throws Exception{
        if(content == null){
            return null;
        }
        try {
            byte[] data = content.getBytes("utf-8");
            return getMD5FromBytes(data,0,data.length);
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    public static String getMD5FromPath(String filePath) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[64 * 1024];
            int len;
            while ((len = fileInputStream.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            return StringUtils.toHexString(messageDigest.digest());
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } finally {
            fileInputStream.close();
        }
    }
}
