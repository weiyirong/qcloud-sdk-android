package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.CORSRule;
import com.tencent.qcloud.network.action.QCloudBodyMd5Action;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.serializer.body.RequestXmlBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 * Put Bucket CORS is used to set up cross-domain access configurations.
 * You can do so by importing configuration files of XML format (file size limit: 64 KB).
 */
public class PutBucketCORSRequest extends CosXmlRequest {
    private CORSConfiguration corsConfiguration;
    public PutBucketCORSRequest(){
        contentType = RequestContentType.APPLICATION_XML;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        corsConfiguration = new CORSConfiguration();
        corsConfiguration.corsRules = new ArrayList<CORSRule>();
    }

    @Override
    public void build() {
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Map.Entry<String,String> entry : requestQueryParams.entrySet())
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
        }

        if(requestHeaders.size() > 0){
            for(Map.Entry<String,String> entry : requestHeaders.entrySet())
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
        }

        requestActions.add(new QCloudBodyMd5Action(this));

        requestBodySerializer = new RequestXmlBodySerializer(corsConfiguration);

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketCORSResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("cors",null);
    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.PUT;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    public void setCORSRuleList(List<CORSRule> corsRuleList) {
        if(corsRuleList != null){
            this.corsConfiguration.corsRules.addAll(corsRuleList);
        }
    }
    public void setCORSRuleList(CORSRule corsRule) {
        if(corsRule != null){
            this.corsConfiguration.corsRules.add(corsRule);
        }
    }

    public CORSConfiguration getCorsConfiguration() {
        return corsConfiguration;
    }
}
