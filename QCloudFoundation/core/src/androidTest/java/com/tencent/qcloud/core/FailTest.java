package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.cos.CosXmlServiceConfig;
import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.cos.object.GetObjectRequest;
import com.tencent.qcloud.core.cos.object.GetObjectResult;
import com.tencent.qcloud.core.network.QCloudHttpRequest;
import com.tencent.qcloud.core.network.QCloudResultListener;
import com.tencent.qcloud.core.network.QCloudServiceConfig;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.exception.QCloudServiceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.net.ProtocolException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by wjielai on 2017/10/11.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@RunWith(AndroidJUnit4.class)
public class FailTest {

    private QBaseServe qBaseServe;
    private String backupHost;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getInstance(appContext);

        CosXmlServiceConfig cosXmlServiceConfig = CosXmlServiceConfig.getInstance();
        backupHost = cosXmlServiceConfig.getHttpHost();
    }

    @After
    public void cleanUp() throws Exception {
        useNormalHost();
    }

    private void useNullHost() throws Exception {
        CosXmlServiceConfig cosXmlServiceConfig = CosXmlServiceConfig.getInstance();
        Field host = QCloudServiceConfig.class.getDeclaredField("httpHost");
        host.setAccessible(true);
        host.set(cosXmlServiceConfig, "NullQCNotExistHost");
    }

    private void useNormalHost() throws Exception {
        CosXmlServiceConfig cosXmlServiceConfig = CosXmlServiceConfig.getInstance();
        Field host = QCloudServiceConfig.class.getDeclaredField("httpHost");
        host.setAccessible(true);
        host.set(cosXmlServiceConfig, backupHost);
    }

    @Test
    public void testGetBucketFailRequest() throws Exception {
        useNullHost();

        GetBucketRequest request = new GetBucketRequest();
        request.setBucket("notExistBucket");
        request.setSign(600,null,null);
        try {
            GetBucketResult result = qBaseServe.cosXmlService.execute(request);
            assertEquals(false, qBaseServe.isSuccess(result.getHttpCode()));
        } catch (QCloudClientException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetObjectFailRequest() throws Exception{
        useNormalHost();

        GetObjectRequest request = new GetObjectRequest("/abc");
        request.setBucket(qBaseServe.bucket);
        request.setCosPath("/sample.txt");
        request.setSign(600,null,null);
        request.setRange(1);
        request.setRspContentType("text/json");
        request.setRspContentLanguage("CN");
        assertEquals("CN", request.getRspContentLanguage());

        try {
            GetObjectResult result = qBaseServe.cosXmlService.execute(request);
            assertEquals(false, qBaseServe.isSuccess(result.getHttpCode()));
        } catch (QCloudClientException e) {
            assertNotNull(e);
        }

        final Object syncObject = new Object();

        qBaseServe.cosXmlService.enqueue(request, new QCloudResultListener<QCloudHttpRequest<GetObjectResult>, GetObjectResult>() {
            @Override
            public void onSuccess(QCloudHttpRequest<GetObjectResult> request, GetObjectResult result) {
                assertFalse(qBaseServe.isSuccess(result.getHttpCode()));
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailed(QCloudHttpRequest<GetObjectResult> request, QCloudClientException clientException,
                                 QCloudServiceException serviceException) {
                assertNotNull(clientException);
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
    public void testGetBucketFailAsyncRequest() throws Exception{
        useNullHost();

        GetBucketRequest request = new GetBucketRequest();
        request.setBucket("notExistBucket");
        request.setSign(600,null,null);

        final Object syncObject = new Object();

        qBaseServe.cosXmlService.getBucketAsync(request, new QCloudResultListener<GetBucketRequest, GetBucketResult>() {
            @Override
            public void onSuccess(GetBucketRequest request, GetBucketResult result) {
                assertFalse(qBaseServe.isSuccess(result.getHttpCode()));
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailed(GetBucketRequest request, QCloudClientException clientException, QCloudServiceException serviceException) {
                assertNotNull(clientException);
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
    public void testGetBucketFailAsyncRequestAndNoListener() throws Exception{
        useNullHost();

        GetBucketRequest request = new GetBucketRequest();
        request.setBucket("notExistBucket");
        request.setSign(600,null,null);

        qBaseServe.cosXmlService.getBucketAsync(request, null);
    }

    @Test
    public void testHttpNoContentResponse() throws Exception{
        useNormalHost();

        GetBucketRequest request = new GetBucketRequest();
        request.setBucket("notExistBucket");
        request.setSign(600,null,null);

        qBaseServe.mockResponseInterceptor.setMockException(new ProtocolException("HTTP 204 had non-zero Content-Length: "));

        GetBucketResult getBucketResult = qBaseServe.cosXmlService.execute(request);
        assertEquals(204, getBucketResult.getHttpCode());

        qBaseServe.mockResponseInterceptor.reset();
    }

    @Test
    public void testHttpProtocolError() throws Exception{
        useNormalHost();

        GetBucketRequest request = new GetBucketRequest();
        request.setBucket("notExistBucket");
        request.setSign(600,null,null);

        try {
            qBaseServe.mockResponseInterceptor.setMockException(new ProtocolException("New Protocol Exception"));
            qBaseServe.cosXmlService.execute(request);
        } catch (QCloudClientException e) {
            assertNotNull(e);
        }
        qBaseServe.mockResponseInterceptor.reset();
    }

    @Test
    public void testSignError() throws Exception{
        useNormalHost();

        GetBucketRequest request = new GetBucketRequest();
        request.setBucket("notExistBucket");
        request.setSign(600,null,null);
        request.setSignerType("wrongSignType");

        try {
            GetBucketResult result = qBaseServe.cosXmlService.execute(request);
            assertNull(result);
        } catch (QCloudClientException e) {
            assertNotNull(e);
        }
    }
}
