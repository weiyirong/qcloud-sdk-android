package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.common.ClientErrorCode;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PutBucketInventoryRequest extends BucketRequest {

    private String inventoryId = "None";
    private static Pattern pattern = Pattern.compile("[^a-zA-Z0-9-_.]+");

    public PutBucketInventoryRequest(String bucket) {
        super(bucket);
    }

    public PutBucketInventoryRequest() {
        super(null);
    }

    public void setInventoryId(String inventoryId) throws CosXmlClientException{
        Matcher matcher = pattern.matcher(inventoryId);
        if(matcher.find()){
            throw new CosXmlClientException(ClientErrorCode.INVALID_ARGUMENT.getCode(),
                    "inventoryId must be in [a-zA-Z0-9-_.]");
        }
        this.inventoryId = inventoryId;
    }

    @Override
    public String getMethod() {
        return RequestMethod.PUT;
    }

    @Override
    public void setQueryParameters(Map<String, String> queryParameters) {
        queryParameters.put("inventory", null);
        queryParameters.put("id", inventoryId);
        super.setQueryParameters(queryParameters);
    }

    @Override
    public RequestBodySerializer getRequestBody() throws CosXmlClientException {
        return null;
    }
}
