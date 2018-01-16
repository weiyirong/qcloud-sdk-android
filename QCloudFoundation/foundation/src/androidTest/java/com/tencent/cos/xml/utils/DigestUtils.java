package com.tencent.cos.xml.utils;

import com.tencent.cos.xml.exception.CosXmlClientException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bradyxiao on 2017/12/28.
 */

public class DigestUtils {

    public static String getMD5(String filePath) throws CosXmlClientException {
        if(filePath == null) throw new CosXmlClientException("file Path is null");
        File file = new File(filePath);
        if(!file.exists()) throw new CosXmlClientException("file Path is not exist");
        String md5;
        FileInputStream fileInputStream = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 32];
            int len = -1;
            while ((len = fileInputStream.read(buffer)) != -1){
                messageDigest.update(buffer, 0, len);
            }
            md5 = StringUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new CosXmlClientException(e);
        } catch (FileNotFoundException e) {
            throw new CosXmlClientException(e);
        } catch (IOException e) {
            throw new CosXmlClientException(e);
        }finally {
            CloseUtil.closeQuietly(fileInputStream);
        }
        return md5;
    }

    public static String getSha1(String content) throws CosXmlClientException {
        String sha1;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            sha1 = StringUtils.toHexString(messageDigest.digest(content.getBytes(
                    Charset.forName("UTF-8"))));
        } catch (NoSuchAlgorithmException e) {
            throw new CosXmlClientException(e);
        }
        return sha1;
    }

    public static String getSHA1FromPath(String filePath) throws CosXmlClientException {
        String sha1;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[64 * 1024];
            int len;
            while((len = fileInputStream.read(buffer,0,buffer.length)) != -1){
                messageDigest.update(buffer,0,len);
            }
            sha1 = StringUtils.toHexString(messageDigest.digest());
        } catch (FileNotFoundException e) {
            throw new CosXmlClientException(e);
        } catch (IOException e) {
            throw new CosXmlClientException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CosXmlClientException(e);
        }finally {
            CloseUtil.closeQuietly(fileInputStream);
        }
        return sha1;
    }

    public static String getSHA1FromBytes(byte[] data, int offset, int len) throws CosXmlClientException{
        String sha1;
        if(data == null || len <= 0 || offset < 0){
            throw new CosXmlClientException("data == null | len <= 0 |" +
                    "offset < 0 |offset >= len");
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(data,offset,len);
            sha1 = StringUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new CosXmlClientException(e);
        }catch (OutOfMemoryError e){
            throw new CosXmlClientException(e);
        }
        return sha1;
    }

    public static String getHmacSha1(String content, String key) throws CosXmlClientException {
       String hmacSha1;
       try {
           byte[] byteKey = key.getBytes(Charset.forName("UTF-8"));
           SecretKey hmacKey = new SecretKeySpec(byteKey, "HmacSHA1");
           Mac mac = Mac.getInstance("HmacSHA1");
           mac.init(hmacKey);
           hmacSha1 = StringUtils.toHexString(mac.doFinal(content.getBytes(
                   Charset.forName("UTF-8"))));
       } catch (NoSuchAlgorithmException e) {
           throw new CosXmlClientException(e);
       } catch (InvalidKeyException e) {
           throw new CosXmlClientException(e);
       }
       return hmacSha1;
    }
}
