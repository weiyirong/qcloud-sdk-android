package com.tencent.cos.xml.common;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public enum  COSACL {
    PRIVATE("private"),
    PUBLIC_READ("public-read"),
    PUBLIC_READ_WRITE("public-read-write");
    private String acl;
    COSACL(String acl){
        this.acl = acl;
    }
    public String getAcl(){
        return acl;
    }
}
