package com.tencent.qcloud.network.exception;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QCloudException extends Exception {


    private QCloudExceptionType exceptionType;

    private String detailMessage;


    @Deprecated
    public QCloudException() {}


    /**
     * 适用于SDK自定义的异常
     *
     * @param exceptionType 基本类型
     */
    public QCloudException(QCloudExceptionType exceptionType, String detailMessage) {

        this.exceptionType = exceptionType;
        this.detailMessage = detailMessage;
    }

    public QCloudException(QCloudExceptionType exceptionType) {

        this.exceptionType = exceptionType;
    }


    /**
     * 适用用于捕获异常后，转化为QCloud异常再次抛出
     *
     * @param cause 原始异常
     */
    @Deprecated
    public QCloudException(Throwable cause) {

        super(cause);
    }

    /**
     *
     * 适用用于捕获异常后，转化为QCloud异常再次抛出
     *
     * @param message 异常详细信息
     * @param cause 原始异常
     */
    @Deprecated
    public QCloudException(String message, Throwable cause) {
        super(message, cause);
    }

    public QCloudExceptionType getExceptionType() {
        return exceptionType;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public String toString() {

        return exceptionType.getMessage() + " : " + detailMessage;
    }
}
