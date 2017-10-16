package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/31.
 * author bradyxiao
 */
@XStreamAlias("Object")
public class DeleteObject {
    @XStreamAlias("Key")
    public String key;
}
