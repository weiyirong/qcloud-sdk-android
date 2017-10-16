package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.List;

final public class HeadObjectResult extends CosXmlResult {
    private String cosObjectType;
    private String cosStorageClass;

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
        List<String> list = getHeaders().get("x-cos-object-type");
        if(list != null){
            cosObjectType = list.get(0);
        }
        return cosObjectType;
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
        List<String> list = getHeaders().get("x-cos-storage-class");
        if(list != null){
            cosStorageClass = list.get(0);
        }
        return cosStorageClass;
    }

    @Override
    public String printHeaders() {
        return super.printHeaders()
                + getCOSObjectType() + "\n"
                + getCOSStorageClass();
    }
}
