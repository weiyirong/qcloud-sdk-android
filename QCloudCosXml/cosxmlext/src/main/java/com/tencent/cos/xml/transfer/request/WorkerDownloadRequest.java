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
import java.util.Map;

import static com.tencent.cos.xml.transfer.request.TransferWorkerRequest.*;
import static com.tencent.cos.xml.transfer.request.WorkerUploadRequest.*;

/**
 * 用于替代 {@link PutObjectRequest}，因为 Worker 方式上传有些功能不支持，
 * 比如进度监听。
 *
 * Created by rickenwang on 2019-11-19.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class WorkerDownloadRequest extends COSWorkerRequest {

    private static final String KEY_FILE_PATH = "filePath";;


    @NonNull private String region;

    @NonNull private String bucket;

    @NonNull private String key;

    @Nullable private String filePath;

    @Nullable private Map<String, String> headers;

    public WorkerDownloadRequest(@NonNull String region, @NonNull String bucket, @NonNull String key, @NonNull String filePath) {

        this.region = region;
        this.bucket = bucket;
        this.key = key;
        this.filePath = filePath;
    }

    /**
     * 转化为 {@link Data}，可以作为 input Data 传递给 Worker
     */
    @SuppressLint("RestrictedApi")
    public Data toWorkerData() {

        Data.Builder builder =  new Data.Builder()
                .putString(KEY_REGION, region)
                .putString(KEY_BUCKET, bucket)
                .putString(KEY_KEY, key);

        if (!TextUtils.isEmpty(filePath)) {
            builder.putString(KEY_FILE_PATH, filePath);
        }

        if (cosServiceEgg != null) {
            builder.putByteArray(KEY_SERVICE_EGG, cosServiceEgg);
        }

        if (headers != null) {

            JSONObject jsonObject = new JSONObject();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            builder.putString(KEY_HEADERS, jsonObject.toString());
        }

        return builder.build();
    }


    /**
     * 在 Worker 中生成 PutObjectRequest 来进行上传
     */
    public static @Nullable PutObjectRequest fromWorkerData(Data data) {

//        if (data == null) {
//            return null;
//        }
//
//        String region = data.getString(KEY_REGION);
//        String bucket = data.getString(KEY_BUCKET);
//        String key = data.getString(KEY_KEY);
//        String filePath = data.getString(KEY_FILE_PATH);
//        byte[] bytes = data.getByteArray(KEY_BYTES);
//        String uploadId = data.getString(KEY_UPLOAD_ID);
//        String headerJson = data.getString(KEY_HEADERS);
//
//        PutObjectRequest putObjectRequest = null;
//
//        if (!TextUtils.isEmpty(filePath)) {
//            putObjectRequest = new PutObjectRequest(bucket, key, filePath);
//        } else {
//            putObjectRequest = new PutObjectRequest(bucket, key, bytes);
//        }
//
//        putObjectRequest.setRegion(region);
//
//        if (headerJson != null) {
//            try {
//                JSONObject headerJsonObject = new JSONObject(headerJson);
//                Iterator<String> keys = headerJsonObject.keys();
//                HashMap<String, List<String>> headers = new HashMap<>();
//                while(keys.hasNext()) {
//                    String headerKey = keys.next();
//                    List<String> headerValue = new LinkedList<>();
//                    headerValue.add(headerJsonObject.getString(headerKey));
//                    headers.put(headerKey, headerValue);
//                }
//                putObjectRequest.setRequestHeaders(headers);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

//        return putObjectRequest;
        return null;
    }

    public void setRegion(@Nullable String region) {
        this.region = region;
    }

    public void setHeaders(@Nullable Map<String, String> headers) {
        this.headers = headers;
    }

    @Nullable
    public String getRegion() {
        return region;
    }

    @NonNull
    public String getBucket() {
        return bucket;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @Nullable
    public String getFilePath() {
        return filePath;
    }


    @Nullable
    public Map<String, String> getHeaders() {
        return headers;
    }
}
