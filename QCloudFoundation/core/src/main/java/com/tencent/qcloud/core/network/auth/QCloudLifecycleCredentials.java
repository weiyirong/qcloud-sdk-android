package com.tencent.qcloud.core.network.auth;

/**
 * Created by wjielai on 2017/9/25.
 *
 * 拥有生命周期的证书，证书会在一定时间后过期。
 *
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public interface QCloudLifecycleCredentials extends QCloudCredentials {

    /**
     * 返回临时签名
     *
     * @return signKey
     */
    String getSignKey();

    /**
     * 返回临时签名有效期
     *
     * @return keyTime
     */
    String getKeyTime();
}
