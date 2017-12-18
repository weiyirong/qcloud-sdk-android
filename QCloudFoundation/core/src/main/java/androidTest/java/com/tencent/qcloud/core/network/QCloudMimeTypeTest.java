package com.tencent.qcloud.core.network;

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
public class QCloudMimeTypeTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getTypeByFileName() throws Exception {
        assertEquals("image/bmp", QCloudMimeType.getTypeByFileName("txt.bmp"));

        assertEquals("application/octet-stream", QCloudMimeType.getTypeByFileName("txt.txt"));

        assertNull(QCloudMimeType.getTypeByFileName(null));
    }

}