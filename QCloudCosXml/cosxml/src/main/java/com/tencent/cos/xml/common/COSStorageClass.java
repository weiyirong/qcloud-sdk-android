package com.tencent.cos.xml.common;

/**
 *
 * COS存储类型
 *
 */
public enum COSStorageClass {

    /** 标准存储 */
    STANDARD("Standard"),

    /** 冷存储 */
    STANDARD_IA("Standard_IA"),

    /** 近线存储 */
    NEARLINE("Nearline");


    private String cosStorageClass;

    COSStorageClass(String cosStorageClass) {
        this.cosStorageClass = cosStorageClass;
    }

    public String getStorageClass(){
        return cosStorageClass;
    }
}
