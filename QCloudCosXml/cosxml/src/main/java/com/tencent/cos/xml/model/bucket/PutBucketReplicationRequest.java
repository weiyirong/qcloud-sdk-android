package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.RequestXmlBodySerializer;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.ReplicationConfiguration;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.action.QCloudBodyMd5Action;

import java.util.Map;

/**
 * Created by bradyxiao on 2017/11/6.
 * <p>
 *     用于向开启版本管理的存储桶添加 replication跨域复制配置。
 *     如果存储桶已经拥有 replication 配置，那么该请求会替换现有配置。
 *     使用该接口存储桶，包括目标存储桶，必须已经开启版本管理，版本管理详细请参见
 *     {@link PutBucketVersioningRequest}.
 *     目标存储桶不能与其在同一个区域。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#putBucketReplication(PutBucketReplicationRequest)
 * @see com.tencent.cos.xml.CosXml#putBucketReplicationAsync(PutBucketReplicationRequest, CosXmlResultListener)
 */

public class PutBucketReplicationRequest extends CosXmlRequest {

    private ReplicationConfiguration replicationConfiguration;

    public PutBucketReplicationRequest(String bucket){
        setBucket(bucket);
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        replicationConfiguration = new ReplicationConfiguration();
    }

    @Override
    protected void build() throws CosXmlClientException{
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
        requestOriginBuilder.body(new RequestXmlBodySerializer(replicationConfiguration));
        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketReplicationResult.class);
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.PUT;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("replication", null);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }

    /**设置 replication的发起者身份标示*/
    public void setReplicationConfigurationWithRole(String ownerUin, String subUin){
        if(ownerUin != null && subUin != null){
            String role = "qcs::cam::uin/" + ownerUin + ":uin/" + subUin;
            replicationConfiguration.role = role;
        }
    }

    /**	具体配置信息，最多支持 1000 个，所有策略只能指向一个目标存储桶*/
    public void setReplicationConfigurationWithRule(RuleStruct ruleStruct){
        if(ruleStruct != null){
            ReplicationConfiguration.Rule rule = new ReplicationConfiguration.Rule();
            rule.id = ruleStruct.id;
            rule.status = ruleStruct.isEnable?"Enabled":"Disabled";
            rule.prefix = ruleStruct.prefix;
            ReplicationConfiguration.Destination destination = new ReplicationConfiguration.Destination();
            destination.storageClass = ruleStruct.storageClass;
            StringBuilder bucket = new StringBuilder();
            bucket.append("qcs:id/0:cos:").append(ruleStruct.region).append(":appid/")
                    .append(ruleStruct.appid).append(":").append(ruleStruct.bucket);
            destination.bucket = bucket.toString();
            rule.destination = destination;
            replicationConfiguration.rule = rule;
        }
    }

    /**
     rule的结构体
     */
    public static class RuleStruct{
        public String appid;
        public String region;
        public String bucket;
        public String storageClass;
        public String id;
        public String prefix;
        public boolean isEnable;
    }

}
