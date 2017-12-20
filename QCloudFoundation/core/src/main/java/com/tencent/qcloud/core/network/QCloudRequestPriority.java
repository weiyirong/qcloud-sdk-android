package com.tencent.qcloud.core.network;

/**
 * Created by wjielai on 2017/8/17.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public enum QCloudRequestPriority {

    Q_CLOUD_REQUEST_PRIORITY_HIGH(2),

    Q_CLOUD_REQUEST_PRIORITY_NORMAL(1),

    Q_CLOUD_REQUEST_PRIORITY_LOW(0);

    int value;

    QCloudRequestPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
