package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.network.QCloudCall;
import com.tencent.qcloud.core.network.QCloudResultListener;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.exception.QCloudServiceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by wjielai on 2017/9/26.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@RunWith(AndroidJUnit4.class)
public class CancelTest {
    private QBaseServe qBaseServe;
    String bucket = "androidsample";
    String opBucket = "wjbk01";

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getInstance(appContext);

    }

    @Test
    public void testMultiCancelOp() throws Exception {
        final GetBucketRequest request = new GetBucketRequest();
        request.setBucket(bucket);
        request.setSign(600, null, null);

        QCloudCall cloudCall = qBaseServe.cosXmlService.getBucketAsync(request, new QCloudResultListener<GetBucketRequest, GetBucketResult>() {
            @Override
            public void onSuccess(GetBucketRequest request, GetBucketResult result) {
            }

            @Override
            public void onFailed(GetBucketRequest request, QCloudClientException clientException, QCloudServiceException serviceException) {
            }
        });

        for (int i = 0; i < 3; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            cloudCall.cancel();
        }
    }

    @Test
    public void testCancelAll() throws Exception {
        final GetBucketRequest request = new GetBucketRequest();
        request.setBucket(bucket);
        request.setSign(600, null, null);
        request.setPrefix("2");
        request.setDelimiter('7');
        request.setMarker("100");

        final Object syncObject = new Object();

        final AtomicBoolean canceled = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    qBaseServe.cosXmlService.getBucketAsync(request, new QCloudResultListener<GetBucketRequest, GetBucketResult>() {
                        @Override
                        public void onSuccess(GetBucketRequest request, GetBucketResult result) {
                            if (canceled.get()) {
                                assertNull(request);
                            }
                        }

                        @Override
                        public void onFailed(GetBucketRequest request, QCloudClientException clientException, QCloudServiceException serviceException) {
                            if (canceled.get()) {
                                assertNull(request);
                            }
                        }
                    });
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                qBaseServe.cosXmlService.cancelAll();
                canceled.set(true);

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        }).start();

        synchronized (syncObject) {
            syncObject.wait();
        }
    }
}
