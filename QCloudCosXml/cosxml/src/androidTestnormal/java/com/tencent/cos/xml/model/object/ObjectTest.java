package com.tencent.cos.xml.model.object;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlBooleanListener;
import com.tencent.cos.xml.model.tag.COSMetaData;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudServiceException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@RunWith(AndroidJUnit4.class)
public class ObjectTest {

    @BeforeClass public static void init() {

        Context appContext = InstrumentationRegistry.getContext();
        QServer.init(appContext);
    }

    @Test public void doesObjectExistTest() throws Exception {

        String bucketName = QServer.guangZhouBucket;

        try {
            Assert.assertFalse(QServer.cosXml.doesObjectExist(bucketName, "/10Mfile.txt"));
            Assert.assertFalse(QServer.cosXml.doesObjectExist(bucketName, "/10MfileNotExist.txt"));
        } catch (CosXmlClientException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
            Assert.assertFalse(false);
        }

    }

     public void doesObjectExistAsyncTest() {

        String bucketName = QServer.guangZhouBucket;

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        QServer.cosXml.doesObjectExistAsync(bucketName, "10Mfile.txt", new CosXmlBooleanListener() {
            @Override
            public void onSuccess(boolean bool) {

                Assert.assertTrue(bool);
                countDownLatch.countDown();
            }

            @Override
            public void onFail(CosXmlClientException exception, CosXmlServiceException serviceException) {

                Assert.assertTrue(false);
                countDownLatch.countDown();
            }
        });

        QServer.cosXml.doesObjectExistAsync(bucketName, "10MfileNotExist.txt", new CosXmlBooleanListener() {
            @Override
            public void onSuccess(boolean bool) {

                Assert.assertTrue(!bool);
                countDownLatch.countDown();
            }

            @Override
            public void onFail(CosXmlClientException exception, CosXmlServiceException serviceException) {

                Assert.assertTrue(false);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    @Test public void uploadStringObjectTest() throws Exception {

        String bucketName = QServer.guangZhouBucket;
        String objectName = "/putobject.txt";

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new StringBuilder("this is a test"));

        PutObjectResult putObjectResult = null;
        try {
            putObjectResult = QServer.cosXml.putObject(putObjectRequest);
            Assert.assertTrue(putObjectResult.httpCode == 200);

        } catch (QCloudClientException clientException) {
            clientException.printStackTrace();
            Assert.assertTrue(false);
        } catch (QCloudServiceException serviceException) {
            serviceException.printStackTrace();
            Assert.assertTrue(false);
        }

    }

    // 后台暂时未全面放开支持
//    @Test public void uploadUrlObjectTest() throws Exception {
//
//        String bucketName = "android-demo-ap-shanghai";
//        String objectName = "/putUrObject.txt";
//        URL url = new URL("http://tac-android-libs-1253960454.cosgz.myqcloud.com/tac_android_configuration.jpg");
//
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, url);
//
//        PutObjectResult putObjectResult = null;
//        try {
//            putObjectResult = QServer.cosXml.putObject(putObjectRequest);
//            Assert.assertTrue(putObjectResult.httpCode == 200);
//
//        } catch (QCloudClientException clientException) {
//            clientException.printStackTrace();
//            Assert.assertTrue(false);
//        } catch (QCloudServiceException serviceException) {
//            serviceException.printStackTrace();
//            Assert.assertTrue(false);
//        }
//
//    }

    @Test public void downloadObjectTest() {

        String bucketName = QServer.guangZhouBucket;
        String objectName = "/putobject.txt";

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new StringBuilder("this is a test"));

        PutObjectResult putObjectResult = null;
        try {
            putObjectResult = QServer.cosXml.putObject(putObjectRequest);
            Assert.assertTrue(putObjectResult.httpCode == 200);

        } catch (QCloudClientException clientException) {
            clientException.printStackTrace();
            Assert.assertTrue(false);
        } catch (QCloudServiceException serviceException) {
            serviceException.printStackTrace();
            Assert.assertTrue(false);
        }

        try {
            byte[] data = QServer.cosXml.getObject(bucketName, objectName);
            Assert.assertTrue("this is a test".equals(new String(data)));

        } catch (QCloudClientException clientException) {
            clientException.printStackTrace();
            Assert.assertTrue(false);
        } catch (QCloudServiceException serviceException) {
            serviceException.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test public void deleteObjectTest() throws Exception {

        String bucketName = QServer.guangZhouBucket;
        String objectName = "/putobject.txt";

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new StringBuilder("this is a test"));

        PutObjectResult putObjectResult = null;
        try {
            putObjectResult = QServer.cosXml.putObject(putObjectRequest);
            Assert.assertTrue(putObjectResult.httpCode == 200);
            Assert.assertTrue(QServer.cosXml.deleteObject(bucketName, objectName));

        } catch (QCloudClientException clientException) {
            clientException.printStackTrace();
            Assert.assertTrue(false);
        } catch (QCloudServiceException serviceException) {
            serviceException.printStackTrace();
            Assert.assertTrue(false);
        }

    }

    @Test public void deleteObjectAsyncTest() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        String bucketName = QServer.guangZhouBucket;
        String objectName = "/putobject.txt";

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new StringBuilder("this is a test"));

        try {
            QServer.cosXml.putObject(putObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }

        QServer.cosXml.deleteObjectAsync(bucketName, objectName, new CosXmlBooleanListener() {
            @Override
            public void onSuccess(boolean bool) {
                Assert.assertTrue(bool);
                countDownLatch.countDown();
            }

            @Override
            public void onFail(CosXmlClientException exception, CosXmlServiceException serviceException) {

                Assert.assertTrue(false);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test public void updateObjectMetaTest() {

        String bucketName = QServer.guangZhouBucket;
        String objectName = "/do_not_remove.png";


        COSMetaData metaData = new COSMetaData();
        long time = System.currentTimeMillis();
        metaData.put("time", String.valueOf(time));

        HeadObjectRequest headObjectRequest = new HeadObjectRequest(bucketName, objectName);

        try {
            QServer.cosXml.updateObjectMeta(bucketName, objectName, metaData);
            HeadObjectResult headObjectResult = QServer.cosXml.headObject(headObjectRequest);
            Assert.assertTrue(String.valueOf(time).equals(headObjectResult.headers.get("x-cos-meta-time").get(0)));
        } catch (CosXmlClientException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }

    }



    @Test public void updateObjectMetaAsyncTest() {

        String bucketName = QServer.guangZhouBucket;
        String objectName = "/do_not_remove.png";


        COSMetaData metaData = new COSMetaData();
        long time = System.currentTimeMillis();
        metaData.put("time", String.valueOf(time));

        HeadObjectRequest headObjectRequest = new HeadObjectRequest(bucketName, objectName);

        QServer.cosXml.updateObjectMetaAsync(bucketName, objectName, metaData, new CosXmlBooleanListener() {
            @Override
            public void onSuccess(boolean bool) {

            }

            @Override
            public void onFail(CosXmlClientException exception, CosXmlServiceException serviceException) {

            }
        });

        try {
            QServer.cosXml.updateObjectMeta(bucketName, objectName, metaData);
            HeadObjectResult headObjectResult = QServer.cosXml.headObject(headObjectRequest);
            Assert.assertTrue(String.valueOf(time).equals(headObjectResult.headers.get("x-cos-meta-time").get(0)));
        } catch (CosXmlClientException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }

    }


    @Test public void copyObjectTest() {

        String bucketName = QServer.guangZhouBucket;
        String srcObjectName = "/do_not_remove.png";
        String dstObjectName = "/do_not_remove.png";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                QServer.appid, bucketName, QServer.region, srcObjectName);

        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, dstObjectName, copySourceStruct);
            CopyObjectResult copyObjectResult = QServer.cosXml.copyObject(copyObjectRequest);
            Log.i("tag", "result is " + copyObjectResult.toString());
        } catch (CosXmlClientException e) {
            e.printStackTrace();

        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }

    }


}
