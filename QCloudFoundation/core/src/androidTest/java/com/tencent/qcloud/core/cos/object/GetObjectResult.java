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

    /**
     * 获取用户自定义的meta信息
     *
     * @return
     */
    public Map<String, List<String>> getCOSMeta() {
        return xCOSMeta;
    }

    /**
     * <p>
     * 获取下载对象的存储类型
     * </p>
     * <p>
     * 枚举值：STANDARD,STANDARD_IA, NEARLINE
     * </p>
     *
     * @return
     */
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

    /**
     * <p>
     * object 是否可以被追加上传，
     * </p>
     * <p>
     * 枚举值：normal 或者 appendable
     * </p>
     * @return
     */
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
