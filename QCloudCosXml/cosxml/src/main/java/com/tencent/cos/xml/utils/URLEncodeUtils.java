package com.tencent.cos.xml.utils;

import com.tencent.cos.xml.exception.CosXmlClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by bradyxiao on 2017/12/14.
 */

public class URLEncodeUtils {

    public static String cosPathEncode(String cosPath) throws CosXmlClientException {
        if(cosPath == null)return null;
        StringBuilder result = new StringBuilder();
        String[] division = cosPath.split("/");
        try {
            for(int i = 0; i < division.length -1; i ++){
                result.append(URLEncoder.encode(division[i], "utf-8")).append("/");
            }
            if(!cosPath.endsWith("/")){
                result.append(division.length > 0 ? (URLEncoder.encode(division[division.length - 1], "utf-8")) : "");
            }else {
                result.append(division.length > 0 ? (URLEncoder.encode(division[division.length - 1], "utf-8")) : "").append("/");
            }
            return result.toString();
        }catch (UnsupportedEncodingException e) {
            throw new CosXmlClientException(e);
        }
    }
}
