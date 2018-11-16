package com.tencent.cos.xml.common;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.utils.GenerateGetObjectURLUtils;
import com.tencent.qcloud.core.auth.SessionCredentialProvider;
import com.tencent.qcloud.core.http.HttpRequest;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by bradyxiao on 2018/10/19.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ACMTest {

    @Test
    public void getCam() throws Exception{
        String appid = "1222222222";
        String bucket = "bbbbbbb";
        String region = Region.AP_Guangzhou.getRegion();
        String cosPath = "cccccc.apk";
       String sign = GenerateGetObjectURLUtils.getRequestUrlWithSign(false, "PUT",null, null, appid, bucket, region, cosPath, 600, new GenerateGetObjectURLUtils.QCloudAPI() {
            @Override
            public String getSecretKey() {
                return "3333333333dfefgrgrggthhtht";
            }

            @Override
            public String getSecretId() {
                return "erefrtyjdgjyjssfrffghyjhyjuju";
            }

            @Override
            public long getKeyDuration() {
                return 1539924473;
            }

            @Override
            public String getSessionToken() {
                return "rrttttyygtyhyhyujju";
            }
        });

        Assert.assertEquals(true, sign.startsWith("http://bbbbbbb-1222222222.cos.ap-guangzhou.myqcloud.com/cccccc.apk"));

        Assert.assertEquals(true, sign.contains("q-ak=erefrtyjdgjyjssfrffghyjhyjuju"));

        Assert.assertEquals(true, sign.contains("x-cos-security-token=rrttttyygtyhyhyujju"));

    }

}
