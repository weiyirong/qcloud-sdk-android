package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.auth.SessionCredentialProvider;
import com.tencent.qcloud.core.http.HttpRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/12/5.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CredentialTest {

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testUseLocalCredentialProvider() throws Exception{
        ShortTimeCredentialProvider credentialProvider = new ShortTimeCredentialProvider(QBaseServe.secretId, QBaseServe.secretKey, 600);
        QCloudLifecycleCredentials credentials = (QCloudLifecycleCredentials) credentialProvider.getCredentials();
        Assert.assertNotNull(credentials.getSignKey());
    }

    @Test
    public void testUseInternalCredentialProvider() throws Exception{
        SessionCredentialProvider credentialProvider = new SessionCredentialProvider(new HttpRequest.Builder<String>()
                .scheme("http")
                .host("203.195.147.180")
                .path("/app/sts")
                .method("GET")
                .query("bucket", "tactest-" + QBaseServe.appId)
                .build());
        QCloudLifecycleCredentials credentials = (QCloudLifecycleCredentials) credentialProvider.getCredentials();
        Assert.assertNotNull(credentials.getSignKey());
    }
}
