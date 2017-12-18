package com.tencent.qcloud.core.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by wjielai on 2017/9/19.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */
@RunWith(AndroidJUnit4.class)
public class QCloudMetadataTest {
    Context appContext;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void testReadMetadata() {
        QCloudMetadata metadata = QCloudMetadata.with(appContext);

        assertEquals("myappid", metadata.appId());
        assertEquals("mysecretid", metadata.secretId());
        assertEquals("mysecretkey", metadata.secretKey());
        assertEquals("myregion", metadata.region());

        assertEquals("somevalue", metadata.value("com.tencent.qcloud.somekey"));

    }

}