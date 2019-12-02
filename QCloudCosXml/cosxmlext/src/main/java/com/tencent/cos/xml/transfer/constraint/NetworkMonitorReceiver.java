package com.tencent.cos.xml.transfer.constraint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by rickenwang on 2019-06-27.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class NetworkMonitorReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (intent.getAction().equalsIgnoreCase(NetworkMonitor.ACTION_MONITOR_NETWORK)) {
            NetworkMonitor.getDefault(context).dispatchNetworkChange(NetworkUtils.getCurrentNetworkType(context));
        }

    }


}
