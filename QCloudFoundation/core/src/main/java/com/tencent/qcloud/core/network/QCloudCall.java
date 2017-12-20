package com.tencent.qcloud.core.network;

/**
 * Created by wjielai on 2017/8/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public interface QCloudCall {

    QCloudRequest request();

    void cancel();

    boolean isExecuted();

    boolean isCanceled();
}
