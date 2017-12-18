package com.tencent.qcloud.core.task;

import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudProgressListener;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;

import java.util.HashSet;
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

    private boolean executed;
    private boolean completed;

    private boolean closed;

    protected T mResult;
    protected QCloudClientException clientException;
    protected QCloudServiceException serviceException;

    protected TaskManager taskManager;

    private Executor observerExecutor;
    protected ThreadPoolExecutor workerExecutor;

    protected Set<QCloudResultListener<T>> mResultListeners = new HashSet<>(5);
    protected Set<QCloudProgressListener> mProgressListeners = new HashSet<>(5);

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

    public final Task observeOn(Executor executor) {
        observerExecutor = executor;
        return this;
    }

    public final T executeNow() throws QCloudClientException, QCloudServiceException {
        taskManager.add(this);
        run();

        if (clientException != null) {
            throw clientException;
        }
        if (serviceException != null) {
            throw  serviceException;
        }

        return mResult;
    }

    public final Task addResultListener(QCloudResultListener<T> resultListener) {
        mResultListeners.add(resultListener);
        return this;
    }

    public final Task addProgressListener(QCloudProgressListener progressListener) {
        mProgressListeners.add(progressListener);
        return this;
    }

    public T getResult() {
        return mResult;
    }

    public Exception getException() {
        return clientException != null ? clientException : serviceException;
    }

    protected void onSuccess() {
        executeListener(new Runnable() {
            @Override
            public void run() {
                for (QCloudResultListener<T> resultListener : mResultListeners) {
                    resultListener.onSuccess(mResult);
                }
            }
        });
    }

    protected void onFailure() {
        executeListener(new Runnable() {
            @Override
            public void run() {
                for (QCloudResultListener resultListener : mResultListeners) {
                    resultListener.onFailure(clientException, serviceException);
                }
            }
        });
    }

    protected void onProgress(final long complete, final long target) {
        executeListener(new Runnable() {
            @Override
            public void run() {
                for (QCloudProgressListener progressListener : mProgressListeners) {
                    progressListener.onProgress(complete, target);
                }
            }
        });
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
        executed = true;
    }

    protected synchronized void onComplete() {
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
