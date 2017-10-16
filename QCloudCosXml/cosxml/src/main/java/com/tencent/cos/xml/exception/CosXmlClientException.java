package com.tencent.cos.xml.exception;

import com.tencent.qcloud.core.network.exception.QCloudClientException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by bradyxiao on 2017/9/26.
 */

public class CosXmlClientException extends QCloudClientException {
    public CosXmlClientException(String message, Throwable t) {
        super(message, t);
    }

    public CosXmlClientException(String message) {
        super(message);
    }

    public CosXmlClientException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String toString(){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        this.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}
