package com.tencent.qcloud.core.cos.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/31.
 * author bradyxiao
 */
@XStreamAlias("CompleteMultipartUpload")
public class CompleteMultipartUpload {
    @XStreamImplicit(itemFieldName = "Part")
    public List<Part> partList;
}
