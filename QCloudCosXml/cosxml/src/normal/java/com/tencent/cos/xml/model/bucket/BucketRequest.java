package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public abstract class BucketRequest extends CosXmlRequest {

    protected String bucket;

    public BucketRequest(String bucket){
        this.bucket = bucket;
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public String getHostPrefix() {
        return bucket;
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }
}
