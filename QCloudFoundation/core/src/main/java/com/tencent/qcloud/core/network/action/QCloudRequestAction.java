package com.tencent.qcloud.core.network.action;

import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import okhttp3.Request;

/**
 * 专为序列化HTTP请求所需要的长时间操作定义的QCloudAction类。
 *
 * 如：计算签名、计算文件SHA1值
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface QCloudRequestAction {

    Request execute(QCloudRealCall request) throws QCloudClientException;
}
