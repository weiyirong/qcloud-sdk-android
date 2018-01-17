package com.tencent.qcloud.core.cos.object;


import com.tencent.qcloud.core.cos.CosXmlResult;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class GetObjectResult extends CosXmlResult {
    private Map<String,List<String>> xCOSMeta;
    private String xCOSObjectType;
    private String xCOSStorageClass;
}
