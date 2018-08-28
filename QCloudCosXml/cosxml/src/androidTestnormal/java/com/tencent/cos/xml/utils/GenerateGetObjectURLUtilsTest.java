package com.tencent.cos.xml.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by bradyxiao on 2018/7/10.
 */
@RunWith(AndroidJUnit4.class)
public class GenerateGetObjectURLUtilsTest {
    @Test
    public void getObjectUrl() throws Exception {
        String url = GenerateGetObjectURLUtils.getObjectUrl(true, "appid", "bucket", "region", "cospath");
        assertEquals("https://bucket-appid.cos.region.myqcloud.com/cospath",url);
        url = GenerateGetObjectURLUtils.getObjectUrl(true, "appid", "bucket-appid", "region", "cospath");
        assertEquals("https://bucket-appid.cos.region.myqcloud.com/cospath", url);
    }

    @Test
    public void getRequestUrlWithSign() throws Exception {
    }

    @Test
    public void getObjectUrlWithSign() throws Exception {
    }

    @Test
    public void getSign() throws Exception {
    }

}