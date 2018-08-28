package com.tencent.qcloud.core.task;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;

import static com.tencent.qcloud.core.task.TaskManager.TASK_LOG_TAG;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/27.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudTask<T> implements Callable<T> {

    private final String identifier;
    private final Object tag;

    private TaskManager taskManager;

    private Task<T> mTask;
    private CancellationTokenSource mCancellationTokenSource;

    private Executor observerExecutor;
    private Executor workerExecutor;

    private AtomicBoolean mIsExecuting = new AtomicBoolean(false);

    private Set<QCloudResultListener<T>> mResultListeners = new HashSet<>(2);
    private Set<QCloudProgressListener> mProgressListeners = new HashSet<>(2);

    public QCloudTask(String identifier, Object tag) {
        this.identifier = identifier;
        this.tag = tag;
        taskManager = TaskManager.getInstance();
    }

    public final Task<T> cast() {
        return mTask;
    }

    public final T executeNow() throws QCloudClientException, QCloudServiceException {
        executeNowSilently();

        Exception exception = getException();
        if (exception != null) {
            if (exception instanceof QCloudClientException) {
                throw (QCloudClientException) exception;
            } else if (exception instanceof QCloudServiceException) {
                throw (QCloudServiceException) exception;
            } else {
                throw new QCloudClientException(exception);
            }
        }

        return getResult();
    }

    public final void executeNowSilently() {
        taskManager.add(this);
        mTask = Task.call(this);
    }

    protected QCloudTask<T> scheduleOn(Executor executor) {
        return scheduleOn(executor, null);
    }

    protected QCloudTask<T> scheduleOn(Executor executor,
                                       CancellationTokenSource cancellationTokenSource) {
        taskManager.add(this);
        workerExecutor = executor;
        mCancellationTokenSource = cancellationTokenSource;

        mTask = Task.call(this, executor, mCancellationTokenSource != null ?
                mCancellationTokenSource.getToken() : null);
        mTask.continueWithTask(new Continuation<T, Task<Void>>() {
            @Override
            public Task<Void> then(Task<T> task) throws Exception {
                Executor callbackExc = observerExecutor != null ? observerExecutor : workerExecutor;
                if (task.isFaulted() || task.isCancelled()) {
                    return Task.call(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            onFailure();
                            return null;
                        }
                    }, callbackExc);
                } else {
                    return Task.call(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            onSuccess();
                            return null;
                        }
                    }, callbackExc);
                }

            }
        });
        return this;
    }

    public void cancel() {
        QCloudLogger.d(TASK_LOG_TAG, "[Call] %s cancel", this);
        if (mCancellationTokenSource != null) {
            mCancellationTokenSource.cancel();
        }
    }

    public final boolean isCanceled() {
        return mCancellationTokenSource != null && mCancellationTokenSource.isCancellationRequested();
    }

    public final boolean isExecuting() {
        return mTask != null && mIsExecuting.get();
    }

    public final boolean isCompleted() {
        return mTask != null && mTask.isCompleted();
    }

    @Override
    public T call() throws Exception {
        try {
            QCloudLogger.d(TaskManager.TASK_LOG_TAG, "[Task] %s start execute", getIdentifier());
            mIsExecuting.compareAndSet(false, true);
            return execute();
        } finally {
            QCloudLogger.d(TaskManager.TASK_LOG_TAG, "[Task] %s complete", getIdentifier());
            taskManager.remove(QCloudTask.this);
        }
    }

    protected abstract T execute() throws QCloudClientException, QCloudServiceException;

    public final QCloudTask<T> observeOn(Executor executor) {
        observerExecutor = executor;
        return this;
    }

    public final QCloudTask<T> addResultListener(QCloudResultListener<T> resultListener) {
        if (resultListener != null) {
            mResultListeners.add(resultListener);
        }
        return this;
    }

    public final QCloudTask<T> addResultListeners(List<QCloudResultListener<T>> resultListeners) {
        if (resultListeners != null) {
            mResultListeners.addAll(resultListeners);
        }
        return this;
    }

    public final QCloudTask<T> removeResultListener(QCloudResultListener<T> resultListener) {
        if (resultListener != null) {
            mResultListeners.remove(resultListener);
        }
        return this;
    }

    public final void removeAllListeners() {
        mResultListeners.clear();
        mProgressListeners.clear();
    }

    public final List<QCloudResultListener<T>> getAllResultListeners() {
        return new ArrayList<>(mResultListeners);
    }

    public final List<QCloudProgressListener> getAllProgressListeners() {
        return new ArrayList<>(mProgressListeners);
    }

    public final QCloudTask<T> addProgressListener(QCloudProgressListener progressListener) {
        if (progressListener != null) {
            mProgressListeners.add(progressListener);
        }
        return this;
    }

    public final QCloudTask<T> addProgressListeners(List<QCloudProgressListener> progressListeners) {
        if (progressListeners != null) {
            mProgressListeners.addAll(progressListeners);
        }
        return this;
    }

    public final QCloudTask<T> removeProgressListener(QCloudProgressListener progressListener) {
        if (progressListener != null) {
            mProgressListeners.remove(progressListener);
        }
        return this;
    }

    public T getResult() {
        return mTask.getResult();
    }

    public Exception getException() {
        return mTask.isFaulted() ? mTask.getError() : mTask.isCancelled() ?
                new QCloudClientException("canceled") : null;
    }

    protected void onSuccess() {
        if (mResultListeners.size() > 0) {
            List<QCloudResultListener<T>> listeners = new ArrayList<>(mResultListeners);
            for (QCloudResultListener<T> resultListener : listeners) {
                resultListener.onSuccess(getResult());
            }
        }
    }

    protected void onFailure() {
        Exception exception = getException();
        if (exception != null && mResultListeners.size() > 0) {
            List<QCloudResultListener<T>> listeners = new ArrayList<>(mResultListeners);
            for (QCloudResultListener resultListener : listeners) {
                if (exception instanceof QCloudClientException) {
                    resultListener.onFailure((QCloudClientException) exception, null);
                } else {
                    resultListener.onFailure(null, (QCloudServiceException) exception);
                }
            }
        }
    }

    protected void onProgress(final long complete, final long target) {
        if (mProgressListeners.size() > 0) {
            executeListener(new Runnable() {
                @Override
                public void run() {
                    List<QCloudProgressListener> listeners = new ArrayList<>(mProgressListeners);
                    for (QCloudProgressListener progressListener : listeners) {
                        progressListener.onProgress(complete, target);
                    }
                }
            });
        }
    }

    private void executeListener(Runnable callback) {
        if (observerExecutor != null) {
            observerExecutor.execute(callback);
        } else {
            callback.run();
        }
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final Object getTag() {
        return tag;
    }
}
