package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
final public class GetObjectResult extends CosXmlResult {
    private Map<String,List<String>> xCOSMeta;
    private String cosObjectType;
    private String cosStorageClass;

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
            cosStorageClass = list.get(0);
        }
        return cosStorageClass;
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
            cosObjectType = list.get(0);
        }
        return cosObjectType;
    }

    @Override
    public String printHeaders() {
        return super.printHeaders() + "\n"
                + getCOSStorageClass() + "\n"
                + getCOSObjectType() + "\n";
    }
}
