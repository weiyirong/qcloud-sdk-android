package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("CommonPrefixs")
public class CommonPrefixes {
    @XStreamAlias("Prefix")
    public String prefix;

    @Override
    public String toString(){
        return "{\nPrefix:" + prefix + "\n}";
    }
}
