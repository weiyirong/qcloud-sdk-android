package com.tencent.cos.xml.transfer.request;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;

import com.tencent.cos.xml.model.object.PutObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于替代 {@link PutObjectRequest}，因为 Worker 方式上传有些功能不支持，
 * 比如进度监听。
 *
 * Created by rickenwang on 2019-11-19.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class WorkerUploadRequest extends TransferWorkerRequest {

    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_BYTES = "bytes";
    private static final String KEY_UPLOAD_ID = "uploadId";

    @NonNull private String filePath;

    @Nullable private byte[] bytes;

    @Nullable private String uploadId;

    public WorkerUploadRequest(@NonNull String region, @NonNull String bucket, @NonNull String key, @NonNull String filePath) {
        super(region, bucket, key);
        this.filePath = filePath;
    }

    public WorkerUploadRequest(@NonNull String region, @NonNull String bucket, @NonNull String key, @NonNull byte[] bytes) {
        super(region, bucket, key);
        this.bytes = bytes;
    }

    /**
     * 转化为 {@link Data}，可以作为 input Data 传递给 Worker
     */
    @SuppressLint("RestrictedApi")
    public Data toWorkerData() {

        Data.Builder builder =  buildWorkerData(new Data.Builder());

        if (!TextUtils.isEmpty(filePath)) {
            builder.putString(KEY_FILE_PATH, filePath);
        }

        if (bytes != null) {
            builder.putByteArray(KEY_BYTES, bytes);
        }

        if (!TextUtils.isEmpty(uploadId)) {
            builder.putString(KEY_UPLOAD_ID, uploadId);
        }

        return builder.build();
    }


    /**
     * 在 Worker 中生成 PutObjectRequest 来进行上传
     */
    public static @Nullable PutObjectRequest fromWorkerData(Data data) {

        if (data == null) {
            return null;
        }

        String region = data.getString(KEY_REGION);
        String bucket = data.getString(KEY_BUCKET);
        String key = data.getString(KEY_KEY);
        String filePath = data.getString(KEY_FILE_PATH);
        byte[] bytes = data.getByteArray(KEY_BYTES);
        String uploadId = data.getString(KEY_UPLOAD_ID);
        String headerJson = data.getString(KEY_HEADERS);

        PutObjectRequest putObjectRequest = null;

        if (!TextUtils.isEmpty(filePath)) {
            putObjectRequest = new PutObjectRequest(bucket, key, filePath);
        } else {
            putObjectRequest = new PutObjectRequest(bucket, key, bytes);
        }

        putObjectRequest.setRegion(region);

        if (headerJson != null) {
            try {
                JSONObject headerJsonObject = new JSONObject(headerJson);
                Iterator<String> keys = headerJsonObject.keys();
                HashMap<String, List<String>> headers = new HashMap<>();
                while(keys.hasNext()) {
                    String headerKey = keys.next();
                    List<String> headerValue = new LinkedList<>();
                    headerValue.add(headerJsonObject.getString(headerKey));
                    headers.put(headerKey, headerValue);
                }
                putObjectRequest.setRequestHeaders(headers);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return putObjectRequest;
    }
}
