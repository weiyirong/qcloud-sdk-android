package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 */
public class HeadObjectResult extends CosXmlResult {
    private String xCOSObjectType;
    private String xCOSStorageClass;

    public String getCOSObjectType() {
        List<String> list = getHeaders().get("x-cos-object-type");
        if(list != null){
            xCOSObjectType = list.get(0);
        }
        return xCOSObjectType;
    }

    public String getCOSStorageClass() {
        List<String> list = getHeaders().get("x-cos-storage-class");
        if(list != null){
            xCOSStorageClass = list.get(0);
        }
        return xCOSStorageClass;
    }

    @Override
    public String printHeaders() {
        return super.printHeaders()
                + getCOSObjectType() + "\n"
                + getCOSStorageClass();
    }
}
