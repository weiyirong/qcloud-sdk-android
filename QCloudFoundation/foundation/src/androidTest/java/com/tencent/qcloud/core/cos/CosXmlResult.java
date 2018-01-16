package com.tencent.qcloud.core.cos;


import com.tencent.qcloud.core.cos.tag.COSXMLError;
import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * <p>
 * COS XML SDK请求结果基类。
 * </p>
 *
 *
 */
public abstract class CosXmlResult {
    /**
     * 如果请求错误，服务端返回的错误信息
     * @see COSXMLError
     */
    @XStreamAlias("Error")
    public COSXMLError error;

    /**
     * return eTag( such as md5) for object
     */
    private String eTag;

    /**
     *  返回对象的URL
     */
    public String accessUrl;

    /**
     *  print response body for result
     * @return String
     */
    public String printBody(){
        return null;
    }

    /**
     * print error for result, {@link COSXMLError}
     * @return String
     */
    public String printError(){
        return error != null ? error.toString() : null;
    }
}
