package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.tencent.cos.xml.model.tag.Rule;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.action.QCloudBodyMd5Action;
import com.tencent.cos.xml.model.RequestXmlBodySerializer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * COS 支持用户以生命周期配置的方式来管理 Bucket 中 Object 的生命周期。
 * </p>
 *
 * <p>
 * 生命周期配置包含一个或多个将应用于一组对象规则的规则集 (其中每个规则为 COS 定义一个操作)。
 * </p>
 *
 * 这些操作分为以下两种：
 * <ul>
 * <li>
 * 转换操作：定义对象转换为另一个存储类的时间。例如，您可以选择在对象创建 30 天后将其转换为 STANDARD_IA (IA，适用于不常访问) 存储类别。
 * </li>
 * <li>
 * 过期操作：指定 Object 的过期时间。COS 将会自动为用户删除过期的 Object。
 * </li>
 * </ul>
 *
 * <p>
 * PutBucketLifecycle 用于为 Bucket 创建一个新的生命周期配置。如果该 Bucket 已配置生命周期，使用该接口创建新的配置的同时则会覆盖原有的配置。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putBucketLifecycle(PutBucketLifecycleRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketLifecycleAsync(PutBucketLifecycleRequest, CosXmlResultListener)
 */
final public class PutBucketLifecycleRequest extends CosXmlRequest {


    private LifecycleConfiguration lifecycleConfiguration;

    public PutBucketLifecycleRequest(String bucket){
        setBucket(bucket);
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        lifecycleConfiguration = new LifecycleConfiguration();
        lifecycleConfiguration.ruleList = new ArrayList<>();
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

        requestOriginBuilder.body(new RequestXmlBodySerializer(lifecycleConfiguration));

        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketLifecycleResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("lifecycle",null);
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
     * 添加多条生命周期规则
     *
     * @param ruleList
     */
    public void setRuleList(List<Rule> ruleList){
        if(ruleList != null){
            this.lifecycleConfiguration.ruleList.addAll(ruleList);
        }
    }

    /**
     * 添加一条生命周期规则
     *
     * @param rule
     */
    public void setRuleList(Rule rule){
        if(rule != null){
            this.lifecycleConfiguration.ruleList.add(rule);
        }
    }

    /**
     * 获取添加的生命周期规则
     *
     * @return
     */
    public LifecycleConfiguration getLifecycleConfiguration() {
        return lifecycleConfiguration;
    }
}
