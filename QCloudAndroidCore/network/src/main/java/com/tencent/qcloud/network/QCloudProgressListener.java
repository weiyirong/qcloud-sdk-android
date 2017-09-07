package com.tencent.qcloud.network;


/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public interface QCloudProgressListener {

    /**
     * 上传进度回调
     *
     */
    void onProgress(long progress, long max);


}
