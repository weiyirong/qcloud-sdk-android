package com.tencent.qcloud.core.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/12/14.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class HttpRequestTest {

    @Test
    public void testUrlEncode() {
        String path = "/设计//.../制造.txt";
        HttpRequest request = new HttpRequest.Builder<String>()
                .method("GET")
                .scheme("http")
                .host("www.qcloud.com")
                .path(path)
                .build();

        try {
            Assert.assertEquals(request.url().toString(), "http://www.qcloud.com/" +
                    URLEncoder.encode("设计", "UTF-8") + "//.../" +
                    URLEncoder.encode("制造", "UTF-8") + ".txt");
        } catch (UnsupportedEncodingException e) {
            Assert.assertTrue(false);
            e.printStackTrace();
        }
    }
}
