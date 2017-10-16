package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.exception.CosXmlClientException;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class CompleteMultiUploadRequestTest {

    @Test public void setterAndGetter() throws Exception {

        CompleteMultiUploadRequest completeMultiUploadRequest = new CompleteMultiUploadRequest(null,
                null, null, null);
        completeMultiUploadRequest.getCompleteMultipartUpload();
        Map<Integer, String> shas = new HashMap<>();
        shas.put(1, "SHA");
        completeMultiUploadRequest.setPartNumberAndETag(shas);
        String uploadId = "id";
        completeMultiUploadRequest.setUploadId(uploadId);
        assertEquals(uploadId, completeMultiUploadRequest.getUploadId());
        String cosPath = "cosPath";
        completeMultiUploadRequest.setCosPath(cosPath);
        assertEquals(cosPath, completeMultiUploadRequest.getCosPath());

    }

    @Test public void checkParameters() throws Exception {

        CompleteMultiUploadRequest listPartsRequest = new CompleteMultiUploadRequest(null,
                null, null, null);
        try {
            listPartsRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("bucket must not be null", e.getMessage());
        }
        listPartsRequest.setBucket("bucket");
        try {
            listPartsRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("cosPath must not be null", e.getMessage());
        }
        listPartsRequest.setCosPath("/cos");
        try {
            listPartsRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("uploadID must not be null", e.getMessage());
        }
    }


}