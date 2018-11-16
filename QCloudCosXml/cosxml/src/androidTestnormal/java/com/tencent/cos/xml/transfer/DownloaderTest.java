package com.tencent.cos.xml.transfer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/3/20.
 */
@RunWith(AndroidJUnit4.class)
public class DownloaderTest {

    @Test
    public void download() throws Exception{
//        Context appContext = InstrumentationRegistry.getContext();
//        QServer.init(appContext);
//        String bucket = QServer.bucketForObjectAPITest;
//        String cosPath = "guides/Messaging/show_custom_notification.png";
//        String localPath = appContext.getExternalCacheDir().getPath();
//        String localFileName = "downloader.txt";
//        final Downloader downloader = new Downloader(appContext, QServer.cosXml);
//        downloader.setProgress(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d("UnitTest", complete + "/" + target);
//                if(target / complete < 2){
////                    downloader.cancel();
//                }
//            }
//        });
//        downloader.download(bucket, cosPath, localPath, localFileName);
//        QServer.deleteLocalFile(localPath + File.separator + localFileName);
    }

    volatile boolean isOver = false;
    @Test
    public void download2() throws Exception{
//        Context appContext = InstrumentationRegistry.getContext();
//        QServer.init(appContext);
//        String bucket = QServer.bucketForObjectAPITest;
//        String cosPath = "guides/Messaging/show_custom_notification.png";
//        String localPath = appContext.getExternalCacheDir().getPath();
//        String localFileName = "downloader.txt";
//        final Downloader downloader = new Downloader(appContext, QServer.cosXml);
//        downloader.setProgress(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d("UnitTest", complete + "/" + target);
////                if(target / complete < 2){
////                    downloader.cancel();
////                }
//            }
//        });
//        downloader.download(bucket, cosPath, localPath, localFileName, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d("UnitTest", result.printResult());
//                isOver = true;
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d("UnitTest", exception != null ? exception.getMessage() : serviceException.getMessage());
//                isOver = true;
//            }
//        });
//
//        while (isOver){
//            Thread.sleep(500);
//        }
//        QServer.deleteLocalFile(localPath + File.separator + localFileName);
    }

}