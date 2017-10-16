package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.List;


final public class AppendObjectResult extends CosXmlResult {

    private String contentSHA1;
    private String nextAppendPosition;
    // get content sha1
    public String getContentSha1() {
        List<String> list = getHeaders().get("x-cos-content-sha1");
        if(list != null){
            contentSHA1 = list.get(0);
        }
        return contentSHA1;
    }

    // get next append position.
    public String getNextAppendPosition() {
        List<String> list = getHeaders().get("x-cos-next-append-position");
        if(list != null){
            nextAppendPosition = list.get(0);
        }
        return nextAppendPosition;
    }

    @Override
    public String printHeaders() {
        String result = super.printHeaders();

        result = result+ "\n"
                + getContentSha1() + "\n"
                + getNextAppendPosition();
        return result;
    }
}
