package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.Arrays;
import java.util.List;

final public class OptionObjectResult extends CosXmlResult {
    private String accessControlAllowOrigin;
    private List<String> accessControlAllowMethods;
    private List<String> accessControlAllowHeaders;
    private List<String> accessControlExposeHeaders;
    private long accessControlMaxAge;

    /**
     * 获取 Access-Control-Allow-Origin 头部
     *
     * @return Access-Control-Allow-Origin 头部
     */
    public String getAccessControlAllowOrigin() {
        List<String> list = getHeaders().get("Access-Control-Allow-Origin");
        if(list != null){
            accessControlAllowOrigin = list.get(0);
        }
        return accessControlAllowOrigin;
    }

    /**
     * 获取 Access-Control-Max-Age 头部
     *
     * @return Access-Control-Max-Age 头部
     */
    public long getAccessControlMaxAge() {
        List<String> list = getHeaders().get("Access-Control-Max-Age");
        if(list != null){
            accessControlMaxAge = Long.parseLong(list.get(0));
        }
        return accessControlMaxAge;
    }

    /**
     * 获取 Access-Control-Allow-Methods 头部
     *
     * @return Access-Control-Allow-Methods 头部
     */
    public List<String> getAccessControlAllowMethods() {
        List<String> list = getHeaders().get("Access-Control-Allow-Methods");
        if(list != null){
            accessControlAllowMethods = Arrays.asList(list.get(0).split(","));
        }
        return accessControlAllowMethods;
    }

    /**
     * 获取 Access-Control-Allow-Headers 头部
     *
     * @return Access-Control-Allow-Headers 头部
     */
    public List<String> getAccessControlAllowHeaders() {
        List<String> list = getHeaders().get("Access-Control-Allow-Headers");
        if(list != null){
            accessControlAllowHeaders = Arrays.asList(list.get(0).split(","));
        }
        return accessControlAllowHeaders;
    }

    /**
     * 获取 Access-Control-Expose-Headers 头部
     *
     * @return Access-Control-Expose-Headers 头部
     */
    public List<String> getAccessControlExposeHeaders() {
        List<String> list = getHeaders().get("Access-Control-Expose-Headers");
        if(list != null){
            accessControlExposeHeaders = Arrays.asList(list.get(0).split(","));
        }
        return accessControlExposeHeaders;
    }

    @Override
    public String printHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.printHeaders())
                .append(getAccessControlAllowOrigin()).append("\n")
                .append(getAccessControlMaxAge()).append("\n");
        getAccessControlAllowMethods();
        getAccessControlAllowHeaders();
        getAccessControlExposeHeaders();
        if(accessControlAllowHeaders != null){
            int size = accessControlAllowHeaders.size();
            for(int i = 0; i < size -1; ++ i){
                stringBuilder.append(accessControlAllowHeaders.get(i)).append(",");
            }
            stringBuilder.append(accessControlAllowHeaders.get(size -1)).append("\n");
        }
        if(accessControlAllowMethods != null){
            int size = accessControlAllowMethods.size();
            for(int i = 0; i < size -1; ++ i){
                stringBuilder.append(accessControlAllowMethods.get(i)).append(",");
            }
            stringBuilder.append(accessControlAllowMethods.get(size -1)).append("\n");
        }
        if(accessControlExposeHeaders != null){
            int size = accessControlExposeHeaders.size();
            for(int i = 0; i < size -1; ++ i){
                stringBuilder.append(accessControlExposeHeaders.get(i)).append(",");
            }
            stringBuilder.append(accessControlExposeHeaders.get(size -1)).append("\n");
        }
        return  stringBuilder.toString();
    }
}
