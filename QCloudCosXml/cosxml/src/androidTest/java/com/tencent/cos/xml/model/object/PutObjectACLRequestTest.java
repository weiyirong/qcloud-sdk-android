package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.exception.CosXmlClientException;


import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class PutObjectACLRequestTest {


    @Test public void checkParameters() throws Exception {

        PutObjectACLRequest putObjectACLRequest = new PutObjectACLRequest(null, null);
        try {
            putObjectACLRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("bucket must not be null", e.getMessage());
        }
        putObjectACLRequest.setBucket("bucket");
        try {
            putObjectACLRequest.checkParameters();
        } catch (CosXmlClientException e) {
            assertEquals("cosPath must not be null", e.getMessage());
        }
    }

    @Test public void setterAndGetter() throws Exception {

        PutObjectACLRequest putObjectACLRequest = new PutObjectACLRequest(null, null);
        putObjectACLRequest.setCosPath("path");
        assertEquals("path", putObjectACLRequest.getCosPath());
        putObjectACLRequest.setXCOSACL(COSACL.PRIVATE);
        List<String> ids = new LinkedList<>();
        ids.add("131");

    }


}