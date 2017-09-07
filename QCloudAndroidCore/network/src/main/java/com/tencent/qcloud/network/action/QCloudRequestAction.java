package com.tencent.qcloud.network.action;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 专为序列化HTTP请求所需要的长时间操作定义的QCloudAction类。
 *
 * 如：计算签名、计算文件SHA1值
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudRequestAction implements Callable {

    private Logger logger = LoggerFactory.getLogger(QCloudRequestAction.class);

    private Future future;

    private QCloudActionResultListener actionResultListener;

    protected QCloudHttpRequest httpRequest;


    public QCloudRequestAction(QCloudHttpRequest httpRequest) {

        this.httpRequest = httpRequest;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public void setActionResultListener(QCloudActionResultListener actionResultListener) {
        this.actionResultListener = actionResultListener;
    }

    public QCloudActionResultListener getActionResultListener() {
        return actionResultListener;
    }

    abstract public void execute() throws Exception;

    public void cancel() {

        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public Object call() throws Exception{

        try {
            execute();
        } catch (Exception e) {

            e.printStackTrace();
            if (actionResultListener != null) {
                actionResultListener.onFailed(QCloudRequestAction.this, new QCloudException(e));
                return null;
            }
        }

        if (actionResultListener != null) {
            QCloudLogger.debug(logger, "one block mission success");
            try {
                actionResultListener.onSuccess(QCloudRequestAction.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
