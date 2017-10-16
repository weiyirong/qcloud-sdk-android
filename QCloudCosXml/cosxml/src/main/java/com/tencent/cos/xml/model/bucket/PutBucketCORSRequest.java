package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.exception.CosXmlClientException;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.CORSRule;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.action.QCloudBodyMd5Action;
import com.tencent.qcloud.core.network.request.serializer.RequestXmlBodySerializer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设置 Bucket 的跨域资源共享权限。
 * </p>
 *
 * <p>
 * 使用 PutBucketCORS 接口创建的规则权限是覆盖当前的所有规则而不是新增一条权限规则。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putBucketCORS(PutBucketCORSRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketCORSAsync(PutBucketCORSRequest, CosXmlResultListener)
 */
final public class PutBucketCORSRequest extends CosXmlRequest {

    private CORSConfiguration corsConfiguration;

    public PutBucketCORSRequest(String bucket) {

        this.bucket = bucket;
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        corsConfiguration = new CORSConfiguration();
        corsConfiguration.corsRules = new ArrayList<>();
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

        requestOriginBuilder.body(new RequestXmlBodySerializer(corsConfiguration));


        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketCORSResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("cors",null);
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
     * 添加多条CORS
     *
     * @param corsRules
     */
    public void addCORSRules(List<CORSRule> corsRules) {
        if(corsRules != null){
            this.corsConfiguration.corsRules.addAll(corsRules);
        }
    }

    /**
     * 添加一条CORSRule
     *
     * @param corsRule
     */
    public void addCORSRule(CORSRule corsRule) {
        if(corsRule != null){
            this.corsConfiguration.corsRules.add(corsRule);
        }
    }

    /**
     * 获取添加的CORSRule列表
     *
     * @return
     */
    public CORSConfiguration getCorsConfiguration() {
        return corsConfiguration;
    }
}
