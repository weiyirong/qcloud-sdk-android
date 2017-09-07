package com.tencent.cos.xml;

import com.tencent.qcloud.network.QCloudServiceConfig;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CosXmlServiceConfig extends QCloudServiceConfig {

    // appid for cos
    private String appid;

    // location for bucket, such as, cn-north, cn-south,cn-east,etc.
    private String region;

    /**
     * construction method for CosXmlServiceConfig
     * @param appid, appid for cos {<a https://www.qcloud.com/document/product/436/6225></a>}
     * @param region, {@link com.tencent.cos.xml.common.Region}
     */
    public CosXmlServiceConfig(String appid, String region) {

        this.appid = appid;
        this.region = region;

        setHttpHost(getHttpHost(appid, region));
        setUserAgent("cos-xml-android-sdk-1.1");

    }

    /**
     * 对于一些比较老的区域，域名不加入"cos"，否则会导致https证书验证出问题。
     *
     * @param appid
     * @param region
     * @return
     */
    private String getHttpHost(String appid, String region) {
        // 如果用户设置的region以cos开头，自动认为是新区域
        if (!region.startsWith("cos.")) {
            boolean isOldRegion = false;
            final String[] old_regions = new String[] {
                    "cn-east", "cn-south", "cn-north", "cn-south-2", "cn-southwest", "sg"
            };

            for (String old_region : old_regions) {
                if (old_region.equals(region)) {
                    isOldRegion = true;
                    break;
                }
            }

            if (!isOldRegion) {
                region = "cos." + region;
            }
        }

        return String.format(Locale.ENGLISH, "-%s.%s.myqcloud.com", appid, region);
    }

    /**
     *   others setting for cosXmlServiceConfig
     *   for http or https protocol: {@link com.tencent.qcloud.network.QCloudServiceConfig#setHttpProtocol(boolean)}
     *   for read or write time for socket stream: {@link QCloudServiceConfig#setSocketTimeout(int)}
     *   for timeout for socket connection: {@link QCloudServiceConfig#setConnectionTimeout(int)}
     *   for retry counts for request: {@link QCloudServiceConfig#setMaxRetryCount(int)}
     */

}
