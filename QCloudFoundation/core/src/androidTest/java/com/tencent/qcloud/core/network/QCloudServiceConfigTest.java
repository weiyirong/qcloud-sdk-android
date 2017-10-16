package com.tencent.qcloud.core.network;


import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@RunWith(AndroidJUnit4.class)
public class QCloudServiceConfigTest {

    @Test
    public void testBuild() {
        QCloudServiceConfig.Builder builder = new QCloudServiceConfig.Builder();
        try {
            builder.setConnectionTimeout(10);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            builder.setSocketTimeout(10);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            builder.setMaxRetryCount(1);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}
