package com.tencent.cos.xml;

import android.content.Context;
import android.os.Environment;

import com.tencent.cos.xml.sign.CosXmlLocalCredentialProvider;
import com.tencent.qcloud.network.auth.LocalSessionCredentialProvider;
import com.tencent.qcloud.network.auth.SessionCredential;

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
    public  CosXmlService cosXmlService;
    private static QBaseServe instance;
    private QBaseServe(Context context){
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig(appid,region);
        cosXmlServiceConfig.setSocketTimeout(450000);

        cosXmlService = new CosXmlService(context,cosXmlServiceConfig,
                new CosXmlLocalCredentialProvider("AKIDPiqmW3qcgXVSKN8jngPzRhvxzYyDL5qP","EH8oHoLgpmJmBQUM1Uoywjmv7EFzd5OJ",600));

//        cosXmlService = new CosXmlService(context,cosXmlServiceConfig,
//                new SessionCredentialProvider("AKIDf9PQDlxB4j9MK2eigOs77rLaZImzvpRo","GI6jANM61oNhndDXbQzJOFqBF3btw0sW",
//                        "c9fba28be4ee77165b70fe386b5761982244e0013",600));

//        SessionCredential sessionCredential = new SessionCredential("AKIDqTrzsVa97qJXVE4wtc5JGvLWTCftma2E", "6wfARTGo7B1bvznLdfS0IK6c5KODo9E3",
//                "e1f29203ea9299828691b6ef5d4157d64d8df9c23", 1504251657);
//        LocalSessionCredentialProvider localSessionCredentialProvider = new LocalSessionCredentialProvider(sessionCredential);
//
//        cosXmlService = new CosXmlService(context,cosXmlServiceConfig, localSessionCredentialProvider);

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
