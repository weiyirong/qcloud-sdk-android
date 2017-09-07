package com.tencent.qcloud.network.auth;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.exception.QCloudException;

import java.util.Map;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class QCloudCredentialProvider {

    protected QCloudSignatureSourceSerializer sourceSerializer;

    protected QCloudSignatureSourceEncryptor sourceEncryptor;

    public QCloudCredentialProvider() {

    }

    public QCloudSignatureSourceEncryptor getSourceEncryptor() {
        return sourceEncryptor;
    }

    public QCloudSignatureSourceSerializer getSourceSerializer() {
        return sourceSerializer;
    }

    public void setSourceEncryptor(QCloudSignatureSourceEncryptor sourceEncryptor) {
        this.sourceEncryptor = sourceEncryptor;
    }

    public void setSourceSerializer(QCloudSignatureSourceSerializer sourceSerializer) {
        this.sourceSerializer = sourceSerializer;
    }

    public abstract String signature(QCloudHttpRequest httpRequest) throws QCloudException;

}
