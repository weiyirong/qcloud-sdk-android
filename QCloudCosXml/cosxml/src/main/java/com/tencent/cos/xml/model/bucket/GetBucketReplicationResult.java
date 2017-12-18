package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ReplicationConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/11/6.
 */

public class GetBucketReplicationResult extends CosXmlResult {

    @XStreamAlias("ReplicationConfiguration")
    public ReplicationConfiguration replicationConfiguration;

    @Override
    public String printBody() {
        return replicationConfiguration != null ? replicationConfiguration.toString() : super.printBody();
    }
}
