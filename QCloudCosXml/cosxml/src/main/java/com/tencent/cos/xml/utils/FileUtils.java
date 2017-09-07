package com.tencent.cos.xml.utils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by bradyxiao on 2017/6/5.
 * author bradyxiao
 */
public class FileUtils {

    public static byte[] getFileContent(String srcPath, long offset, int length ) throws IOException {
        if(offset < 0 || length < 0){
            throw new IllegalArgumentException("offset or length < 0");
        }
        FileInputStream fileInputStream = new FileInputStream(srcPath);
        byte[] data = null;
        byte[] temp = new byte[length];
        try {
            fileInputStream.skip(offset);
            int readLen = fileInputStream.read(temp,0,temp.length);
            if(readLen < 0){
                data = new byte[0];
            }else {
                if(readLen < length){
                    data = new byte[readLen];
                    System.arraycopy(temp,0,data,0,readLen);
                }else{
                    data = temp;
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            fileInputStream.close();
        }
        return data;
    }
}
