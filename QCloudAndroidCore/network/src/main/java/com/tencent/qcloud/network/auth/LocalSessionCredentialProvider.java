package com.tencent.qcloud.network.auth;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.HexUtils;
import com.tencent.qcloud.network.tools.QCloudEncryptTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class LocalSessionCredentialProvider extends SmartSessionCredentialProvider {

    private Logger logger = LoggerFactory.getLogger(LocalSessionCredentialProvider.class);

    public LocalSessionCredentialProvider(SessionCredential sessionCredential) {

        this.sessionCredential = sessionCredential;
    }

    @Override
    protected SessionCredential sessionCredential() throws QCloudException {

        return sessionCredential;
    }

}
