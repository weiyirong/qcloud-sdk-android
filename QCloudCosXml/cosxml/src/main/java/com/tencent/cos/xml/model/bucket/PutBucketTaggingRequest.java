package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.Tag;
import com.tencent.cos.xml.model.tag.TagSet;
import com.tencent.cos.xml.model.tag.Tagging;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.action.QCloudBodyMd5Action;
import com.tencent.cos.xml.model.RequestXmlBodySerializer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设置Bucket的tag信息
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putBucketTagging(PutBucketTaggingRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketTaggingAsync(PutBucketTaggingRequest, CosXmlResultListener)
 */
final public class PutBucketTaggingRequest extends CosXmlRequest {

    private Tagging tagging;

    public PutBucketTaggingRequest(String bucket){
        setBucket(bucket);
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        tagging = new Tagging();
        tagging.tagSet = new TagSet();
        tagging.tagSet.tagList = new ArrayList<>();
    }

    @Override
    protected void build() throws CosXmlClientException {
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Object object : requestQueryParams.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
            }
        }

        if(requestHeaders.size() > 0){
            for(Object object : requestHeaders.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
            }
        }

        requestActions.add(new QCloudBodyMd5Action());

        requestOriginBuilder.body(new RequestXmlBodySerializer(tagging));


        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketTaggingResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("tagging",null);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    /**
     * 增加多个Tag
     *
     * @param tags
     */
    public void addTags(List<Tag> tags){
        if(tags != null){
            tagging.tagSet.tagList.addAll(tags);
        }
    }

    /**
     * 获取所有设置的Tag信息。
     *
     * @return
     */
    public Tagging getTagging() {
        return tagging;
    }

    /**
     * 增加单个Tag
     *
     * @param tag
     */
    public void addTag(Tag tag){
        if(tag != null){
            tagging.tagSet.tagList.add(tag);
        }
    }
}
