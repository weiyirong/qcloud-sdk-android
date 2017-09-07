package com.tencent.qcloud.network.action;

import com.tencent.qcloud.network.exception.QCloudException;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface QCloudActionResultListener {

    void onSuccess(QCloudRequestAction action);

    void onFailed(QCloudRequestAction action, QCloudException exception);
}
