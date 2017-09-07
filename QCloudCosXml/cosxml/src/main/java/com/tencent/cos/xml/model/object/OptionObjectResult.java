package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 */
public class OptionObjectResult extends CosXmlResult {
    private String accessControlAllowOrigin;
    private List<String> accessControlAllowMethods;
    private List<String> accessControlAllowHeaders;
    private List<String> accessControlExposeHeaders;
    private long accessControlMaxAge;

    public String getAccessControlAllowOrigin() {
        List<String> list = getHeaders().get("Access-Control-Allow-Origin");
        if(list != null){
            accessControlAllowOrigin = list.get(0);
        }
        return accessControlAllowOrigin;
    }

    public long getAccessControlMaxAge() {
        List<String> list = getHeaders().get("Access-Control-Max-Age");
        if(list != null){
            accessControlMaxAge = Long.parseLong(list.get(0));
        }
        return accessControlMaxAge;
    }

    public List<String> getAccessControlAllowMethods() {
        List<String> list = getHeaders().get("Access-Control-Allow-Methods");
        if(list != null){
            accessControlAllowMethods = Arrays.asList(list.get(0).split(","));
        }
        return accessControlAllowMethods;
    }

    public List<String> getAccessControlAllowHeaders() {
        List<String> list = getHeaders().get("Access-Control-Allow-Headers");
        if(list != null){
            accessControlAllowHeaders = Arrays.asList(list.get(0).split(","));
        }
        return accessControlAllowHeaders;
    }

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
