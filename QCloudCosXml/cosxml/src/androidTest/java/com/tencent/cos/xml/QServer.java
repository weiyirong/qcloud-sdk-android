package com.tencent.cos.xml;

import android.content.Context;
import android.util.Log;

import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.qcloud.core.auth.BasicQCloudCredentials;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.tencent.qcloud.core.http.QCloudHttpRetryHandler;
import com.tencent.qcloud.core.task.RetryStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradyxiao on 2018/3/12.
 */

public class QServer {

    public final static String TAG = "UnitTest";

    /** 腾讯云 cos 服务的 appid */
    public final static String ownUin = BuildConfig.COSUin;
    public final static String appid = BuildConfig.COSAppId;
    public static final String secretId = BuildConfig.COSSecretId;
    public static final String secretKey = BuildConfig.COSSecretKey;
    /** persistBucket 所处在的地域 */
    public final static String region = Region.AP_Guangzhou.getRegion() ;
    public static final String persistBucket = "android-ut-persist-bucket";
    public static final String tempBucket = "android-ut-temp-bucket";
    public static CosXmlService cosXml;

    public static boolean cspTest = false;
    private QServer(Context context){
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(false)
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .setRetryHandler(new QCloudHttpRetryHandler(){@Override public boolean shouldRetry(Request request, Response response, Exception exception){
                    Log.e(TAG, request.url().host());
                    try {
                        cosXml.addCustomerDNS("service.cos.myqcloud.com", new String[]{"123.151.157.147"});
                    } catch (CosXmlClientException e) {
                        e.printStackTrace();
                    }
                    return true;
                } })
                .builder();
        cosXml = new CosXmlService(context, cosXmlServiceConfig,
                new ShortTimeCredentialProvider(secretId,secretKey,600) );
        try {
            cosXml.addCustomerDNS("service.cos.myqcloud.com", new String[]{"123.159.157.147"});
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        }
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
