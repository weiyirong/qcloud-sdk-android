package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.exception.CosXmlClientException;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class InitMultipartUploadRequestTest {


    @Test public void checkParameters() throws Exception {

        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(null, null);
        try {
            initMultipartUploadRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("bucket must not be null", e.getMessage());
        }
        initMultipartUploadRequest.setBucket("bucket");
        try {
            initMultipartUploadRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("cosPath must not be null", e.getMessage());
        }
    }

    @Test public void getterAndSetter() throws Exception {

        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(null, null);
        String cosPath = "cosPath";
        String cacheControl = "no-cache";
        String contentDisposition = "attach";
        String contentEncodeing = "utf-8";
        String expire = "expire";
        initMultipartUploadRequest.setCosPath(cosPath);
        assertEquals(cosPath, initMultipartUploadRequest.getCosPath());
        initMultipartUploadRequest.setCacheControl(cacheControl);
        initMultipartUploadRequest.setContentDisposition(contentDisposition);
        initMultipartUploadRequest.setContentEncoding(contentEncodeing);
        initMultipartUploadRequest.setExpires(expire);
        initMultipartUploadRequest.setXCOSMeta("key", "value");
        initMultipartUploadRequest.setXCOSACL("acl");
    }
}