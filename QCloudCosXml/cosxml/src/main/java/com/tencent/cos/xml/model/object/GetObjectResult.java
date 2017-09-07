package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/7.
 * author bradyxiao
 */
public class GetObjectResult extends CosXmlResult {
    private Map<String,List<String>> xCOSMeta;
    private String xCOSObjectType;
    private String xCOSStorageClass;

    public Map<String, List<String>> getCOSMeta() {
        return xCOSMeta;
    }

    public String getCOSStorageClass() {
        if (getHeaders() == null) {
            return "";
        }
        List<String> list = getHeaders().get("x-cos-storage-class");
        if(list != null){
            xCOSStorageClass = list.get(0);
        }
        return xCOSStorageClass;
    }

    public String getCOSObjectType() {
        if (getHeaders() == null) {
            return "";
        }
        List<String> list = getHeaders().get("x-cos-object-type");
        if(list != null){
            xCOSObjectType = list.get(0);
        }
        return xCOSObjectType;
    }

    @Override
    public String printHeaders() {
        return super.printHeaders() + "\n"
                + getCOSStorageClass() + "\n"
                + getCOSObjectType() + "\n";
    }
}
