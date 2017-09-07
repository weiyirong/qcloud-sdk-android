package com.tencent.cos.xml.utils;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bradyxiao on 2017/2/28.
 * author bradyxiao
 * 借助java sha1算法
 * 简便
 */
public class SHA1Utils {
    public static String getSHA1FromBytes(byte[] data, int offset, int len) throws Exception{
        if(data == null || len <= 0 || offset < 0){
            throw new IllegalArgumentException("data == null | len <= 0 |" +
                    "offset < 0 |offset >= len");
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(data,offset,len);
            return StringUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
           throw e;
        }catch (OutOfMemoryError e){
            throw e;
        }
    }

    public static String getSHA1FromString(String content) throws Exception{
        try {
            byte[] data = content.getBytes("utf-8");
            return getSHA1FromBytes(data,0,data.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSHA1FromPath(String filePath) throws Exception{
        FileInputStream fileInputStream = new FileInputStream(filePath);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[64 * 1024];
            int len;
            while((len = fileInputStream.read(buffer,0,buffer.length)) != -1){
                messageDigest.update(buffer,0,len);
            }
            return StringUtils.toHexString(messageDigest.digest());
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }finally {
            fileInputStream.close();

        }
    }

}
