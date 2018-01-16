package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by wjielai on 2017/10/13.
 */

@RunWith(AndroidJUnit4.class)
public class ServiceTest {
    private QBaseServe qBaseServe;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = new QBaseServe(appContext, new ShortTimeCredentialProvider(QBaseServe.secretId,
                QBaseServe.secretKey, 600));
    }

}
