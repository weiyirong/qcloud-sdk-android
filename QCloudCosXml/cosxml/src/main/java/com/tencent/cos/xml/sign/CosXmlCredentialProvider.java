package com.tencent.cos.xml.sign;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.auth.BasicCredentialProvider;
import com.tencent.qcloud.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Request;

/**
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 *
 * <a herf https://www.qcloud.com/document/product/436/7778></a>
 */

public abstract class CosXmlCredentialProvider extends BasicCredentialProvider {


    /**
     * construction method for CosXmlCredentialProvider
     * @param secretId, <a herf https://www.qcloud.com/document/product/436/6225></a>
     */
    public CosXmlCredentialProvider(String secretId) {

        super(secretId);
    }

}
