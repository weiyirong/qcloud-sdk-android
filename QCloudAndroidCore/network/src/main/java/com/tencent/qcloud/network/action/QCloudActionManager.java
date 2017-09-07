package com.tencent.qcloud.network.action;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudActionManager {

    private Logger logger = LoggerFactory.getLogger(QCloudActionManager.class);

    private ExecutorService actionExecutor;


    public QCloudActionManager(int maxActionConcurrent) {

        actionExecutor = Executors.newFixedThreadPool(maxActionConcurrent);

    }

    public void execute(QCloudRequestAction action) {

        QCloudLogger.debug(logger, "QCloudActionManager execute");

        Future future = null;
        try {
            future = actionExecutor.submit(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
        action.setFuture(future);
    }

    public void cancel(QCloudRequestAction action) {

        if (action.getFuture()!=null) {
            action.getFuture().cancel(true);
            if (action.getActionResultListener()!=null) { // future的取消异常接锅
                action.getActionResultListener().onFailed(action, new QCloudException(QCloudExceptionType.REQUEST_USER_CANCELLED));
            }
        }
    }

    public void release() {

        if (actionExecutor != null) {
            actionExecutor.shutdownNow();
        }

    }
}
