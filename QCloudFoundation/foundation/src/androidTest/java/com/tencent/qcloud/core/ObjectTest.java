package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.cos.object.DeleteObjectRequest;
import com.tencent.qcloud.core.cos.object.DeleteObjectResult;
import com.tencent.qcloud.core.cos.object.GetObjectRequest;
import com.tencent.qcloud.core.cos.object.PutObjectRequest;
import com.tencent.qcloud.core.cos.object.PutObjectResult;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.QCloudHttpRequest;
import com.tencent.qcloud.core.task.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
@RunWith(AndroidJUnit4.class)
public class ObjectTest {
    private QBaseServe qBaseServe;
    private String localDir;

    private String opCosPath = "/slice_upload.txt";

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getInstance(appContext);
        localDir = appContext.getExternalCacheDir().getPath();

    }

    @Test
    public void testGetObjectRequest() throws Exception{
        GetObjectRequest request = new GetObjectRequest(localDir);
        request.setBucket(QBaseServe.bucket);
        request.setCosPath("/simple.txt");
        request.setRspContentType("text/json");
        request.setRspContentLanguage("CN");

        QCloudHttpRequest<Void> httpRequest = qBaseServe.cosXmlService.buildGetObjectRequest(request);
        HttpResult<Void> result = qBaseServe.cosXmlService.execute(httpRequest);
        assertEquals(true, qBaseServe.isSuccess(result.code()));
    }

    @Test
    public void testAsyncGetObjectRequest() throws Exception{
        GetObjectRequest request = new GetObjectRequest(localDir);
        request.setBucket(QBaseServe.bucket);
        request.setCosPath("/simple.txt");
        request.setRspContentType("text/json");
        request.setRspContentLanguage("CN");

        final Object syncObject = new Object();

        QCloudHttpRequest<Void> httpRequest = qBaseServe.cosXmlService.buildGetObjectRequest(request);
        Task<HttpResult<Void>> task = qBaseServe.cosXmlService.schedule(httpRequest, null);
        task.addProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                assertEquals(true, complete <= target);
                if (complete == target) {
                    synchronized (syncObject) {
                        syncObject.notify();
                    }
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void testDeleteObjectRequest() throws Exception{
        DeleteObjectRequest request = new DeleteObjectRequest();
        request.setBucket(QBaseServe.bucket);
        request.setCosPath(opCosPath);

        QCloudHttpRequest<DeleteObjectResult> httpRequest = qBaseServe.cosXmlService.buildDeleteObjectRequest(request);
        HttpResult<DeleteObjectResult> result = qBaseServe.cosXmlService.execute(httpRequest);
        assertEquals(true, qBaseServe.isSuccess(result.code()));
    }

    @Test
    public void testSimpleUploadRequest() throws Exception{
        Context testContext = InstrumentationRegistry.getContext();
        File localFile = QBaseServe.getLocalTempFile(testContext);
        PutObjectRequest request = new PutObjectRequest(QBaseServe.bucket, "/simple.txt", localFile.getPath());

        QCloudHttpRequest<PutObjectResult> httpRequest = qBaseServe.cosXmlService.buildPutObjectRequest(request);
        HttpResult<PutObjectResult> result = qBaseServe.cosXmlService.execute(httpRequest);

        assertEquals(true,qBaseServe.isSuccess(result.code()));
    }

    @Test
    public void testSimpleStreamUploadRequest() throws Exception{
        Context testContext = InstrumentationRegistry.getContext();
        File localFile = QBaseServe.getLocalTempFile(testContext);

        InputStream inputStream = new FileInputStream(localFile);
        PutObjectRequest request = new PutObjectRequest(QBaseServe.bucket, "/simple.txt",
                inputStream, 1000);

        QCloudHttpRequest<PutObjectResult> httpRequest = qBaseServe.cosXmlService.buildPutObjectRequest(request);
        HttpResult<PutObjectResult> result = qBaseServe.cosXmlService.execute(httpRequest);
        inputStream.close();

        assertEquals(true,qBaseServe.isSuccess(result.code()));
    }


}
