package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.VersioningConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/11/6.
 */

public class GetBucketVersioningResult extends CosXmlResult {

    @XStreamAlias("VersioningConfiguration")
    public VersioningConfiguration versioningConfiguration;

    @Override
    public String printBody() {
        return versioningConfiguration != null ? versioningConfiguration.toString() : super.printBody();
    }
}
