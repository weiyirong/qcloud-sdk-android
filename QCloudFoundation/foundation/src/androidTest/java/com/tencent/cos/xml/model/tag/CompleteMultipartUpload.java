package com.tencent.cos.xml.model.tag;

import java.util.List;

/**
 * Created by bradyxiao on 2017/11/24.
 */

public class CompleteMultipartUpload {
    public List<Part> parts;

    public static class Part{
        public int partNumber;
        public String eTag;
    }
}
