package com.tencent.qcloud.core.task;

import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * Created by wjielai on 2017/11/27.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class TaskManager {

    private Map<String, Task> taskPool;

    private static volatile TaskManager instance;

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }

        return instance;
    }

    private TaskManager() {
        taskPool = new ConcurrentHashMap<>(30);
    }

    void add(Task task) {
        taskPool.put(task.getIdentifier(), task);
        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] ADD %s, %d cached", task.toString(), taskPool.size());
    }

    void remove(Task task) {
        if (taskPool.remove(task.getIdentifier()) != null) {
            QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] REMOVE %s, %d cached",
                    task.toString(), taskPool.size());
        }
    }

    public Task get(String identifier) {
        return taskPool.get(identifier);
    }

    public List<Task> snapshot() {
        return new ArrayList<>(taskPool.values());
    }

    void evict() {
        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Buffer] CLEAR %d", taskPool.size());
        taskPool.clear();
    }
}
