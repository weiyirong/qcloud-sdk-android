package com.tencent.cos.xml.model.object;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.tencent.cos.xml.QBaseServe;
import com.tencent.qcloud.core.network.QCloudProgressListener;


import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class AppendObjectRequestTest {

    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    @Before
    public void setUp() throws Exception {

        qBaseServe = QBaseServe.getInstance(InstrumentationRegistry.getContext());
    }

    @Test public void test1() throws Exception {

        String srcPath = qBaseServe.crateFile(1024 * 1024);
        String cosPath = "/putobject_" + System.currentTimeMillis() + ".txt";
        InputStream inputStream = new FileInputStream(srcPath);

        AppendObjectRequest request = new AppendObjectRequest(qBaseServe.bucket, cosPath, inputStream,
                inputStream.available(), 0);

//        request.setBucket(qBaseServe.bucket);
//        request.setCosPath(cosPath);
//        //request.setSrcPath(srcPath);
//        request.setInputStream(inputStream, inputStream.available());


        request.setSign(600,null,null);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d("TAG", String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        request.getProgressListener();
        assertEquals(cosPath, request.getCosPath());
        //assertEquals(srcPath, request.getSrcPath());
        List<String> uinList = new LinkedList<>();
        uinList.add("1059310888");


        AppendObjectResult result =  qBaseServe.cosXmlService.appendObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d("TAG",response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test public void test2() throws Exception {
        String srcPath = qBaseServe.crateFile(1024 * 1024);
        InputStream inputStream = new FileInputStream(srcPath);
        String cosPath = "/putobject_" + System.currentTimeMillis() + ".txt";
        AppendObjectRequest request = new AppendObjectRequest(qBaseServe.bucket, cosPath, inputStream, inputStream.available(), 0);
        assertEquals(inputStream, request.getInputStream());
        request.setSign(600,null,null);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d("TAG", String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        request.getProgressListener();
        assertEquals(cosPath, request.getCosPath());
        //assertEquals(srcPath, request.getSrcPath());
        List<String> uinList = new LinkedList<>();
        uinList.add("1059310888");


        AppendObjectResult result =  qBaseServe.cosXmlService.appendObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d("TAG",response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }


    @Test public void test3() throws Exception {
        String cosPath = "/putobject_" + System.currentTimeMillis() + ".txt";
        byte[] data = new byte[1024 * 1024];
        AppendObjectRequest request = new AppendObjectRequest(qBaseServe.bucket, cosPath, data, 0);
        request.getData();
        request.setSign(600,null,null);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d("TAG", String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        request.getProgressListener();
        assertEquals(cosPath, request.getCosPath());
        //assertEquals(srcPath, request.getSrcPath());
        List<String> uinList = new LinkedList<>();
        uinList.add("1059310888");


        AppendObjectResult result =  qBaseServe.cosXmlService.appendObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d("TAG",response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

//    @Test public void checkParameters() throws Exception {
//
//        AppendObjectRequest request = new AppendObjectRequest("", "", "", 0);
//
//        try {
//            request.checkParameters();
//        } catch (CosXmlClientException e) {
//            assertEquals("bucket must not be null", e.getDetailMessage());
//        }
//        request.setBucket("bucket");
//
//        try {
//            request.checkParameters();
//        } catch (CosXmlClientException e) {
//            assertEquals("cosPath must not be null", e.getDetailMessage());
//        }
//        request.setCosPath("");
//        try {
//            request.checkParameters();
//        } catch (CosXmlClientException e) {
//            assertEquals("Data Source must not be null", e.getDetailMessage());
//        }
//
//        request.setSrcPath("");
//        try {
//            request.checkParameters();
//        } catch (CosXmlClientException e) {
//            assertEquals("upload file does not exist", e.getDetailMessage());
//        }
//
//    }

//    @Test public void setterAndGetter() throws Exception {
//
//        AppendObjectRequest putObjectRequest = new AppendObjectRequest();
//        String cacheControl = "no-cache";
//        String contentDisposition = "attach";
//        String contentEncodeing = "utf-8";
//        String expire = "expire";
//        putObjectRequest.setCacheControl(cacheControl);
//        putObjectRequest.setContentDisposition(contentDisposition);
//        putObjectRequest.setContentEncodeing(contentEncodeing);
//        putObjectRequest.setExpires(expire);
//        putObjectRequest.setXCOSMeta("key", "value");
//        putObjectRequest.setXCOSACL("acl");
//        //putObjectRequest.setXCOSACL(new COSACL(""));
//        List<String> list = new LinkedList<>();
//        list.add("12");
//        putObjectRequest.setXCOSGrantReadWithUIN(list);
//        putObjectRequest.setXCOSGrantWriteWithUIN(list);
//        putObjectRequest.setXCOSGrantWrite(list);
//        putObjectRequest.setXCOSReadWriteWithUIN(list);
//        putObjectRequest.setXCOSReadWrite(list);
//
//    }


}