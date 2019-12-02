package com.tencent.cos.xml.transfer.constraints;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.work.Logger;
import androidx.work.impl.constraints.WorkConstraintsCallback;
import androidx.work.impl.constraints.WorkConstraintsTracker;
import androidx.work.impl.model.WorkSpec;
import androidx.work.impl.utils.taskexecutor.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickenwang on 2019-11-27.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TransferConstraintsTracker implements ConstraintController.OnConstraintUpdatedCallback {


    private static final String TAG = Logger.tagWithPrefix("TransferConstraintsTracker");

    @Nullable
    private final WorkConstraintsCallback mCallback;
    private final ConstraintController<?>[] mConstraintControllers;

    // We need to keep hold a lock here for the cases where there is 1 WCT tracking a list of
    // WorkSpecs. Changes in constraints are notified on the main thread. Enqueues / Cancellations
    // occur on the task executor thread pool. So there is a chance of
    // ConcurrentModificationExceptions.
    private final Object mLock;

    /**
     * @param context      The application {@link Context}
     * @param taskExecutor The {@link TaskExecutor} being used by WorkManager.
     * @param callback     The callback is only necessary when you need
     *                     {@link TransferConstraintsTracker} to notify you about changes in
     *                     constraints for the list of {@link WorkSpec}'s that it is tracking.
     */
    public TransferConstraintsTracker(
            @NonNull Context context,
            @NonNull TaskExecutor taskExecutor,
            @Nullable WorkConstraintsCallback callback) {

        Context appContext = context.getApplicationContext();
        mCallback = callback;
        mConstraintControllers = new ConstraintController[] {
                new NetworkConnectedController(appContext, taskExecutor)
        };
        mLock = new Object();
    }


    /**
     * Replaces the list of tracked {@link TransferSpec}s to monitor if their constraints are met.
     *
     * @param transferSpecs A list of {@link TransferSpec}s to monitor constraints for
     */
    @SuppressWarnings("unchecked")
    public void replace(@NonNull List<TransferSpec> transferSpecs) {
        synchronized (mLock) {
            for (ConstraintController controller : mConstraintControllers) {
                controller.setCallback(null);
            }

            for (ConstraintController controller : mConstraintControllers) {
                controller.replace(transferSpecs);
            }

            for (ConstraintController controller : mConstraintControllers) {
                controller.setCallback(this);
            }
        }
    }

    /**
     * Resets and clears all tracked {@link TransferSpec}s.
     */
    public void reset() {
        synchronized (mLock) {
            for (ConstraintController controller : mConstraintControllers) {
                controller.reset();
            }
        }
    }

    /**
     * Returns <code>true</code> if all the underlying constraints for a given WorkSpec are met.
     *
     * @param transferSpecId The {@link TransferSpec} id
     * @return <code>true</code> if all the underlying constraints for a given {@link TransferSpec} are
     * met.
     */
    public boolean areAllConstraintsMet(@NonNull String transferSpecId) {
        synchronized (mLock) {
            for (ConstraintController constraintController : mConstraintControllers) {
                if (constraintController.isWorkSpecConstrained(transferSpecId)) {
                    Logger.get().debug(TAG, String.format("Work %s constrained by %s", transferSpecId,
                            constraintController.getClass().getSimpleName()));
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void onConstraintMet(@NonNull List<String> transferSpecIds) {
        synchronized (mLock) {
            List<String> unconstrainedWorkSpecIds = new ArrayList<>();
            for (String workSpecId : transferSpecIds) {
                if (areAllConstraintsMet(workSpecId)) {
                    Logger.get().debug(TAG, String.format("Constraints met for %s", workSpecId));
                    unconstrainedWorkSpecIds.add(workSpecId);
                }
            }
            if (mCallback != null) {
                mCallback.onAllConstraintsMet(unconstrainedWorkSpecIds);
            }
        }
    }

    @Override
    public void onConstraintNotMet(@NonNull List<String> transferSpecIds) {
        synchronized (mLock) {
            if (mCallback != null) {
                mCallback.onAllConstraintsNotMet(transferSpecIds);
            }
        }
    }
}
