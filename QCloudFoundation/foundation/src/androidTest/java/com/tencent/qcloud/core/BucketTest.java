package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.cos.bucket.DeleteBucketRequest;
import com.tencent.qcloud.core.cos.bucket.DeleteBucketResult;
import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.cos.bucket.PutBucketRequest;
import com.tencent.qcloud.core.cos.bucket.PutBucketResult;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.QCloudHttpRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
@RunWith(AndroidJUnit4.class)
public class BucketTest {
    private QBaseServe qBaseServe;
    String opBucket = "wjbk01";

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getInstance(appContext);

    }

    @Test
    public void testParallelRequest() throws Exception {
        final GetBucketRequest request = new GetBucketRequest();
        request.setBucket(QBaseServe.bucket);

        final Object syncObject = new Object();
        final AtomicInteger count = new AtomicInteger(0);
        final int task = 20;

        for (int i = 0; i < task; i++) {
            QCloudHttpRequest<GetBucketResult> getBucketResultQCloudHttpRequest = qBaseServe.cosXmlService
                    .buildGetBucketRequest(request);
            qBaseServe.cosXmlService.schedule(getBucketResultQCloudHttpRequest, new QCloudResultListener<HttpResult<GetBucketResult>>() {
                @Override
                public void onSuccess(HttpResult<GetBucketResult> result) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {}
                    assertNotNull(request);
                    synchronized (syncObject) {
                        if (count.incrementAndGet() == task) {
                            syncObject.notify();
                        }
                    }
                }

                @Override
                public void onFailure(QCloudClientException clientException, QCloudServiceException serviceException) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {}
                    assertNotNull(clientException);
                    synchronized (syncObject) {
                        if (count.incrementAndGet() == task) {
                            syncObject.notify();
                        }
                    }
                }
            });
        }

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void testGetBucketRequest() throws Exception{
        GetBucketRequest request = new GetBucketRequest();
        request.setBucket(QBaseServe.bucket);
        QCloudHttpRequest<GetBucketResult> getBucketResultQCloudHttpRequest = qBaseServe.cosXmlService
                .buildGetBucketRequest(request);

        HttpResult<GetBucketResult> result =
                qBaseServe.cosXmlService.execute(getBucketResultQCloudHttpRequest);
        assertEquals(true, qBaseServe.isSuccess(result.code()));
    }

    @Test
    public void testGetBucketRequest2() throws Exception {
        GetBucketRequest request = new GetBucketRequest();
        request.setBucket(QBaseServe.bucket);
        request.setPrefix("2");
        request.setDelimiter('7');
        request.setMarker("100");
        QCloudHttpRequest<GetBucketResult> getBucketResultQCloudHttpRequest = qBaseServe.cosXmlService
                .buildGetBucketRequest(request);

        final Object syncObject = new Object();

        qBaseServe.cosXmlService.schedule(getBucketResultQCloudHttpRequest,
                new QCloudResultListener<HttpResult<GetBucketResult>>() {
            @Override
            public void onSuccess(HttpResult<GetBucketResult>result) {
                assertEquals(true, qBaseServe.isSuccess(result.code()));
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailure(QCloudClientException clientException,
                                 QCloudServiceException serviceException) {
                assertNull(clientException);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });


        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void testPutBucketRequest() throws Exception{
        PutBucketRequest request = new PutBucketRequest();
        request.setBucket(opBucket);

        QCloudHttpRequest<PutBucketResult> putBucketResultQCloudHttpRequest =
                qBaseServe.cosXmlService.buildPutBucketRequest(request);
        HttpResult<PutBucketResult> result =
                qBaseServe.cosXmlService.execute(putBucketResultQCloudHttpRequest);

        assertEquals(true, qBaseServe.isSuccess(result.code())
                || result.code() == 409);
    }

    @Test
    public void testDeleteBucketRequest() throws Exception{
        DeleteBucketRequest request = new DeleteBucketRequest();
        request.setBucket(opBucket);

        QCloudHttpRequest<DeleteBucketResult> deleteBucketResultQCloudHttpRequest =
                qBaseServe.cosXmlService.buildDeleteBucketRequest(request);
        HttpResult<DeleteBucketResult> result =
                qBaseServe.cosXmlService.execute(deleteBucketResultQCloudHttpRequest);

        assertEquals(true, qBaseServe.isSuccess(result.code()));
    }


}
