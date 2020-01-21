package com.tencent.cos.xml;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.tencent.qcloud.core.http.HttpTaskMetrics;
import com.tencent.qcloud.core.logger.QCloudLogger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.tencent.cos.xml.QServer.appid;
import static com.tencent.cos.xml.QServer.region;
import static com.tencent.cos.xml.QServer.secretId;
import static com.tencent.cos.xml.QServer.secretKey;

/**
 * <p>
 * </p>
 * Created by wjielai on 2020-01-15.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class NetworkPerfTest {

    private static final String TAG = "NetworkPerfTest";

    private Context mContext;
    private String bucket = "android-ut-persist-bucket";

    private final int everyTest = 3;
    private final boolean isSerial = true;

    private static class PerfStat {
        public List<Result> stats = new ArrayList<>();
    }

    private static class Result {
        public String fileId;
        public Exception exception;
        public double duration; // seconds
        public double avgSpeed; // B/s

        public Result(String fileId, Exception exception, double duration, double avgSpeed) {
            this.exception = exception;
            this.fileId = fileId;
            this.duration = duration;
            this.avgSpeed = avgSpeed;
        }
    }

    private static class MergedMetrics {
        double dnsLookupTookTime;

        double connectTookTime;

        double secureConnectTookTime;

        double writeRequestHeaderTookTime;

        double writeRequestBodyTookTime;

        double readResponseHeaderTookTime;

        double readResponseBodyTookTime;

        double fullTaskTookTime = 0.0001;

        double calculateMD5STookTime;

        double signRequestTookTime;

        public void add(HttpTaskMetrics taskMetrics) {
            dnsLookupTookTime += taskMetrics.dnsLookupTookTime();
            connectTookTime += taskMetrics.connectTookTime();
            secureConnectTookTime += taskMetrics.secureConnectTookTime();
            writeRequestBodyTookTime += taskMetrics.writeRequestBodyTookTime();
            writeRequestHeaderTookTime += taskMetrics.writeRequestHeaderTookTime();
            readResponseBodyTookTime += taskMetrics.readResponseBodyTookTime();
            readResponseHeaderTookTime += taskMetrics.readResponseHeaderTookTime();
            fullTaskTookTime += taskMetrics.fullTaskTookTime();
            calculateMD5STookTime += taskMetrics.calculateMD5STookTime();
            signRequestTookTime += taskMetrics.signRequestTookTime();
        }

        @Override
        public String toString() {
            return new StringBuilder().append("Http Metrics: \n")
                    .append("fullTaskTookTime : ").append(fullTaskTookTime).append("\n")
                    .append("calculateMD5STookTime : ").append(calculateMD5STookTime).append("\n")
                    .append("signRequestTookTime : ").append(signRequestTookTime).append("\n")
                    .append("dnsLookupTookTime : ").append(dnsLookupTookTime).append("\n")
                    .append("connectTookTime : ").append(connectTookTime).append("\n")
                    .append("secureConnectTookTime : ").append(secureConnectTookTime).append("\n")
                    .append("writeRequestHeaderTookTime : ").append(writeRequestHeaderTookTime).append("\n")
                    .append("writeRequestBodyTookTime : ").append(writeRequestBodyTookTime).append("\n")
                    .append("readResponseHeaderTookTime : ").append(readResponseHeaderTookTime).append("\n")
                    .append("readResponseBodyTookTime : ").append(readResponseBodyTookTime)
                    .toString();
        }
    }

    private Long[] sizeSelections = new Long[] {
            1024L * 1024,
            1024L * 1024 * 5,
            1024L * 1024 * 50,
//            1024L * 1024 * 100,
            1024L * 1024 * 200
    };
    private long sliceSize = 1 * 1024 * 1024;

    @Before
    public void setup() throws IOException {
        mContext = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
    }

    private void uploadFile(final String cosKey, final File file,
                            final CountDownLatch signal, final Object lock, final PerfStat perfStat) {
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(true)
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();
        CosXmlService mService = new CosXmlService(mContext, cosXmlServiceConfig,
                new ShortTimeCredentialProvider(secretId,secretKey,600) );
        TransferManager transferManager = new TransferManager(mService,
                new TransferConfig.Builder()
                        .setSliceSizeForUpload(sliceSize)
                        .build());

        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosKey, file.getPath(), null);
        final double[] taskStat = new double[2];
        taskStat[0] = 0; // transfer bytes
        final MergedMetrics metrics = new MergedMetrics();
        final long startTime = System.currentTimeMillis();

        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                QCloudLogger.i(TAG, cosKey + " upload progress : " + ((double)complete  / (double)target));
                taskStat[0] = complete;
            }
        });
        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                QCloudLogger.i(TAG, cosKey + ": upload success, " + metrics);
                double tookTime = (System.currentTimeMillis() - startTime) / 1000.0;
                perfStat.stats.add(new Result(cosKey, null, tookTime, taskStat[0]/ tookTime));
                signal.countDown();
                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Exception e = exception != null ? exception : serviceException;
                QCloudLogger.i(TAG, cosKey + ": upload failed:  " + e.getCause());
                double tookTime = (System.currentTimeMillis() - startTime) / 1000.0;
                perfStat.stats.add(new Result(cosKey, e, tookTime, taskStat[0]/ tookTime));
                signal.countDown();
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                QCloudLogger.i(TAG, cosKey + ": upload state change to  : " + state);
            }
        });

        cosxmlUploadTask.setOnGetHttpTaskMetrics(new COSXMLTask.OnGetHttpTaskMetrics() {
            @Override
            public void onGetHttpMetrics(String requestName, HttpTaskMetrics httpTaskMetrics) {
                metrics.add(httpTaskMetrics);
            }
        });
    }

    private static File createTempFile(Context context, long fileLength) throws IOException {
        File cacheFile = new File(context.getExternalCacheDir(), "temp_" + fileLength + ".txt");
        if (!cacheFile.exists()) {
            RandomAccessFile accessFile = new RandomAccessFile(cacheFile, "rws");
            accessFile.setLength(fileLength);
            accessFile.write(new Random().nextInt(200));
            accessFile.seek(fileLength/2);
            accessFile.write(new Random().nextInt(200));
            accessFile.close();
        }
        return cacheFile;
    }

    @Test
    public void testUpload() throws IOException, InterruptedException {
        final CountDownLatch signal = new CountDownLatch(everyTest * sizeSelections.length);
        final Object lock = new Object();
        final PerfStat perfStat = new PerfStat();

        for (long size : sizeSelections) {
            for (int i = 0; i < everyTest; i++) {
                File file = createTempFile(mContext, size);
                String cosKey = file.getName();
                uploadFile(cosKey, file, signal, lock, perfStat);
                if (isSerial) {
                    synchronized (lock) {
                        lock.wait();
                    }
                }
            }
        }

        signal.await();

        double sumDuration = 0, sumSpeed = 0;
        String lastFileId = null;
        for (Result result : perfStat.stats) {
            QCloudLogger.i(TAG, String.format("STAT === %s: exception[%s] duration[%f s] avgSpeed[%f B/S]",
                    result.fileId, result.exception, result.duration, result.avgSpeed));
            if (lastFileId != null && !result.fileId.equals(lastFileId)) {
                QCloudLogger.i(TAG, String.format("STAT AVG === %s: duration[%f s] avgSpeed[%f B/S]",
                        lastFileId, sumDuration / everyTest, sumSpeed / everyTest));
                sumDuration = 0;
                sumSpeed = 0;
            }
            lastFileId = result.fileId;
            sumDuration += result.duration;
            sumSpeed += result.avgSpeed;
        }
        QCloudLogger.i(TAG, String.format("STAT AVG === %s: duration[%f s] avgSpeed[%f B/S]",
                lastFileId, sumDuration / everyTest, sumSpeed / everyTest));

        // write for log to write
        TimeUnit.SECONDS.sleep(5);
    }
}
