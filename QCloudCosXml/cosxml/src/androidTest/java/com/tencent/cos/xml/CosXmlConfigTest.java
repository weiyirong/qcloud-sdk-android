package com.tencent.cos.xml;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * </p>
 * Created by wjielai on 2018/11/21.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class CosXmlConfigTest {

    @Test
    public void testIpPortHost() {
        CosXmlServiceConfig config = new CosXmlServiceConfig.Builder()
                .setHost(Uri.parse("https://127.0.0.1:8080"))
                .builder();
        Assert.assertEquals(config.getHost("", false),
                "127.0.0.1");
        Assert.assertEquals(config.getPort(),
                8080);
        config = new CosXmlServiceConfig.Builder()
                .setHost(Uri.parse("https://127.0.0.1"))
                .builder();
        Assert.assertEquals(config.getHost("", false),
                "127.0.0.1");
    }

    @Test
    public void testCSPHost() {
        CosXmlServiceConfig config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "region")
                .setBucketInPath(false)
                .builder();
        String host = config.getHost("bucket-1250000000",false);
        Assert.assertEquals("bucket-1250000000.cos.region.myqcloud.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(null, "region")
                .setBucketInPath(false)
                .builder();
        host = config.getHost("bucket",false);
        Assert.assertEquals("bucket.cos.region.myqcloud.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(null, "region")
                .setBucketInPath(false)
                .builder();
        host = config.getHost("bucket-1250000000",false);
        Assert.assertEquals("bucket-1250000000.cos.region.myqcloud.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "region")
                .setBucketInPath(false)
                .setEndpointSuffix("cos.region.yun.tce.com")
                .builder();
        host = config.getHost("bucket",false);
        Assert.assertEquals("bucket-1250000000.cos.region.yun.tce.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "region")
                .setBucketInPath(true)
                .setEndpointSuffix("cos.region.yun.tce.com")
                .builder();
        host = config.getHost("bucket",false);
        Assert.assertEquals("cos.region.yun.tce.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "region")
                .setBucketInPath(true)
                .setEndpointSuffix("cos.yun.tce.com")
                .builder();
        host = config.getHost("bucket",false);
        Assert.assertEquals("cos.yun.tce.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "region")
                .setBucketInPath(false)
                .setEndpointSuffix("cos.${region}.yun.tce.com")
                .builder();
        host = config.getHost("bucket",true);
        Assert.assertEquals("bucket-1250000000.cos-accelerate.yun.tce.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "ap-guangzhou")
                .setBucketInPath(false)
                .setEndpointSuffix("cos.${region}.myqcloud.com")
                .builder();
        host = config.getHost("bucket-1250000000",false);
        Assert.assertEquals("bucket-1250000000.cos.ap-guangzhou.myqcloud.com", host);

        config = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion("1250000000", "ap-guangzhou")
                .builder();
        Assert.assertEquals("cos-accelerate.myqcloud.com",
                config.getEndpointSuffix(config.getRegion(),true));
    }
}
