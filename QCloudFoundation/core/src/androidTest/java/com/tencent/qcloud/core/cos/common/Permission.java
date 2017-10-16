package com.tencent.qcloud.core.cos.common;

/**
 * Created by bradyxiao on 2017/5/24.
 * author bradyxiao
 */
public enum Permission {
    READ("READ"),
    WRITE("WRITE"),
    FULL_CONTROL("FULL_CONTROL");
    private String permission;
    Permission(String permission){
        this.permission = permission;
    }
    public String getPermission(){
        return permission;
    }
}
