package com.tencent.cos.xml.model.object;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.tencent.qcloud.network.exception.QCloudException;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class UploadPartRequestTest {


    @Test public void build() throws Exception {

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setCosPath("");
        InputStream inputStream = new FileInputStream(File.createTempFile("test", "txt"));
        uploadPartRequest.setInputStream(inputStream, 10);
        uploadPartRequest.build();
    }


    @Test public void checkParameters() throws Exception {

        UploadPartRequest uploadPartRequest = new UploadPartRequest();

        try {
            uploadPartRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("bucket must not be null", e.getDetailMessage());
        }

        uploadPartRequest.setBucket("bucket");
        try {
            uploadPartRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("partNumber must be >= 1", e.getDetailMessage());
        }

        uploadPartRequest.setPartNumber(1);
        try {
            uploadPartRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("uploadID must not be null", e.getDetailMessage());
        }

        uploadPartRequest.setUploadId("id");
        try {
            uploadPartRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("cosPath must not be null", e.getDetailMessage());
        }

        uploadPartRequest.setCosPath("cospath");
        try {
            uploadPartRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("Data Source must not be null", e.getDetailMessage());
        }

        uploadPartRequest.setSrcPath("src path");
        try {
            uploadPartRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("upload file does not exist", e.getDetailMessage());
        }

    }

    @Test public void setterAndGetter() throws Exception {

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        String cosPath = "cosPath";
        String srcPath = "srcPath";
        byte[] data = new byte[3];
        InputStream inputStream = new FileInputStream(File.createTempFile("test", "txt"));
        uploadPartRequest.setCosPath(cosPath);
        assertEquals(cosPath, uploadPartRequest.getCosPath());
        uploadPartRequest.setSrcPath(srcPath);
        assertEquals(srcPath, uploadPartRequest.getSrcPath());
        uploadPartRequest.setData(data);
        assertEquals(data, uploadPartRequest.getData());
        uploadPartRequest.setInputStream(inputStream, 100);
        assertEquals(inputStream, uploadPartRequest.getInputStream());


    }
}