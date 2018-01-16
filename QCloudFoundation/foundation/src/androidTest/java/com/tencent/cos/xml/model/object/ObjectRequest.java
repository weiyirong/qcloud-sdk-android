package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public abstract class ObjectRequest extends CosXmlRequest {

    protected String bucket;
    protected String cosPath;

    public ObjectRequest(String bucket, String cosPath){
        this.bucket = bucket;
        this.cosPath = cosPath;
    }

    @Override
    public String getHostPrefix() {
        return bucket;
    }

    @Override
    public String getPath() {
        if(cosPath != null && !cosPath.startsWith("/")){
            return  "/" + cosPath;
        }else {
            return cosPath;
        }
    }

    @Override
    public void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null ");
        }
        if(cosPath == null){
            throw new CosXmlClientException("cosPath must not be null ");
        }
    }
}
