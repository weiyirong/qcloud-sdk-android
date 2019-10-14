package com.tencent.cos.xml.csp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.cos.xml.BuildConfig;
import com.tencent.cos.xml.QServer;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by rickenwang on 2018/9/4.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@RunWith(AndroidJUnit4.class)
public class CSPTest {

    private static BaseTest baseSimpleTest;

    String appid = BuildConfig.CSP_APPID;
    String secretId = BuildConfig.CSP_SECRET_ID;
    String secretKey = BuildConfig.CSP_SECRET_KEY;
    String region = "wh";


    @BeforeClass public static void init() {

    }

    @Test public void test() {
        if(!QServer.cspTest)return;
        Context context = InstrumentationRegistry.getContext();
        String endpointSuffix = "cos." + region + ".yun.ccb.com";
        String bucket = "rickenwang";

        baseSimpleTest = new BaseTest(context, appid, bucket, secretId, secretKey, region, endpointSuffix);
        baseSimpleTest.testSimpleSyncMethod();
    }

    @Test public void testRegionNull() {
        if(!QServer.cspTest)return;
        Context context = InstrumentationRegistry.getContext();
        String endpointSuffix = "cos.wh.yun.ccb.com";
        String bucket = "rickenwang";

        baseSimpleTest = new BaseTest(context, appid, bucket, secretId, secretKey, "", endpointSuffix);
        baseSimpleTest.testSimpleSyncMethod();
    }

    @Test public void testAppidNull() {
        if(!QServer.cspTest)return;
        Context context = InstrumentationRegistry.getContext();
        String endpointSuffix = "cos." + region + ".yun.ccb.com";
        String bucket = "rickenwang-1255000008";

        baseSimpleTest = new BaseTest(context, "", bucket, secretId, secretKey, region, endpointSuffix);
        baseSimpleTest.testSimpleSyncMethod();
    }

    @Test public void testTrunkedDownload() {
        if(!QServer.cspTest)return;
        baseSimpleTest.testCspTrunkedDownload();
    }
}
