package com.tencent.cos.xml.exception;

import com.tencent.qcloud.core.common.QCloudClientException;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public class CosXmlClientException extends QCloudClientException {

    public CosXmlClientException(String message){
        super(message);
    }

    public CosXmlClientException(String message, Throwable cause){
        super(message, cause);
    }

    public CosXmlClientException(Throwable cause){
        super(cause);
    }
}
