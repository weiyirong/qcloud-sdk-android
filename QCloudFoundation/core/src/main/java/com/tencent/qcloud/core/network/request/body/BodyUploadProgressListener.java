package com.tencent.qcloud.core.network.request.body;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface BodyUploadProgressListener {


    void onProgress(long progress, long max);
}
