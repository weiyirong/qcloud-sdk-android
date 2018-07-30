package com.tencent.cos.xml.transfer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class NetworkMonitor  {

    static final int MSG_DISCONNECT = 300;
    static final int MSG_CONNECT = 400;

    private NetworkInfoReceiver networkInfoReceiver;

    /**
     * Constants of intent action sent to the service.
     */
    static final String INTENT_ACTION_TRANSFER_ADD = "add_transfer";

    /**
     * registers a BroadcastReceiver to receive network status change event.
     */
    private boolean isReceiverNotRegistered = true;


    /**
     * update network state and start or stop transfer if need.
     */
    private HandlerThread handlerThread;
    private Handler updateHandler;


    private List<NetworkListener> networkListeners;

    NetworkMonitor() {

        networkListeners = new LinkedList<>();
    }

    void addNetworkListener(NetworkListener networkListener) {

        networkListeners.add(networkListener);
    }

    void removeNetworkListener(NetworkListener networkListener) {

        networkListeners.remove(networkListener);
    }

    synchronized void register(Context context) {

        if (isReceiverNotRegistered) {

            QCloudLogger.i(TransferUtility.TRANSFER_UTILITY_TAG, "registering receiver");

            handlerThread = new HandlerThread("COSTransferUpdateHandlerThread");
            handlerThread.start();
            setHandlerLooper(context, handlerThread.getLooper());

            try {

                context.registerReceiver(this.networkInfoReceiver,
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (final IllegalArgumentException iae) {
                QCloudLogger.w(TransferUtility.TRANSFER_UTILITY_TAG,"Ignoring the exception trying to register the receiver for connectivity change.");
            } catch (final IllegalStateException ise) {
                QCloudLogger.w(TransferUtility.TRANSFER_UTILITY_TAG,"Ignoring the leak in registering the receiver.");
            } finally {
                isReceiverNotRegistered = false;
            }
        }
    }


    void unregister(Context context) {

        try {
            if (networkInfoReceiver != null) {
                QCloudLogger.i(TransferUtility.TRANSFER_UTILITY_TAG, "unregistering receiver");
                context.unregisterReceiver(this.networkInfoReceiver);
                isReceiverNotRegistered = true;
            }
        } catch (final IllegalArgumentException iae) {
            /*
             * Ignore on purpose, just in case the service stops before
             * onStartCommand where the receiver is registered.
             */
            QCloudLogger.w(TransferUtility.TRANSFER_UTILITY_TAG, "exception trying to destroy the service");
        }
    }



    class UpdateHandler extends Handler {

        public UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MSG_CONNECT) {
                notifyNetworkReconnect();
            } else if (msg.what == MSG_DISCONNECT) {

                notifyNetworkDisconnect();
            }
        }
    }

    /**
     * pause if transfer is in progress
     */
    private void notifyNetworkDisconnect() {

        QCloudLogger.i(TransferUtility.TRANSFER_UTILITY_TAG, "pause transfer on network disconnect");
        for (NetworkListener listener : networkListeners) {
            listener.onDisconnect();
        }
    }

    /**
     * restart if transfer is waiting for network
     */
    private void notifyNetworkReconnect() {

        QCloudLogger.i(TransferUtility.TRANSFER_UTILITY_TAG, "resume transfer on network reconnect");
        for (NetworkListener listener : networkListeners) {
            listener.onReconnect();
        }
    }

    static class NetworkInfoReceiver extends BroadcastReceiver {
        private final Handler handler;
        private final ConnectivityManager connManager;

        /**
         * Constructs a NetworkInfoReceiver.
         *
         * @param handler a handle to send message to
         */
        public NetworkInfoReceiver(Context context, Handler handler) {
            this.handler = handler;
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                final boolean networkConnected = isNetworkConnected();
                QCloudLogger.i(TransferUtility.TRANSFER_UTILITY_TAG, "Network connected: " + networkConnected);
                handler.sendEmptyMessage(networkConnected ? MSG_CONNECT : MSG_DISCONNECT);
            }
        }

        /**
         * Gets the status of network connectivity.
         *
         * @return true if network is connected, false otherwise.
         */
        boolean isNetworkConnected() {
            final NetworkInfo info = connManager.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
    }

    private void setHandlerLooper(Context context, Looper looper) {

        updateHandler = new UpdateHandler(looper);
        networkInfoReceiver = new NetworkInfoReceiver(context.getApplicationContext(), updateHandler);
    }


    interface NetworkListener {

        void onReconnect();

        void onDisconnect();
    }

}
