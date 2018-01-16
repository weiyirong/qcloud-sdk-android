package com.tencent.qcloud.core;

import android.content.Context;
import android.os.Environment;

import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.SessionCredentialProvider;
import com.tencent.qcloud.core.cos.CosXmlService;
import com.tencent.qcloud.core.cos.CosXmlServiceConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class QBaseServe {
    public static String bucket = "androidtest";
    public static String region = "ap-guangzhou";
    public static String appId = "1253960454";   // 请替换成你的app id
    public static String secretId = "XXX";  // 请替换成你的secret id
    public static String secretKey = "XXX";  // 请替换成你的secret key

    public CosXmlService cosXmlService;
    private CosXmlServiceConfig cosXmlServiceConfig;
    public MockResponseInterceptor mockResponseInterceptor;
    private static QBaseServe instance;

    private QBaseServe(Context context) {
        mockResponseInterceptor = new MockResponseInterceptor();
        cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appId, region)
                .build();
        cosXmlService = new CosXmlService(context, cosXmlServiceConfig,
                new SessionCredentialProvider(secretId, secretKey, appId, region, "cosxml"));
    }

    public QBaseServe(Context context, QCloudCredentialProvider credentialProvider) {
        this(context);
        cosXmlService = new CosXmlService(context, cosXmlServiceConfig, credentialProvider);
    }

    public static Request emptyRequest() {
        return new Request.Builder().url("http://www.qcloud.com").build();
    }

    public static File getLocalTempFile(Context context) {
        File localFile = new File(context.getExternalCacheDir() + File.separator + "test.txt");
        if (localFile.exists()) {
            return localFile;
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(localFile, "UTF-8");
            out.println("here is local file");
            return localFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return null;
    }

    public static QBaseServe getInstance(Context context) {
        synchronized (QBaseServe.class) {
            if (instance == null) {
                instance = new QBaseServe(context);
            }
        }
        return instance;
    }

    public static boolean isSuccess(int httpCode) {
        if (httpCode < 300 && httpCode >= 200) {
            return true;
        } else {
            return false;
        }
    }

    public static String crateFile(long length) throws Exception {
        String srcPath = Environment.getExternalStorageDirectory().getPath() + "/"
                + System.currentTimeMillis() + ".txt";
        File file = new File(srcPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.setLength(length);
                randomAccessFile.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return srcPath;
    }
}
