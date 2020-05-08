package com.tencent.cos.xml.upload;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.core.TestConfigs;
import com.tencent.cos.xml.core.TestLocker;
import com.tencent.cos.xml.core.TestUtils;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tencent.cos.xml.core.TestUtils.mergeExceptionMessage;

@RunWith(AndroidJUnit4.class)
public class UploadManagerTest {

    /**
     * 上传 1M 小文件测试
     */
    @Test public void testSimpleUpload() {

        TransferManager transferManager = TestUtils.newDefaultTerminalTransferManager();
        COSXMLUploadTask uploadTask = transferManager.upload(TestConfigs.TERMINAL_PERSIST_BUCKET, TestConfigs.COS_TXT_1M_PATH,
                TestConfigs.LOCAL_TXT_1M_PATH, null);
        final TestLocker testLocker = new TestLocker();
        uploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Assert.assertTrue(true);
                testLocker.release();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                Assert.fail(mergeExceptionMessage(clientException, serviceException));
                testLocker.release();
            }
        });

        testLocker.lock();
    }

    /**
     * 上传 100M 大文件测试
     */
    @Test public void testMultiUpload() {

        TransferManager transferManager = TestUtils.newDefaultTerminalTransferManager();
        COSXMLUploadTask uploadTask = transferManager.upload(TestConfigs.TERMINAL_PERSIST_BUCKET, TestConfigs.COS_TXT_100M_PATH,
                TestConfigs.LOCAL_TXT_100M_PATH, null);
        final TestLocker testLocker = new TestLocker();

        uploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Assert.assertTrue(true);
                testLocker.release();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                Assert.fail(mergeExceptionMessage(clientException, serviceException));
                testLocker.release();
            }
        });

        testLocker.lock();
    }

    // TODO: 2020-04-29 上传超大文件 10G


    /**
     * 批量小文件上传
     */
    @Test public void testBatchSimpleUpload() {

        TransferManager transferManager = TestUtils.newDefaultTerminalTransferManager();
        int count = 10;
        long size = 1024 * 1024;
        final TestLocker testLocker = new TestLocker(count);

        for (int i = 0; i < count; i ++) {

            String cosPath = "/ut_files/1M_" + i + ".txt";
            String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + cosPath;
            try {
                TestUtils.createFile(localPath, size);
            } catch (IOException e) {
                e.printStackTrace();
            }

            COSXMLUploadTask uploadTask = transferManager.upload(TestConfigs.TERMINAL_PERSIST_BUCKET, cosPath,
                    localPath, null);
            uploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    testLocker.release();
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                    Assert.fail(mergeExceptionMessage(clientException, serviceException));
                    testLocker.release();
                }
            });
        }

        testLocker.lock();
        Assert.assertTrue(true);
    }

    /**
     * 批量大文件上传
     */
    @Test public void testBatchMultiUpload() {

        TransferManager transferManager = TestUtils.newDefaultTerminalTransferManager();
        int count = 10;
        long size = 100 * 1024 * 1024;
        final TestLocker testLocker = new TestLocker(count);

        for (int i = 0; i < count; i ++) {

            String cosPath = "/ut_files/100M_" + i + ".txt";
            String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + cosPath;
            try {
                TestUtils.createFile(localPath, size);
            } catch (IOException e) {
                e.printStackTrace();
            }

            COSXMLUploadTask uploadTask = transferManager.upload(TestConfigs.TERMINAL_PERSIST_BUCKET, cosPath,
                    localPath, null);
            uploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    testLocker.release();
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                    Assert.fail(mergeExceptionMessage(clientException, serviceException));
                    testLocker.release();
                }
            });
        }

        testLocker.lock();
        Assert.assertTrue(true);
    }


    /**
     * 上传进度 100% 后点击暂停，并等待 5s 后恢复上传
     */
    @Test public void testPauseAndResume() {

        TransferManager transferManager = TestUtils.newDefaultTerminalTransferManager();
        final COSXMLUploadTask uploadTask = transferManager.upload(TestConfigs.TERMINAL_PERSIST_BUCKET, TestConfigs.COS_TXT_100M_PATH,
                TestConfigs.LOCAL_TXT_100M_PATH, null);
        final TestLocker testLocker = new TestLocker();
        final TestLocker waitPauseLocker = new TestLocker();

        uploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Assert.assertTrue(true);
                testLocker.release();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                Assert.fail(mergeExceptionMessage(clientException, serviceException));
                testLocker.release();
            }
        });

        uploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.i(TestConfigs.UNIT_TEST_TAG, "state " + state);

            }
        });


        final AtomicBoolean pauseSuccess = new AtomicBoolean(false);
        uploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {

                Log.i(TestConfigs.UNIT_TEST_TAG, "progress  = " + 1.0 * complete / target);

                if (complete == target && !pauseSuccess.get()) {
                    if (uploadTask.pauseSafely()) {
                        Log.i(TestConfigs.UNIT_TEST_TAG, "pause success!!");
                        pauseSuccess.set(true);
                    } else {
                        Log.i(TestConfigs.UNIT_TEST_TAG, "pause failed!!");
                    }
                    waitPauseLocker.release();
                }
            }
        });

        waitPauseLocker.lock(); // 等待暂停

        if (pauseSuccess.get()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(TestConfigs.UNIT_TEST_TAG, "start resume");
                    uploadTask.resume();
                }
            }, 5000);
        }

        testLocker.lock();
    }


}
