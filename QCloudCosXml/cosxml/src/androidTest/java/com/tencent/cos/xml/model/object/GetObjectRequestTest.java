package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.network.Range;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class GetObjectRequestTest {


    @Test public void build() throws Exception {

        GetObjectRequest getObjectRequest = new GetObjectRequest(null,
                null, null);
        getObjectRequest.setCosPath("he");
        getObjectRequest.build();
    }

    @Test public void checkParameters() throws Exception {

        GetObjectRequest initMultipartUploadRequest = new GetObjectRequest(null,
                null, null);
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

    @Test public void setterAndGetter() {

        GetObjectRequest getObjectRequest = new GetObjectRequest(null,
                null, null);
        getObjectRequest.setRspContentType("text/txt");
        assertEquals("text/txt", getObjectRequest.getRspContentType());
        String expire = "expire";
        String expact = "expact";
        String cacheControl = "no-cache";
        String contentDisposition = "attach";
        String contentEncoding = "utf-8";
        String cosPath = "/cos";
        Range range = new Range(20,100);
        String ifModifiedSince = "modify";
        String savePath = "path";

        getObjectRequest.setExpect(expact);
        assertEquals(expact, getObjectRequest.getExpect());
        getObjectRequest.setRspExpires(expire);
        assertEquals(expire, getObjectRequest.getRspExpires());
        getObjectRequest.setRspCacheControl(cacheControl);
        assertEquals(cacheControl, getObjectRequest.getRspCacheControl());
        getObjectRequest.setRspContentDispositon(contentDisposition);
        assertEquals(contentDisposition, getObjectRequest.getRspContentDispositon());
        getObjectRequest.setRspContentEncoding(contentEncoding);
        assertEquals(contentEncoding, getObjectRequest.getRspContentEncoding());
        getObjectRequest.setCosPath(cosPath);
        assertEquals(cosPath, getObjectRequest.getCosPath());
        getObjectRequest.setRange(100);
        assertEquals(true, getObjectRequest.getRange() != null);
        getObjectRequest.setIfModifiedSince(ifModifiedSince);
        getObjectRequest.setSavePath(savePath);
        assertEquals(savePath, getObjectRequest.getSavePath());
    }

}