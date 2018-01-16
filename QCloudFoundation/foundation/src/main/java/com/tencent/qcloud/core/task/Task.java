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
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/27.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class Task<T> implements Runnable {

    private final String identifier;
    private final Object tag;

    protected boolean executed;
    protected boolean completed;

    private boolean closed;

    protected T mResult;
    protected QCloudClientException clientException;
    protected QCloudServiceException serviceException;

    protected TaskManager taskManager;

    private Executor observerExecutor;
    protected ThreadPoolExecutor workerExecutor;

    protected Set<QCloudResultListener<T>> mResultListeners = new HashSet<>(2);
    protected Set<QCloudProgressListener> mProgressListeners = new HashSet<>(2);

    Task(String identifier, Object tag) {
        this.identifier = identifier;
        this.tag = tag;
        taskManager = TaskManager.getInstance();
    }

    protected void scheduleOn(ThreadPoolExecutor executor) {
        taskManager.add(this);
        workerExecutor = executor;
        TaskExecutors.schedule(executor, this);
    }

    public final Task<T> observeOn(Executor executor) {
        observerExecutor = executor;
        return this;
    }

    public final T executeNow() throws QCloudClientException, QCloudServiceException {
        executeNowSilently();

        if (clientException != null) {
            throw clientException;
        }
        if (serviceException != null) {
            throw  serviceException;
        }

        return mResult;
    }

    public final void executeNowSilently() {
        taskManager.add(this);
        run();
    }

    public final Task<T> addResultListener(QCloudResultListener<T> resultListener) {
        if (resultListener != null) {
            mResultListeners.add(resultListener);
        }
        return this;
    }

    public final Task<T> addResultListeners(List<QCloudResultListener<T>> resultListeners) {
        if (resultListeners != null) {
            mResultListeners.addAll(resultListeners);
        }
        return this;
    }

    public final Task<T> removeResultListener(QCloudResultListener<T> resultListener) {
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

    public final Task<T> addProgressListener(QCloudProgressListener progressListener) {
        if (progressListener != null) {
            mProgressListeners.add(progressListener);
        }
        return this;
    }

    public final Task<T> addProgressListeners(List<QCloudProgressListener> progressListeners) {
        if (progressListeners != null) {
            mProgressListeners.addAll(progressListeners);
        }
        return this;
    }

    public final Task<T> removeProgressListener(QCloudProgressListener progressListener) {
        if (progressListener != null) {
            mProgressListeners.remove(progressListener);
        }
        return this;
    }

    public T getResult() {
        return mResult;
    }

    public Exception getException() {
        return clientException != null ? clientException : serviceException;
    }

    protected void onSuccess() {
        if (mResultListeners.size() > 0) {
            executeListener(new Runnable() {
                @Override
                public void run() {
                    List<QCloudResultListener<T>> listeners = new ArrayList<>(mResultListeners);
                    for (QCloudResultListener<T> resultListener : listeners) {
                        resultListener.onSuccess(mResult);
                    }
                }
            });
        }
    }

    protected void onFailure() {
        if (mResultListeners.size() > 0) {
            executeListener(new Runnable() {
                @Override
                public void run() {
                    List<QCloudResultListener<T>> listeners = new ArrayList<>(mResultListeners);
                    for (QCloudResultListener resultListener : listeners) {
                        resultListener.onFailure(clientException, serviceException);
                    }
                }
            });
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

    protected synchronized void close() {
        if (!closed) {
            closed = true;
            taskManager.remove(this);
        }
    }

    protected synchronized void onExecute() {
        QCloudLogger.d(TaskManager.TASK_LOG_TAG, "[Task] %s start execute", getIdentifier());
        executed = true;
    }

    protected synchronized void onComplete() {
        QCloudLogger.d(TaskManager.TASK_LOG_TAG, "[Task] %s complete", getIdentifier());
        close();
        completed = true;
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final Object getTag() {
        return tag;
    }

    public final synchronized boolean isExecuted() {
        return executed;
    }

    public final synchronized boolean isCompleted() {
        return completed;
    }

    protected abstract T onCommand() throws QCloudClientException, QCloudServiceException;

    @Override
    public void run() {
        onExecute();

        try {
            mResult = onCommand();
            onSuccess();
        } catch (QCloudClientException e) {
            e.printStackTrace();
            clientException = e;
            onFailure();
        } catch (QCloudServiceException e) {
            e.printStackTrace();
            serviceException = e;
            onFailure();
        }

        onComplete();
    }
}
