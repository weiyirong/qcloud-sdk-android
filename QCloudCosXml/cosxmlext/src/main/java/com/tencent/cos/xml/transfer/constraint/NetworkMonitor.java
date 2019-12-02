package com.tencent.cos.xml.transfer.constraint;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.tencent.cos.xml.transfer.worker.DbExecutors;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class NetworkMonitor {

    static final String ACTION_MONITOR_NETWORK = "android.net.conn.CONNECTIVITY_CHANGE";

    private static volatile NetworkMonitor instance;

    private ConstraintsDao constraintsDao;

    private NetworkMonitor(Context context) {

        init(context.getApplicationContext());

        if (!monitorByCallback()) {
            dispatchNetworkChange(NetworkUtils.getCurrentNetworkType(context));
        }

        constraintsDao = ConstraintsDatabase.create(context).constraintsDao();
    }

    private boolean monitorByCallback() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static NetworkMonitor getDefault(Context context) {

        if (instance == null) {

            synchronized (NetworkMonitor.class) {
                if (instance == null) {
                    instance = new NetworkMonitor(context);
                }
            }
        }

        return instance;
    }



    private void init(Context context) {

        if (monitorByCallback()) {

            initAfterL(context);
        } else {
            initBeforeL();
        }

    }

    /**
     * 在 API 21 (Android 7.0) 及其以上使用 NetworkCallback 来监听网络变化
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initAfterL(Context context) {

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest networkRequest = builder.build();

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    dispatchNetworkChange(NetworkType.NONE);
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);

                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                            || networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {

                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            dispatchNetworkChange(NetworkType.CELLULAR);
                        } else {
                            dispatchNetworkChange(NetworkType.WIFI);
                        }
                    }
                }
            });
        }
    }

    /**
     * 在 API 21 之前的版本使用静态注册的广播来监听网络变化
     */
    private void initBeforeL() {}

    void dispatchNetworkChange(NetworkType networkType) {

        DbExecutors.DB_EXECUTOR.execute(() -> {

            Constraints constraints = constraintsDao.getConstraints();
            if (constraints != null) {
                constraintsDao.updateNetworkType(networkType);
            } else {
                constraintsDao.insert(new Constraints.Builder().networkType(networkType).build());
            }
        });
    }

}
