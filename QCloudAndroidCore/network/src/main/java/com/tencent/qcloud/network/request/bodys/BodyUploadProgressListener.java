package com.tencent.qcloud.network.request.bodys;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface BodyUploadProgressListener {


    void onProgress(long progress, long max);
}
