package com.tencent.qcloud.core.network.auth;

import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

/**
 * Created by wjielai on 2017/9/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public interface QCloudSignSourceProvider {

    String source(QCloudRealCall request) throws QCloudClientException;
}
