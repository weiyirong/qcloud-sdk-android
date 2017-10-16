package com.tencent.cos.xml.exception;

import com.tencent.qcloud.core.network.exception.QCloudClientException;

/**
 * Created by bradyxiao on 2017/9/26.
 */

public class QCloudException extends QCloudClientException {
    public QCloudException(String message, Throwable t) {
        super(message, t);
    }

    public QCloudException(String message) {
        super(message);
    }

    public QCloudException(Throwable throwable) {
        super(throwable);
    }

}
