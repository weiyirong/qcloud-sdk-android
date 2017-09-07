package com.tencent.cos.xml.model.object;

import android.support.test.InstrumentationRegistry;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.QBaseServe;
import com.tencent.cos.xml.common.ResumeData;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.exception.QCloudException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class MultipartUploadHelperTest {


    QBaseServe qBaseServe;

    @Before public void setUp() throws Exception {
        qBaseServe = QBaseServe.getInstance(InstrumentationRegistry.getContext());
    }

    @Test public void setterAndGetter() throws Exception {

        CosXmlService cosXmlService = qBaseServe.cosXmlService;
        MultipartUploadHelper multipartUploadHelper = new MultipartUploadHelper(cosXmlService);
        String bucket = "bucket";
        String cosPath = "cosPath";
        String srcPath = "srcPath";
        int silceSize = 1024;
        multipartUploadHelper.setBucket(bucket);
        assertEquals(bucket, multipartUploadHelper.getBucket());
        multipartUploadHelper.setCosPath(cosPath);
        assertEquals(cosPath, multipartUploadHelper.getCosPath());
        multipartUploadHelper.setSrcPath(srcPath);
        assertEquals(srcPath, multipartUploadHelper.getSrcPath());
        multipartUploadHelper.setSliceSize(silceSize);
        assertEquals(silceSize, multipartUploadHelper.getSliceSize());
        multipartUploadHelper.setSign(1000);

    }

    @Test public void test() throws Exception {

        final MultipartUploadHelper request = new MultipartUploadHelper(qBaseServe.cosXmlService);
        request.setBucket(qBaseServe.bucket);
        request.setCosPath("/" + System.currentTimeMillis() + ".txt");
        request.setSrcPath(qBaseServe.crateFile(1024 * 1024 * 5));
        request.setSliceSize(1024 * 1024);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {

            }
        });
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    request.upload();
                } catch (QCloudException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        Thread.sleep(100);
        ResumeData resumeData = request.cancel();
        CosXmlResult cosXmlResult = request.resume(resumeData);
        assertEquals(true, qBaseServe.isSuccess(cosXmlResult.getHttpCode()));
    }
}