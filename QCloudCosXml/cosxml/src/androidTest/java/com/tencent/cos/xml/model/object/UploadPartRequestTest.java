package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.exception.CosXmlClientException;

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
        String cosPath = "cosPath";
        String srcPath = "srcPath";
        byte[] data = new byte[3];
        InputStream inputStream = new FileInputStream(File.createTempFile("test", "txt"));
        UploadPartRequest uploadPartRequest = new UploadPartRequest("bucket", cosPath, 1, srcPath, "uploadId");
        uploadPartRequest.setCosPath("");
        uploadPartRequest.setInputStream(inputStream, 10);
        uploadPartRequest.build();
    }


    @Test public void checkParameters() throws Exception {
        String cosPath = "cosPath";
        String srcPath = "srcPath";
        byte[] data = new byte[3];
        InputStream inputStream = new FileInputStream(File.createTempFile("test", "txt"));
        UploadPartRequest uploadPartRequest = new UploadPartRequest("bucket", cosPath, 1, srcPath, "uploadId");

        try {
            uploadPartRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("bucket must not be null", e.getMessage());
        }

        uploadPartRequest.setBucket("bucket");
        try {
            uploadPartRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("partNumber must be >= 1", e.getMessage());
        }

        uploadPartRequest.setPartNumber(1);
        try {
            uploadPartRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("uploadID must not be null", e.getMessage());
        }

        uploadPartRequest.setUploadId("id");
        try {
            uploadPartRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("cosPath must not be null", e.getMessage());
        }

        uploadPartRequest.setCosPath("cospath");
        try {
            uploadPartRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("Data Source must not be null", e.getMessage());
        }

        uploadPartRequest.setSrcPath("src path");
        try {
            uploadPartRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("upload file does not exist", e.getMessage());
        }

    }

    @Test public void setterAndGetter() throws Exception {
        String cosPath = "cosPath";
        String srcPath = "srcPath";
        byte[] data = new byte[3];
        InputStream inputStream = new FileInputStream(File.createTempFile("test", "txt"));
        UploadPartRequest uploadPartRequest = new UploadPartRequest("bucket", cosPath, 1, srcPath, "uploadId");
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