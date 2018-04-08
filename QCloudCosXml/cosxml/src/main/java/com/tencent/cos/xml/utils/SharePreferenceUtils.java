package com.tencent.cos.xml.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bradyxiao on 2018/3/20.
 */

public class SharePreferenceUtils {
    private SharedPreferences sharedPreferences;

    public SharePreferenceUtils(Context context){
       sharedPreferences = context.getSharedPreferences("downloader", Context.MODE_PRIVATE);
    }


    public synchronized void updateValue(String localFilePath, String eTag){
        if(localFilePath != null){
            sharedPreferences.edit().putString(localFilePath, eTag).apply();
        }
    }

    public synchronized String getValue(String localFilePath){
        if(localFilePath != null){
            return sharedPreferences.getString(localFilePath, null);
        }else {
            return null;
        }
    }

    public synchronized void clear(String localFilePath){
        if(localFilePath != null){
            sharedPreferences.edit().remove(localFilePath).apply();
        }
    }

}
