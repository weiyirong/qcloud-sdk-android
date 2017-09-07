package com.tencent.cos.xml.sign;

import com.tencent.qcloud.network.auth.BasicLocalCredentialProvider;
import com.tencent.qcloud.network.auth.LocalSessionCredentialProvider;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.tools.HexUtils;
import com.tencent.qcloud.network.tools.QCloudEncryptTools;



/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@Deprecated
public class CosXmlLocalCredentialProvider extends BasicLocalCredentialProvider {

    public CosXmlLocalCredentialProvider(String secretId, String secretKey, long keyDuration) {
        super(secretId, secretKey, keyDuration);

    }

}
