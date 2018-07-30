package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.http.RequestBodySerializer;

/**
 * <p>
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

final public class GetObjectBytesRequest extends ObjectRequest {


    public GetObjectBytesRequest(String bucket, String cosPath) {
        super(bucket, cosPath);
    }

    @Override
    public String getMethod() {

        return RequestMethod.GET;
    }

    @Override
    public RequestBodySerializer getRequestBody() throws CosXmlClientException {
        return null;
    }
}
