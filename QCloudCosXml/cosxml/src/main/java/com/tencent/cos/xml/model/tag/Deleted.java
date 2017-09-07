package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
@XStreamAlias("Deleted")
public class Deleted {
    /**
     * Name of Object
     */
    @XStreamAlias("Key")
    public String key;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[key:").append(key).append("]");
        return stringBuilder.toString();
    }
}
