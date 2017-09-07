package com.tencent.cos.xml.common;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 */
public enum COSACL {
    /**
     * public-read : 公有读私有写
     * private ：私有读写
     * public-read-write ：公有读写
     */
    PUBLIC_READ("public-read"),
    PRIVATE("private"),
    PUBLIC_READ_WRITE("public-read-write");

    private String acl;
    COSACL(String acl ) {
        this.acl = acl;
    }

    public String getACL(){
        return acl;
    }
}
