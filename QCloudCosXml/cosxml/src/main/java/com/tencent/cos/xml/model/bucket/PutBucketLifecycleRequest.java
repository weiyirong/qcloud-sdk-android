package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.tencent.cos.xml.model.tag.Rule;
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
 * Put Bucket Lifecycle request is used to configure life cycle management.
 * You can use this request to implement lifecycle management and periodic deletion for data.
 * This operation will overwrite previous configuration files with newly uploaded files.
 * Life cycle management is effective to both files and folders.
 * (Currently this is only supported for South China region)
 */
public class PutBucketLifecycleRequest extends CosXmlRequest {
    private LifecycleConfiguration lifecycleConfiguration;
    public PutBucketLifecycleRequest(){
        contentType = RequestContentType.APPLICATION_XML;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        lifecycleConfiguration = new LifecycleConfiguration();
        lifecycleConfiguration.ruleList = new ArrayList<Rule>();
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

        requestBodySerializer = new RequestXmlBodySerializer(lifecycleConfiguration);

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketLifecycleResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("lifecycle",null);
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

    public void setRuleList(List<Rule> ruleList){
        if(ruleList != null){
            this.lifecycleConfiguration.ruleList.addAll(ruleList);
        }
    }

    public void setRuleList(Rule rule){
        if(rule != null){
            this.lifecycleConfiguration.ruleList.add(rule);
        }
    }

    public LifecycleConfiguration getLifecycleConfiguration() {
        return lifecycleConfiguration;
    }
}
