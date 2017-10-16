package com.tencent.qcloud.core.network;

import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestBodySerializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

import static org.junit.Assert.*;

/**
 * Created by wjielai on 2017/9/19.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */
@RunWith(AndroidJUnit4.class)
public class QCloudHttpRequestBuilderTest {

    QCloudHttpRequestBuilder qCloudHttpRequestBuilder;
    @Before
    public void setUp() throws Exception {
        qCloudHttpRequestBuilder = new QCloudHttpRequestBuilder();

        qCloudHttpRequestBuilder.scheme("http");
        qCloudHttpRequestBuilder.hostAddFront("tencentqlcoud");
        qCloudHttpRequestBuilder.hostAddRear("-r");
        qCloudHttpRequestBuilder.pathAddFront("pp2");
        qCloudHttpRequestBuilder.pathAddFront("pp1");
        qCloudHttpRequestBuilder.pathAddRear("pr");

        qCloudHttpRequestBuilder.query("qkey", "qvalue");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalScheme() throws Exception {
        qCloudHttpRequestBuilder.scheme("illegal");
    }

    @Test
    public void testFullHost() throws Exception {
        qCloudHttpRequestBuilder.host("fullhost");
        qCloudHttpRequestBuilder.method("GET");
        Request request = qCloudHttpRequestBuilder.build();
        assertEquals("fullhost", request.url().host());
    }

    @Test
    public void testFullUrl() throws Exception {
        qCloudHttpRequestBuilder.fullUrl("http://www.tencent.com");
        qCloudHttpRequestBuilder.method("GET");
        Request request = qCloudHttpRequestBuilder.build();
        assertEquals("http://www.tencent.com/", request.url().toString());
    }

    @Test
    public void testBuildGetRequest() throws Exception {
        qCloudHttpRequestBuilder.method("GET");
        Request request = qCloudHttpRequestBuilder.build();
        assertRequest(request);
    }

    @Test
    public void testBuildPostRequestWithBody() throws Exception {
        qCloudHttpRequestBuilder.method("POST");

        qCloudHttpRequestBuilder.body(new RequestBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {

            }
        });

        Request request = qCloudHttpRequestBuilder.build();

        assertRequest(request);
    }

    @Test
    public void testBuildPostRequestWithSerializer() throws Exception {
        qCloudHttpRequestBuilder.method("POST");

        qCloudHttpRequestBuilder.body(new RequestBodySerializer() {
            @Override
            public RequestBody serialize() throws QCloudClientException {
                return null;
            }
        });

        Request request = qCloudHttpRequestBuilder.build();
        assertRequest(request);
    }

    private void assertRequest(Request request) {
        assertEquals("http://tencentqlcoud-r/pp1/pp2/pr?qkey=qvalue", request.url().toString());
    }

}