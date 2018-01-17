package com.tencent.qcloud.core.cos;

import com.tencent.qcloud.core.auth.COSXmlSignSourceProvider;
import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;

public abstract class CosXmlRequest<T> {

    protected String bucket;

    private QCloudSignSourceProvider cosSignSourceProvider;

    public CosXmlRequest() {
        cosSignSourceProvider = new COSXmlSignSourceProvider(600);
    }

    public CosXmlRequest setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public QCloudSignSourceProvider getSignSourceProvider() {
        return cosSignSourceProvider;
    }
}
