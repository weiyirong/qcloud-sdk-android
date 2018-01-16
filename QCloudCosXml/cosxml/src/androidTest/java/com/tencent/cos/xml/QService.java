package com.tencent.cos.xml;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by bradyxiao on 2017/12/4.
 */

public class QService {
    private static CosXmlService cosXmlClient;

    static final String TAG = "Unit_Test";

    public static String appid = "1253960454";
    public static String region = Region.AP_Guangzhou.getRegion();

    public static CosXmlService getCosXmlClient(Context context){
        synchronized (QService.class){
            if(cosXmlClient == null){
                CosXmlServiceConfig configuration = new CosXmlServiceConfig.Builder()
                        .isHttps(false)
                        .setAppidAndRegion(appid,region)
                        .builder();
                QCloudCredentialProvider qCloudCredentialProvider = new ShortTimeCredentialProvider(
                        "XXXXX",
                        "XXXXXX",
                        60 * 60);
                cosXmlClient = new CosXmlService(context, configuration, qCloudCredentialProvider);
            }
        }
        return cosXmlClient;
    }

    public static String createFile(long length) throws IOException {
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

    public static void delete(CosXmlService cosXmlService, String bucket, String cosPath) throws CosXmlServiceException, CosXmlClientException {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, cosPath);
        DeleteObjectResult deleteObjectResult = cosXmlService.deleteObject(deleteObjectRequest);
        Log.d(TAG, deleteObjectResult.printResult());
    }

    public static void delete(String localPath){
        File file = new File(localPath);
        if(!file.exists()){
            return;
        }else {
            file.delete();
        }

    }
}
