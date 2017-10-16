package com.tencent.cos.xml.model.object;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.tencent.cos.xml.QBaseServe;
import com.tencent.cos.xml.exception.CosXmlClientException;
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
public class PutObjectRequestTest {


    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    @Before
    public void setUp() throws Exception {

        qBaseServe = QBaseServe.getInstance(InstrumentationRegistry.getContext());
    }

    @Test public void test1() throws Exception {

        String srcPath = qBaseServe.crateFile(1024 * 1024);
        InputStream inputStream = new FileInputStream(srcPath);
        String cosPath = "/putobject_" + System.currentTimeMillis() + ".txt";
        PutObjectRequest request = new PutObjectRequest(qBaseServe.bucket, cosPath, inputStream, inputStream.available());
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


        PutObjectResult result =  qBaseServe.cosXmlService.putObject(request);
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
        PutObjectRequest request = new PutObjectRequest(qBaseServe.bucket, cosPath, inputStream, inputStream.available());
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


        PutObjectResult result =  qBaseServe.cosXmlService.putObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d("TAG",response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }


    @Test public void test3() throws Exception {
        String cosPath = "/putobject_" + System.currentTimeMillis() + ".txt";
        byte[] data = new byte[]{1,2,3};
        PutObjectRequest request = new PutObjectRequest(qBaseServe.bucket, cosPath, data);
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


        PutObjectResult result =  qBaseServe.cosXmlService.putObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d("TAG",response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test public void checkParameters() throws Exception {
        String bucket = null;
        String cosPath = null;
        byte[] data = null;
        PutObjectRequest request = new PutObjectRequest(bucket, cosPath, data);

        try {
            request.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("bucket must not be null", e.getMessage());
        }
        request.setBucket("bucket");

        try {
            request.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("cosPath must not be null", e.getMessage());
        }
        request.setCosPath("");
        try {
            request.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("Data Source must not be null", e.getMessage());
        }

        request.setSrcPath("");
        try {
            request.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("upload file does not exist", e.getMessage());
        }

    }

    @Test public void setterAndGetter() throws Exception {
        String bucket = null;
        String cosPath = null;
        byte[] data = null;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, data);
        String cacheControl = "no-cache";
        String contentDisposition = "attach";
        String contentEncodeing = "utf-8";
        String expire = "expire";
        putObjectRequest.setCacheControl(cacheControl);
        putObjectRequest.setContentDisposition(contentDisposition);
        putObjectRequest.setContentEncodeing(contentEncodeing);
        putObjectRequest.setExpires(expire);
        putObjectRequest.setXCOSMeta("key", "value");
        putObjectRequest.setXCOSACL("acl");
        //putObjectRequest.setXCOSACL(new COSACL(""));
        List<String> list = new LinkedList<>();
        list.add("12");



    }

}