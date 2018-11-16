package com.tencent.cos.xml;

import android.content.Context;

import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.qcloud.core.auth.BasicQCloudCredentials;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by bradyxiao on 2018/3/12.
 */

public class QServer {

    public final static String TAG = "UnitTest";

    public final static String ownUin = "腾讯云账号";
    /** 腾讯云 cos 服务的 appid */
    public final static String appid = "腾讯云appid";
    public static final String secretId = "cos 服务密钥Id";
    public static final String secretKey = "cos 服务密钥key";
    public static final String bucketForBucketAPITest = "android-demo-ap-guangzhou";
    /** bucketForObjectAPITest 所处在的地域 */
    public final static String region = Region.AP_Guangzhou.getRegion() ;
    public static String bucketForObjectAPITest = "xmlandroidtest";
    public static CosXml cosXml;

    private QServer(Context context){
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(false)
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();
        cosXml = new CosXmlService(context, cosXmlServiceConfig,
                new ShortTimeCredentialProvider(secretId,secretKey,600) );
    }

    public static void init(Context context){
        synchronized (QServer.class){
           if(cosXml == null){
               new QServer(context);
           }
        }
    }


    public static void deleteCOSObject(Context context, String bucket, String cosPath) throws CosXmlServiceException, CosXmlClientException {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, cosPath);
        QServer.init(context);
        QServer.cosXml.deleteObject(deleteObjectRequest);
    }

    public static void deleteLocalFile(String srcPath){
        if(srcPath != null){
            File file = new File(srcPath);
            if(file.exists()){
                file.delete();
            }
        }
    }

    public static String createFile(Context context, long fileLength) throws IOException {
        String cacheFilePath = context.getExternalCacheDir().getPath() + File.separator
                + System.currentTimeMillis() + ".txt";
        RandomAccessFile accessFile = new RandomAccessFile(cacheFilePath, "rws");
        accessFile.setLength(fileLength);
        accessFile.close();
        return cacheFilePath;
    }

}
