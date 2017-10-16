package com.tencent.cos.xml;

import android.content.Context;
import android.os.Environment;



import com.tencent.qcloud.core.network.auth.LocalCredentialProvider;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class QBaseServe {
    public  String bucket = "xy2";
    public  String appid = "1253653367";
    public  String region = "cn-south";
    public  String secretId = "xxxxx";
    public  String secretKey = "xxxxx";
    public  long secretKeyDuration = 600;

    public  CosXmlService cosXmlService;
    private static QBaseServe instance;
    private QBaseServe(Context context){
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .setConnectionTimeout(45000)
                .setSocketTimeout(30000)
                .build();

        cosXmlService = new CosXmlService(context,cosXmlServiceConfig,
                new LocalCredentialProvider(secretId,secretKey,secretKeyDuration));
    }

    public static QBaseServe getInstance(Context context){
        synchronized (QBaseServe.class){
            if(instance == null){
                instance = new QBaseServe(context);
            }
        }
        return instance;
    }


    public static boolean isSuccess(int httpCode){
        if(httpCode < 300 && httpCode >= 200){
            return true;
        }else {
            return false;
        }
    }

    public static String crateFile(long length) throws Exception{
        String srcPath = Environment.getExternalStorageDirectory().getPath() + "/"
                + System.currentTimeMillis() + ".txt";
        File file = new File(srcPath);
        if(!file.exists()){
            try {
                file.createNewFile();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
                randomAccessFile.setLength(length);
                randomAccessFile.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return srcPath;
    }
}
