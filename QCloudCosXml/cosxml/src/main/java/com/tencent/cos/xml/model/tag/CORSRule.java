package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("CORSRule")
public class CORSRule {
    @XStreamAlias("ID")
    public String id;
    @XStreamAlias("AllowedOrigin")
    public String allowedOrigin;
    @XStreamImplicit(itemFieldName = "AllowedMethod")
    public List<String> allowedMethod;
    @XStreamImplicit(itemFieldName = "AllowedHeader")
    public List<String> allowedHeader;
    @XStreamAlias("MaxAgeSeconds")
    public String maxAgeSeconds;
    @XStreamImplicit(itemFieldName = "ExposeHeader")
    public List<String> exposeHeader;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("ID:").append(id).append("\n")
                .append("AllowedOrigin:").append(allowedOrigin).append("\n")
                .append("AllowedMethod:").append(allowedMethod).append("\n")
                .append("AllowedHeader:").append(allowedHeader).append("\n")
                .append("MaxAgeSeconds:").append(maxAgeSeconds).append("\n")
                .append("ExposeHeader:").append(exposeHeader).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
