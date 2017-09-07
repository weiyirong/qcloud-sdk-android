package com.tencent.cos.xml.model.object;

import android.app.Application;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.tencent.cos.xml.QBaseServe;
import com.tencent.cos.xml.common.ResumeData;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.qcloud.network.QCloudProgressListener;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class MultipartUploadTest {

    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    @Before
    public void setUp() throws Exception {

        qBaseServe = QBaseServe.getInstance(InstrumentationRegistry.getContext());
    }


    @Test public void test() throws Exception {

        MultipartUpload multipartUpload = new MultipartUpload(qBaseServe.cosXmlService, null);
        assertEquals(true, multipartUpload.getCosXmlService() != null);
        multipartUpload.setBucket(qBaseServe.bucket);
        assertEquals(qBaseServe.bucket, multipartUpload.getBucket());
        String cosPath = "/" + System.currentTimeMillis() + ".txt";
        multipartUpload.setCosPath(cosPath);
        assertEquals(cosPath, multipartUpload.getCosPath());
        String srcPath = qBaseServe.crateFile(1024 * 1024 * 5);
        //assertEquals(1024 * 1024 * 5, multipartUpload.getFileLength());
        multipartUpload.setSrcPath(srcPath);
        assertEquals(srcPath, multipartUpload.getSrcPath());
        multipartUpload.setSliceSize(1024 * 1024);
        assertEquals(1024 * 1024, multipartUpload.getSliceSize());
        multipartUpload.upload2();
        Thread.sleep(100);
        multipartUpload.cancel();
        Thread.sleep(100);
        multipartUpload.upload2();
        multipartUpload.abortAsync(null);
        multipartUpload.abort();

    }

}