package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.model.CosXmlResult;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/7.
 * author bradyxiao
 */
public class AppendObjectResult extends CosXmlResult {

    private String xCOSContentSha1;
    private String xCOSNextAppendPosition;
    // get content sha1
    public String getContentSha1() {
        List<String> list = getHeaders().get("x-cos-content-sha1");
        if(list != null){
            xCOSContentSha1 = list.get(0);
        }
        return xCOSContentSha1;
    }

    // get next append position.
    public String getNextAppendPosition() {
        List<String> list = getHeaders().get("x-cos-next-append-position");
        if(list != null){
            xCOSNextAppendPosition = list.get(0);
        }
        return xCOSNextAppendPosition;
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
