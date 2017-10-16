package com.tencent.qcloud.core;

import android.content.Context;
import android.os.Environment;

import com.tencent.qcloud.core.cos.CosXmlService;
import com.tencent.qcloud.core.cos.CosXmlServiceConfig;
import com.tencent.qcloud.core.network.auth.LocalCredentialProvider;
import com.tencent.qcloud.core.network.auth.LocalSessionCredentialProvider;
import com.tencent.qcloud.core.util.QCTestAccountReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class QBaseServe {
    public String bucket = "androidsample";
    public String region = "cn-south";
    public String appId = QCTestAccountReader.getAppId();   // 请替换成你的app id
    public String secretId = QCTestAccountReader.getSecretId();  // 请替换成你的secret id
    public String secretKey = QCTestAccountReader.getSecretKey();  // 请替换成你的secret key
    public CosXmlService cosXmlService;
    public MockResponseInterceptor mockResponseInterceptor;
    private static QBaseServe instance;
    private static QBaseServe secondInstance;
    private QBaseServe(Context context, boolean isSessionCredential){
        mockResponseInterceptor = new MockResponseInterceptor();
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appId, region)
                .setConnectionTimeout(15000)
                .setSocketTimeout(15000)
                .setMaxRetryCount(3)
                .setDebuggable(true)
                .addNetworkInterceptor(mockResponseInterceptor)
                .build();

        if (isSessionCredential) {
            cosXmlService = new CosXmlService(context,cosXmlServiceConfig,
                    new LocalSessionCredentialProvider(secretId,secretKey,appId, cosXmlServiceConfig.getUserAgent()));
        } else {
            cosXmlService = new CosXmlService(context,cosXmlServiceConfig,
                new LocalCredentialProvider(secretId,secretKey,600));
        }
    }

    public Request emptyRequest() {
        return new Request.Builder().url("http://www.qcloud.com").build();
    }

    public static QBaseServe getInstance(Context context){
        synchronized (QBaseServe.class){
            if(instance == null){
                instance = new QBaseServe(context, true);
            }
        }
        return instance;
    }

    public static QBaseServe getSecondInstance(Context context){
        synchronized (QBaseServe.class){
            if(secondInstance == null){
                secondInstance = new QBaseServe(context, false);
            }
        }
        return secondInstance;
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
