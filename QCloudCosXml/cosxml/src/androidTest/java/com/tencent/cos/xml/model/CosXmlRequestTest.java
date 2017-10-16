package com.tencent.cos.xml.model;



import com.tencent.cos.xml.exception.CosXmlClientException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class CosXmlRequestTest {

    private CosXmlRequest cosXmlRequest;
    private String jsonContentType = "text/json";
    private String expect = "expect";
    private String sha = "sha";

    @Before public void setUp() throws Exception {


        cosXmlRequest = new DefaultCosXmlRequest();
    }

    @Test
    public void getContentType() throws Exception {

        assertEquals(jsonContentType, cosXmlRequest.getContentType());
    }

    @Test
    public void setExpect() throws Exception {

        cosXmlRequest.setExpect(expect);
        assertEquals(expect, cosXmlRequest.getExpect());
    }

    @Test
    public void getExpect() throws Exception {

    }

    @Test
    public void setXCOSContentSha1() throws Exception {

        cosXmlRequest.setContentMd5(sha);
        assertEquals(sha, cosXmlRequest.getContentMd5());
    }

    @Test
    public void getRequestHeaders() throws Exception {

        assertEquals(true, cosXmlRequest.getRequestQueryParams() != null);
    }

    @Test
    public void getRequestQueryParams() throws Exception {

        cosXmlRequest.getRequestQueryParams();
    }

    @Test
    public void getRequestBodyParams() throws Exception {

        assertEquals(true, cosXmlRequest.getRequestBodyParams() != null);
    }

    @Test
    public void getXCOSContentSha1() throws Exception {

    }

    class DefaultCosXmlRequest extends CosXmlRequest {

        public DefaultCosXmlRequest() {

            contentType = jsonContentType;
        }

        @Override
        protected void setRequestMethod() {

        }

        @Override
        protected void setRequestPath() {

        }

        @Override
        protected void setRequestQueryParams() {

        }

        @Override
        public void checkParameters() throws CosXmlClientException {

        }

        @Override
        public void build() {

        }
    }

}