package com.tencent.qcloud.core.logger;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by wjielai on 2017/8/28.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@RunWith(AndroidJUnit4.class)
public class QCloudLoggerTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
        QCloudLogger.setUp(context);
    }

    @Test
    public void testWriteLog() {
        String tag = "TestTag";

        QCloudLogger.v(tag, "this is a verbose");
        QCloudLogger.v(tag, new NullPointerException(), "this is a verbose");

        QCloudLogger.d(tag, "this is a debug");
        QCloudLogger.d(tag, new NullPointerException(), "this is a debug");

        QCloudLogger.i(tag, "this is a info");
        QCloudLogger.i(tag, new NullPointerException(), "this is a info");

        QCloudLogger.w(tag, "this is a warning");
        QCloudLogger.w(tag, new NullPointerException(), "this is a warning");

        QCloudLogger.e(tag, "this is a error");
        QCloudLogger.e(tag, new NullPointerException(), "this is a error");


    }

    @Test
    public void testFormatError() throws Exception {
        String tag = "TestTag";

        QCloudLogger.v(tag, "this is a verbose %s", "s");
        QCloudLogger.v(tag, new NullPointerException(), "this is a verbose %s", "s");

        QCloudLogger.d(tag, "this is a debug %s", "s");
        QCloudLogger.d(tag, new NullPointerException(), "this is a debug %s", "s");

        QCloudLogger.i(tag, "this is a info %s", 1);
        QCloudLogger.i(tag, new NullPointerException(), "this is a info %s", "s");

        QCloudLogger.w(tag, "this is a warning %s", "s");
        QCloudLogger.w(tag, new NullPointerException(), "this is a warning %s", "s");

        QCloudLogger.e(tag, "this is a error %s", "s");
        QCloudLogger.e(tag, new NullPointerException(), "this is a error %s", "s");


        QCloudLogger.i(tag, "this is a info %%E4%B8%AD%E6%96%87");


    }
}
