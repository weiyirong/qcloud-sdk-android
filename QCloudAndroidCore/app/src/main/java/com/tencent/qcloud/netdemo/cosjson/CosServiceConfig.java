package com.tencent.qcloud.netdemo.cosjson;


import com.tencent.qcloud.network.QCloudServiceConfig;

/**
<<<<<<< HEAD
 * 在底层HTTP配置参数的基础上定义一些业务共用的参数
 *
=======
>>>>>>> serializer
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CosServiceConfig extends QCloudServiceConfig {

    /**
     * 用户必填参数
     */
    private String appid;

    private String region;


    public CosServiceConfig(String appid, String region) {

        this.appid = appid;
        this.region = region;
        setHttpHost("host");
    }

    public String getAppid() {
        return appid;
    }

    public String getRegion() {
        return region;
    }
}
