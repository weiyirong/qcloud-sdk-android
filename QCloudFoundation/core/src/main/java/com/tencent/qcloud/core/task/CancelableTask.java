package com.tencent.qcloud.core.task;

import com.tencent.qcloud.core.logger.QCloudLogger;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/29.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class CancelableTask<T> extends Task<T> {

    private boolean canceled;

    public CancelableTask(String identifier, Object tag) {
        super(identifier, tag);
    }

    @Override
    public void run() {
        if (canceled) {
            return;
        }

        super.run();
    }

    @Override
    protected void onProgress(long complete, long target) {
        if (canceled) {
            return;
        }
        super.onProgress(complete, target);
    }

    @Override
    protected void onSuccess() {
        if (canceled) {
            return;
        }
        super.onSuccess();
    }

    @Override
    protected void onFailure() {
        if (canceled) {
            return;
        }
        super.onFailure();
    }

    protected abstract boolean onCancel();

    synchronized public boolean isCanceled() {
        return canceled;
    }

    synchronized public final void cancel() {
        if (canceled) {
            return;
        }

        if (onCancel()) {
            workerExecutor.remove(this);
            close();
            canceled = true;
            QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s cancel", this);
        }
    }
}
