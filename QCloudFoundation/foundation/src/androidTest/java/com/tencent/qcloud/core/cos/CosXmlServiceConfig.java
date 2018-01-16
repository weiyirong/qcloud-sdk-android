package com.tencent.qcloud.core.cos;

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

public final class CosXmlServiceConfig {

    static CosXmlServiceConfig instance;

    final String appid;

    final String region;

    final String host;

    private CosXmlServiceConfig(Builder builder) {
        appid = builder.appid;
        region = builder.region;
        host = builder.host;
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

    public String getHost() {
        return host;
    }

    public final static class Builder {

        private String appid;
        private String region;
        private String host;

        public Builder() {
        }

        public Builder setAppidAndRegion(String appid, String region) {
            this.appid = appid;
            this.region = region;
            this.host = getHttpHost(appid, region);
            return this;
        }

        public CosXmlServiceConfig build() {
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
