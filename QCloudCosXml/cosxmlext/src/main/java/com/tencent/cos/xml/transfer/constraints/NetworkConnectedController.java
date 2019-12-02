package com.tencent.cos.xml.transfer.constraints;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.impl.constraints.NetworkState;
import androidx.work.impl.constraints.trackers.Trackers;
import androidx.work.impl.model.WorkSpec;
import androidx.work.impl.utils.taskexecutor.TaskExecutor;

import static androidx.work.NetworkType.CONNECTED;

/**
 * Created by rickenwang on 2019-11-27.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class NetworkConnectedController extends ConstraintController<NetworkState> {
    public NetworkConnectedController(Context context, TaskExecutor taskExecutor) {
        super(TransferTrackers.getInstance(context, taskExecutor).getNetworkStateTracker());
    }

    @Override
    boolean hasConstraint(@NonNull WorkSpec workSpec) {
        return workSpec.constraints.getRequiredNetworkType() == CONNECTED;
    }

    @Override
    boolean isConstrained(@NonNull NetworkState state) {
        if (Build.VERSION.SDK_INT >= 26) {
            return !state.isConnected() || !state.isValidated();
        } else {
            return !state.isConnected();
        }
    }
}
