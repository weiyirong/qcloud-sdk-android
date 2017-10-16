package com.tencent.qcloud.core.cos;

import com.tencent.qcloud.core.network.BuildConfig;
import com.tencent.qcloud.core.network.QCloudServiceConfig;

import java.util.Locale;

/**
 * <p>
 * COS XML SDK配置项。
 * </p>
 *
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class CosXmlServiceConfig extends QCloudServiceConfig {

    static CosXmlServiceConfig instance;

    final String appid;

    final String region;

    private CosXmlServiceConfig(Builder builder) {
        super(builder);

        appid = builder.appid;
        region = builder.region;
    }

    public static CosXmlServiceConfig getInstance() {
        return instance;
    }

    public String getAppid() {
        return appid;
    }

    public String getRegion() {
        return region;
    }

    public final static class Builder extends QCloudServiceConfig.Builder<Builder> {

        private String appid;
        private String region;

        public Builder() {
            setUserAgent("cos-xml-android-sdk-" + BuildConfig.VERSION_NAME);
        }

        public Builder setAppidAndRegion(String appid, String region) {
            this.appid = appid;
            this.region = region;
            setHost(getHttpHost(appid, region));
            return this;
        }

        public CosXmlServiceConfig build() {
            setMaxLowPriorityRequestConcurrent(4);
            setMaxNormalPriorityRequestConcurrent(5);
            setMaxRequestConcurrentNumber(6);
            return new CosXmlServiceConfig(this);
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
    }

}
