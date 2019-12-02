package com.tencent.cos.xml.transfer.worker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.tencent.cos.xml.transfer.constraint.Constraints;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@Entity(primaryKeys = {"bucket", "key", "filePath"})
public class TransferSpec {

    @NonNull
    public String id;

    @NonNull
    public String region;       //

    @NonNull
    public String bucket;       //

    @NonNull
    public String key;          //

    @NonNull
    public String filePath;

    @Nullable
    public String uploadId;     // 分片上传 id

    @Nullable
    public String workerId;     // worker request id

    @NonNull
    public boolean isUpload; // upload or download

    @Nullable
    public String headers;      // 通过一个 JSON 字符串保存

    @NonNull
    public boolean completeBackground;     //

    @Nullable
    public String etag;         //

    @Nullable
    public String serviceEgg;   //

    @Embedded
    @NonNull
    public Constraints constraints = Constraints.NONE;

    @Ignore
    public TransferSpec(String region, String bucket, String key, String filePath, String headers, boolean isUpload) {

        id = isUpload ? "upload : " + filePath + " to " + bucket + "/" + key :
            "download : " + bucket + "/" + key + " to " + filePath;

        this.region = region;
        this.bucket = bucket;
        this.key = key;
        this.filePath = filePath;
        this.headers = headers;
        this.isUpload = isUpload;
        this.completeBackground = false;
    }


    /**
     * @hide
     */
    // @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public TransferSpec() { // stub required for room
    }

    public boolean isCompleteBackground() {
        return completeBackground;
    }

    public String getEtag() {
        return etag;
    }

    public String getWorkerId() {
        return workerId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setServiceEgg(@NonNull byte[] serviceEgg) {
        this.serviceEgg = new String(serviceEgg);
    }

    @Nullable
    public byte[] getServiceEgg() {
        return serviceEgg != null ? serviceEgg.getBytes() : null;
    }

    public void setUploadId(@Nullable String uploadId) {
        this.uploadId = uploadId;
    }
}
