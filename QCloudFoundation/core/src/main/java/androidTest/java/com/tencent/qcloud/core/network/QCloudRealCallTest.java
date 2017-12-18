package com.tencent.qcloud.core.network;

import com.tencent.qcloud.core.network.exception.QCloudClientException;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by wjielai on 2017/9/19.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */
public class QCloudRealCallTest {

    QCloudRealCall realCall;
    QCloudHttpRequest request;

    @Before
    public void setUp() throws Exception {
        request = new QCloudHttpRequest() {
            @Override
            protected void build() {
                requestOriginBuilder.method("GET");
                requestOriginBuilder.scheme("http");
                requestOriginBuilder.host("qCloud");
            }
        };

        request.build();
        realCall = new QCloudRealCall(request, null);
    }

    @Test
    public void getClientException() throws Exception {
        String clientMessage = "client message";

        IOException nativeException = new IOException("native io exception");
        QCloudClientException clientException = realCall.getClientException(nativeException);
        assertEquals("java.io.IOException: native io exception", clientException.getMessage());

        QCloudClientException causedException = new QCloudClientException(nativeException);
        clientException = realCall.getClientException(new IOException(causedException));
        assertEquals("java.io.IOException: native io exception", clientException.getMessage());

        QCloudClientException causedException2 = new QCloudClientException(clientMessage, nativeException);
        clientException = realCall.getClientException(new IOException(causedException2));
        assertEquals(clientMessage, clientException.getMessage());

        QCloudClientException messageException = new QCloudClientException(clientMessage);
        clientException = realCall.getClientException(new IOException(messageException));
        assertEquals(clientMessage, clientException.getMessage());
    }

    @Test
    public void request() throws Exception {
        assertNotNull(realCall.request());
    }

}