package com.tencent.qcloud.network.cosv4;

import com.tencent.qcloud.network.auth.QCloudSignatureSourceEncryptor;
import com.tencent.qcloud.network.exception.QCloudException;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class CosV4SignatureSourceEncryptor implements QCloudSignatureSourceEncryptor {

    public abstract String encrypt(String source) throws QCloudException;
}
