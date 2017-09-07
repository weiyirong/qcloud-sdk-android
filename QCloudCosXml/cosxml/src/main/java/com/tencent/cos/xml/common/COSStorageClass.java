package com.tencent.cos.xml.common;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
public enum COSStorageClass {
    STANDARD("Standard"),
    STANDARD_IA("Standard_IA"),
    NEARLINE("Nearline");
    private String cosStorageClass;

    COSStorageClass(String cosStorageClass) {
        this.cosStorageClass = cosStorageClass;
    }
    public String getStorageClass(){
        return cosStorageClass;
    }
}
