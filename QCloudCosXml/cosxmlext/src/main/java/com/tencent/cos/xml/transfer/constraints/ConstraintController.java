package com.tencent.cos.xml.transfer.constraints;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.impl.constraints.ConstraintListener;
import androidx.work.impl.constraints.trackers.ConstraintTracker;
import androidx.work.impl.model.WorkSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickenwang on 2019-11-27.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public abstract class ConstraintController<T> implements ConstraintListener<T> {


    /**
     * A callback for when a constraint changes.
     */
    public interface OnConstraintUpdatedCallback {

        /**
         * Called when a constraint is met.
         *
         * @param workSpecIds A list of {@link WorkSpec} IDs that may have become eligible to run
         */
        void onConstraintMet(@NonNull List<String> workSpecIds);

        /**
         * Called when a constraint is not met.
         *
         * @param workSpecIds A list of {@link WorkSpec} IDs that have become ineligible to run
         */
        void onConstraintNotMet(@NonNull List<String> workSpecIds);
    }

    private final List<String> mMatchingWorkSpecIds = new ArrayList<>();

    private T mCurrentValue;
    private ConstraintTracker<T> mTracker;
    private OnConstraintUpdatedCallback mCallback;

    ConstraintController(ConstraintTracker<T> tracker) {
        mTracker = tracker;
    }

    /**
     * Sets the callback to inform when constraints change.  This callback is also triggered the
     * first time it is set.
     *
     * @param callback The callback to inform about constraint met/unmet states
     */
    public void setCallback(OnConstraintUpdatedCallback callback) {
        if (mCallback != callback) {
            mCallback = callback;
            updateCallback();
        }
    }

    abstract boolean hasConstraint(@NonNull WorkSpec workSpec);

    abstract boolean isConstrained(@NonNull T currentValue);


    /**
     * Replaces the list of {@link WorkSpec}s to monitor constraints for.
     *
     * @param workSpecs A list of {@link WorkSpec}s to monitor constraints for
     */

    public void replace(@NonNull List<WorkSpec> workSpecs) {
        mMatchingWorkSpecIds.clear();

        for (WorkSpec workSpec : workSpecs) {
            if (hasConstraint(workSpec)) {
                mMatchingWorkSpecIds.add(workSpec.id);
            }
        }

        if (mMatchingWorkSpecIds.isEmpty()) {
            mTracker.removeListener(this);
        } else {
            mTracker.addListener(this);
        }
        updateCallback();
    }

    /**
     * Clears all tracked {@link WorkSpec}s.
     */
    public void reset() {
        if (!mMatchingWorkSpecIds.isEmpty()) {
            mMatchingWorkSpecIds.clear();
            mTracker.removeListener(this);
        }
    }

    /**
     * Determines if a particular {@link WorkSpec} is constrained. It is constrained if it is
     * tracked by this controller, and the controller constraint was set, but not satisfied.
     *
     * @param workSpecId The ID of the {@link WorkSpec} to check if it is constrained.
     * @return {@code true} if the {@link WorkSpec} is considered constrained
     */
    public boolean isWorkSpecConstrained(@NonNull String workSpecId) {
        return mCurrentValue != null && isConstrained(mCurrentValue)
                && mMatchingWorkSpecIds.contains(workSpecId);
    }

    private void updateCallback() {
        if (mMatchingWorkSpecIds.isEmpty() || mCallback == null) {
            return;
        }

        if (mCurrentValue == null || isConstrained(mCurrentValue)) {
            mCallback.onConstraintNotMet(mMatchingWorkSpecIds);
        } else {
            mCallback.onConstraintMet(mMatchingWorkSpecIds);
        }
    }

    @Override
    public void onConstraintChanged(@Nullable T newValue) {
        mCurrentValue = newValue;
        updateCallback();
    }

}
