package com.tencent.cos.xml.model.bucket;


import android.text.TextUtils;

import com.tencent.cos.xml.CosXmlServiceConfig;
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
    public String getPath(CosXmlServiceConfig config) {

        StringBuilder path = new StringBuilder();
        String appid = config.getAppid();
        String fullBucketName = bucket;

        if (config.isBucketInPath()) {

            if(!fullBucketName.endsWith("-" + appid) && !TextUtils.isEmpty(appid)){
                fullBucketName = fullBucketName + "-" + appid;
            }
            path.append("/");
            path.append(fullBucketName);
        }

        return path + "/";
    }

    @Override
    public String getHostPrefix() {
        return bucket;
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        if(requestURL != null){
            return;
        }
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }
}
