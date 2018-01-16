package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.qcloud.core.http.RequestBodySerializer;

/**
 * <p>
 * 删除一个Object
 * </p>
 *
 */
final public class DeleteObjectRequest extends ObjectRequest {

    public DeleteObjectRequest(String bucket, String cosPath){
        super(bucket, cosPath);
    }

    @Override
    public String getMethod() {
        return RequestMethod.DELETE;
    }

    @Override
    public RequestBodySerializer getRequestBody() {
        return null;
    }

}
