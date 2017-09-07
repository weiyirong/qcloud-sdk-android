package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.tag.Tag;
import com.tencent.cos.xml.model.tag.TagSet;
import com.tencent.cos.xml.model.tag.Tagging;
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
 * Put Bucket Tagging API is used to tag specified Bucket.
 * Tags are used to organize and manage relevant Buckets.
 * Status code 400 will be returned if the request sets different Values for the same Key.
 * Status code 204 will be returned if the request succeeds.
 */
public class PutBucketTaggingRequest extends CosXmlRequest {
    private Tagging tagging;
    public PutBucketTaggingRequest(){
        contentType = RequestContentType.APPLICATION_XML;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        tagging = new Tagging();
        tagging.tagSet = new TagSet();
        tagging.tagSet.tagList = new ArrayList<Tag>();
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

        requestBodySerializer = new RequestXmlBodySerializer(tagging);

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketTaggingResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("tagging",null);
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

    public void setTagList(List<Tag> tagList){
        if(tagList != null){
            tagging.tagSet.tagList.addAll(tagList);
        }
    }

    public Tagging getTagging() {
        return tagging;
    }

    public void setTagList(Tag tag){
        if(tag != null){
            tagging.tagSet.tagList.add(tag);
        }
    }
}
