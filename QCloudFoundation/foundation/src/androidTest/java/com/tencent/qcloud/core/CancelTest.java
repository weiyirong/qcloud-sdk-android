package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.http.QCloudHttpRequest;
import com.tencent.qcloud.core.task.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * Created by wjielai on 2017/9/26.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@RunWith(AndroidJUnit4.class)
public class CancelTest {
    private QBaseServe qBaseServe;
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
        request.setBucket(QBaseServe.bucket);

        QCloudHttpRequest<GetBucketResult> getBucketResultQCloudHttpRequest = qBaseServe.cosXmlService
                .buildGetBucketRequest(request);

        Task<HttpResult<GetBucketResult>> task =
                qBaseServe.cosXmlService.schedule(getBucketResultQCloudHttpRequest, null);

        for (int i = 0; i < 3; i++) {
            ((HttpTask) task).cancel();
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    @Test
    public void testCancelAll() throws Exception {
        final GetBucketRequest request = new GetBucketRequest();
        request.setBucket(QBaseServe.bucket);

        final Object syncObject = new Object();

        final AtomicBoolean canceled = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    QCloudHttpRequest<GetBucketResult> getBucketResultQCloudHttpRequest = qBaseServe.cosXmlService
                            .buildGetBucketRequest(request);

                    qBaseServe.cosXmlService.schedule(getBucketResultQCloudHttpRequest, new QCloudResultListener<HttpResult<GetBucketResult>>() {
                        @Override
                        public void onSuccess(HttpResult<GetBucketResult> result) {
                            if (canceled.get()) {
                                assertTrue(false);
                            }
                        }

                        @Override
                        public void onFailure(QCloudClientException clientException, QCloudServiceException serviceException) {
                            if (canceled.get()) {
                                assertTrue(false);
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
