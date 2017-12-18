package com.tencent.qcloud.core.cos.common;

/**
 * <p>
 * 定义COS ACL的三种有效属性
 * </p>
 * <p>
 * 详情参见<a
 * href="https://www.qcloud.com/document/product/436/7737"> COS 创建Bucket </a>
 * </p>
 */
public enum COSACL {

    /**
     * public-read : 公有读私有写
     */
    PUBLIC_READ("public-read"),

    /**
     * private ：私有读写
     */
    PRIVATE("private"),

    /**
     * public-read-write ：公有读写
     */
    PUBLIC_READ_WRITE("public-read-write");

    private String acl;

    COSACL(String acl ) {
        this.acl = acl;
    }

    public String getACL(){
        return acl;
    }
}
