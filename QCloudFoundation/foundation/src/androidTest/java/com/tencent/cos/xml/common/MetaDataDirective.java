package com.tencent.cos.xml.common;

/**
 * Created by bradyxiao on 2017/9/20.
 * author bradyxiao
 */
public enum MetaDataDirective {
    COPY("Copy"),
    REPLACED("Replaced");

    String directive;
    MetaDataDirective(String directive){
        this.directive = directive;
    }

    public String getMetaDirective(){
        return directive;
    }
}
