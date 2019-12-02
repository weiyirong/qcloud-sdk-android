package com.tencent.cos.xml.transfer.constraint;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rickenwang on 2019-06-27.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
        if (infos != null) {
            for (NetworkInfo info : infos) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    public static NetworkType getCurrentNetworkType(Context context) {


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return NetworkType.NONE;
        }

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return NetworkType.NONE;
        }

        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return NetworkType.CELLULAR;
        } else if (isNetworkAvailable(context)) {
            return NetworkType.WIFI;
        }

        return NetworkType.NONE;
    }

}
