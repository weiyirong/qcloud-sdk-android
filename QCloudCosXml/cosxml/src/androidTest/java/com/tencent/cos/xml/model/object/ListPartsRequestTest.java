package com.tencent.cos.xml.model.object;



import com.tencent.cos.xml.exception.CosXmlClientException;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class ListPartsRequestTest {


    @Test public void checkParameters() throws Exception {

        ListPartsRequest listPartsRequest = new ListPartsRequest(null, null, null);
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

    @Test public void setterAndGetter() {

        ListPartsRequest listPartsRequest = new ListPartsRequest(null, null, null);
        listPartsRequest.getUploadId();
        int maxParts = 3;
        int partNumberMarker = 3;
        String encodingType = "utf-8";
        listPartsRequest.setMaxParts(maxParts);
        assertEquals(maxParts, listPartsRequest.getMaxParts());
        listPartsRequest.setPartNumberMarker(partNumberMarker);
        assertEquals(partNumberMarker, listPartsRequest.getPartNumberMarker());
        listPartsRequest.setEncodingType(encodingType);
        assertEquals(encodingType, listPartsRequest.getEncodingType());
        listPartsRequest.getCosPath();

    }
}