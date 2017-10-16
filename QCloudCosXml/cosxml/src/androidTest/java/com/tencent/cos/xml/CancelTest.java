package com.tencent.cos.xml;

import android.util.Log;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CancelTest {


    @Test public void test() {

        List<String> list = new LinkedList<>();
        list.add("123");
        list.add("234");
        Log.d("TAG", getXCOSGrantForId(list));
        Log.d("TAG", getXCOSGrantForUIN(list));
    }


    private String getXCOSGrantForId(List<String> idList){
        if(idList != null){
            int size = idList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size -1; ++ i){
                    stringBuilder.append("id=\"qcs::cam::")
                            .append(idList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("id=\"qcs::cam::")
                        .append(idList.get(size -1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    private String getXCOSGrantForUIN(List<String> uinList){
        if(uinList != null){
            int size = uinList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size - 1; ++ i){
                    stringBuilder.append("uin=")
                            .append("\"").append(uinList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("uin=")
                        .append("\"").append(uinList.get(size - 1)).append("\"");
                return stringBuilder.toString();
            }

        }
        return null;
    }
}
