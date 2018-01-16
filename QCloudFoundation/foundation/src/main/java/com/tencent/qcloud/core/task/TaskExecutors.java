package com.tencent.qcloud.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/29.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class TaskExecutors {

    public static final ThreadPoolExecutor COMMAND_EXECUTOR;

    public static final ThreadPoolExecutor UPLOAD_EXECUTOR;

    public static final ThreadPoolExecutor DOWNLOAD_EXECUTOR;

    public static final UIThreadExecutor UI_THREAD_EXECUTOR;

    static {
        COMMAND_EXECUTOR = new ThreadPoolExecutor(5, 5, 5L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128),
                new TaskThreadFactory("Command-"));
        UPLOAD_EXECUTOR = new ThreadPoolExecutor(2, 2, 5L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128),
                new TaskThreadFactory("Upload-"));
        DOWNLOAD_EXECUTOR = new ThreadPoolExecutor(3, 3, 5L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128),
                new TaskThreadFactory("Download-"));
        UI_THREAD_EXECUTOR = new UIThreadExecutor();

        UPLOAD_EXECUTOR.allowCoreThreadTimeOut(true);
        COMMAND_EXECUTOR.allowCoreThreadTimeOut(true);
        DOWNLOAD_EXECUTOR.allowCoreThreadTimeOut(true);
    }

    static void schedule(Executor executor, Task task) {
        executor.execute(task);
    }

    static final class TaskThreadFactory implements ThreadFactory {
        private final AtomicInteger increment = new AtomicInteger(1);
        private final String tag;

        TaskThreadFactory(String tag) {
            this.tag = tag;
        }

        @Override
        public final Thread newThread(Runnable runnable) {
            Thread newThread = new Thread(runnable, "QCloud-" + tag + increment.getAndIncrement());
            newThread.setDaemon(false);
            newThread.setPriority(9);
            return newThread;
        }
    }
}
