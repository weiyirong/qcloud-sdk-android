package com.tencent.cos.xml.transfer.request;

import androidx.annotation.NonNull;
import androidx.work.Data;

/**
 * 上传和下载请求
 *
 * Created by rickenwang on 2019-11-21.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class TransferWorkerRequest extends COSWorkerRequest {

    public static final String KEY_REGION    = "region";
    public static final String KEY_BUCKET    = "bucket";
    public static final String KEY_KEY       = "key";

    @NonNull private String region;

    @NonNull private String bucket;

    @NonNull private String key;


    public TransferWorkerRequest(@NonNull String region, @NonNull String bucket, @NonNull String key) {
        this.region = region;
        this.bucket = bucket;
        this.key = key;
    }

    @Override
    protected Data.Builder buildWorkerData(Data.Builder builder) {
        super.buildWorkerData(builder);

        builder.putString(KEY_REGION, region);
        builder.putString(KEY_BUCKET, bucket);
        builder.putString(KEY_KEY, key);

        return builder;
    }
}
