package com.tencent.qcloud.core.network;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by wjielai on 2017/10/11.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@RunWith(AndroidJUnit4.class)
public class QCloudRequestBufferTest {
    QCloudRequestBuffer buffer;


    @Before
    public void setUp() throws Exception {
        buffer = new QCloudRequestBuffer(4, 4, 4);
    }

    @Test
    public void testGetCall() throws Exception {
        QCloudRealCall realCall = null;
        QCloudHttpRequest request = null;
        for (int i = 0 ; i < 12; i++) {
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

            if (i % 2 == 0) {
                buffer.add(realCall);
            } else {
                buffer.addRunner(realCall);
            }


            assertEquals(realCall, buffer.getCall(request));
        }
    }

    @Test
    public void testCanRun() throws Exception {
        for (int i = 0 ; i < 30; i++) {
            QCloudHttpRequest request = new QCloudHttpRequest() {
                @Override
                protected void build() {
                    requestOriginBuilder.method("GET");
                    requestOriginBuilder.scheme("http");
                    requestOriginBuilder.host("qCloud");
                }
            };
            request.priority = QCloudRequestPriority.values()[i % 3];

            request.build();
            QCloudRealCall realCall = new QCloudRealCall(request, null);

            buffer.add(realCall);
        }

        int[] prios = new int[3];
        for (int i = 0; i < 30; i++) {
            QCloudRealCall realCall = buffer.next();
            if (realCall != null) {
                prios[realCall.priority.ordinal()] += 1;
            }
        }

        assertEquals(4, prios[0]);
        assertEquals(0, prios[1]);
        assertEquals(0, prios[2]);
    }
}
