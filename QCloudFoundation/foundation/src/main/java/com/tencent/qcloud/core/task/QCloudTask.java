package com.tencent.qcloud.core.task;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.common.QCloudTaskStateListener;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

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

    // 任务正在排队
    public static final int STATE_QUEUEING = 1;
    // 任务正在执行
    public static final int STATE_EXECUTING = 2;
    // 任务执行结束
    public static final int STATE_COMPLETE = 3;

    private final String identifier;
    private final Object tag;

    private TaskManager taskManager;

    private Task<T> mTask;
    private CancellationTokenSource mCancellationTokenSource;
    private int mState;

    private Executor observerExecutor;
    private Executor workerExecutor;

    private Set<QCloudResultListener<T>> mResultListeners = new HashSet<>(2);
    private Set<QCloudProgressListener> mProgressListeners = new HashSet<>(2);
    private Set<QCloudTaskStateListener> mStateListeners = new HashSet<>(2);

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
        onStateChanged(STATE_QUEUEING);
        mTask = Task.call(this);
    }

    protected QCloudTask<T> scheduleOn(Executor executor) {
        return scheduleOn(executor, null);
    }

    protected QCloudTask<T> scheduleOn(Executor executor,
                                       CancellationTokenSource cancellationTokenSource) {
        taskManager.add(this);
        onStateChanged(STATE_QUEUEING);
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

    /**
     * 任务是否已经取消
     *
     * @return true 表示任务已取消
     */
    public final boolean isCanceled() {
        return mCancellationTokenSource != null && mCancellationTokenSource.isCancellationRequested();
    }

    /**
     * 任务是否正在执行
     *
     * @return true 表示正在执行
     */
    public final boolean isExecuting() {
        return getState() == STATE_EXECUTING;
    }

    /**
     * 任务是否执行完成
     *
     * @return true 表示完成
     */
    public final boolean isCompleted() {
        return getState() == STATE_COMPLETE;
    }

    /**
     * 获取任务执行状态
     *
     * @return 任务状态，，有以下几种状态：
     * {@link QCloudTask#STATE_QUEUEING};
     * {@link QCloudTask#STATE_EXECUTING};
     * {@link QCloudTask#STATE_COMPLETE}
     */
    public final synchronized int getState() {
        return mState;
    }

    @Override
    public T call() throws Exception {
        try {
            QCloudLogger.d(TaskManager.TASK_LOG_TAG, "[Task] %s start execute", getIdentifier());
            onStateChanged(STATE_EXECUTING);
            return execute();
        } finally {
            QCloudLogger.d(TaskManager.TASK_LOG_TAG, "[Task] %s complete", getIdentifier());
            onStateChanged(STATE_COMPLETE);
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

    public final List<QCloudTaskStateListener> getAllStateListeners() {
        return new ArrayList<>(mStateListeners);
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

    public final QCloudTask<T> addStateListener(QCloudTaskStateListener stateListener) {
        if (stateListener != null) {
            mStateListeners.add(stateListener);
        }
        return this;
    }

    public final QCloudTask<T> addStateListeners(List<QCloudTaskStateListener> stateListeners) {
        if (stateListeners != null) {
            mStateListeners.addAll(stateListeners);
        }
        return this;
    }

    public final QCloudTask<T> removeStateListener(QCloudTaskStateListener stateListener) {
        if (stateListener != null) {
            mStateListeners.remove(stateListener);
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

    protected void onStateChanged(int newState) {
        setState(newState);
        if (mStateListeners.size() > 0) {
            executeListener(new Runnable() {
                @Override
                public void run() {
                    List<QCloudTaskStateListener> listeners = new ArrayList<>(mStateListeners);
                    for (QCloudTaskStateListener listener : listeners) {
                        listener.onStateChanged(identifier, mState);
                    }
                }
            });
        }
    }

    private synchronized void setState(int newState) {
        mState = newState;
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
