package com.tencent.cos.xml;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.MetaDataDirective;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteMultiObjectResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.OptionObjectRequest;
import com.tencent.cos.xml.model.object.OptionObjectResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.tencent.cos.xml.model.tag.pic.PicOperationRule;
import com.tencent.cos.xml.model.tag.pic.PicOriginalInfo;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.tencent.qcloud.core.http.HttpTaskMetrics;
import com.tencent.qcloud.core.http.QCloudHttpRetryHandler;
import com.tencent.qcloud.core.logger.QCloudLogger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.cos.xml.QServer.TAG;


/**
 * Created by bradyxiao on 2018/3/13.
 */

@RunWith(AndroidJUnit4.class)
public class QuicTest {

    @Test public void test() {

        List<PicOriginalInfo> picOriginalInfos = new LinkedList<>();
        PicOriginalInfo picOriginalInfo = null;
        for (int i = 0; i < 3; i++) {
            picOriginalInfo = new PicOriginalInfo("key" + i, "location");
            picOriginalInfos.add(picOriginalInfo);
        }

        for (PicOriginalInfo info : picOriginalInfos) {
            Log.i("tag", info.key);
        }

    }

    @Test
    public void testUploadObject() throws CosXmlServiceException, CosXmlClientException {

        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(true)
                .enableQuic(true)
                .setRegion("yfb")
                .setDebuggable(true)
                .builder();

        Context context = InstrumentationRegistry.getContext();
        String secretId = BuildConfig.COS_CI_SECRET_ID;
        String secretKey = BuildConfig.COS_CI_SECRET_KEY;
        String bucket = "alanyfb-1251668577";
        String cosPath = "test/100kb";
        String srcPath = null;

        int _1M = 1024 * 1024;
        int _20M = 20 * 1024 * 1024;
        int _200M = 200 * 1024 * 1024;

        long fileLength = _200M;

        try {
            srcPath = QServer.createFile(context, fileLength);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CosXmlService cosXml = new CosXmlService(InstrumentationRegistry.getContext(), cosXmlServiceConfig,
                new ShortTimeCredentialProvider(secretId, secretKey,600) );

        cosXml.addCustomerDNS("alanyfb-1251668577.cos.yfb.myqcloud.com", new String[] {"129.204.98.7"});

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        putObjectRequest.setRequestHeaders("Host", "alanyfb-1251668577.cos.yfb.myqcloud.com", false);
        TransferManager transferManager = new TransferManager(cosXml, new TransferConfig.Builder().build());
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(putObjectRequest, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, complete + "/" + target);
            }
        });
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.i(TAG, result.printResult());
                countDownLatch.countDown();
                Assert.assertTrue(true);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                // todo Upload failed because of CosXmlClientException or CosXmlServiceException...

                if (serviceException != null) {
                    Log.e(TAG, "service error " + serviceException.toString());
                }
                if (clientException != null) {
                    Log.e(TAG, "client error " + clientException.toString());
                }
                countDownLatch.countDown();
                Assert.assertFalse(true);
            }
        });
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG, state.toString());
            }
        });

        try {
            Thread.sleep(5000);
            cosxmlUploadTask.pause();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cosxmlUploadTask.resume();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetObject() throws CosXmlServiceException, CosXmlClientException {

        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(true)
                .enableQuic(true)
                .setRegion("yfb")
                .setDebuggable(true)
                .builder();

        Context context = InstrumentationRegistry.getContext();
        String secretId = BuildConfig.COS_CI_SECRET_ID;
        String secretKey = BuildConfig.COS_CI_SECRET_KEY;
        String bucket = "alanyfb-1251668577";
        String cosPath = "test/100kb";
        String srcPath = Environment.getExternalStorageDirectory().getAbsolutePath(); // File.createTempFile("test", "txt").getAbsolutePath();

        CosXmlService cosXml = new CosXmlService(InstrumentationRegistry.getContext(), cosXmlServiceConfig,
                new ShortTimeCredentialProvider(secretId, secretKey,600) );

        cosXml.addCustomerDNS("alanyfb-1251668577.cos.yfb.myqcloud.com", new String[] {"211.159.131.17"});

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, cosPath, srcPath);
        getObjectRequest.setRequestHeaders("Host", "alanyfb-1251668577.cos.yfb.myqcloud.com", false);
        getObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, complete + "/" + target);
            }
        });
        Log.d(TAG, "start test");
        try {
            GetObjectResult getObjectResult = cosXml.getObject(getObjectRequest);
            Log.i(TAG, getObjectResult.printResult());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    }

}
