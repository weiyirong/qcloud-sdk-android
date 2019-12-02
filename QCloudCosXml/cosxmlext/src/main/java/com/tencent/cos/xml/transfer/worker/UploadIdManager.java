package com.tencent.cos.xml.transfer.worker;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

/**
 * Created by rickenwang on 2019-11-20.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class UploadIdManager {

    private static volatile UploadIdManager instance;

    private final String UPLOAD_ID_MANAGER_SP_KEY = "upload-id-manager.sp";


    private UploadIdManager() {
    }

    public static UploadIdManager getInstance() {

        if (instance == null) {
            synchronized (UploadIdManager.class) {
                if (instance == null) {
                    instance = new UploadIdManager();
                }
            }
        }
        return instance;
    }

    public void saveUploadId(Context context, String key, String uploadId) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(UPLOAD_ID_MANAGER_SP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, uploadId);
        editor.apply();
    }

    @Nullable public String loadUploadId(Context context, String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(UPLOAD_ID_MANAGER_SP_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public void clearUploadId(Context context, String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(UPLOAD_ID_MANAGER_SP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }


    public void clearAll(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(UPLOAD_ID_MANAGER_SP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
