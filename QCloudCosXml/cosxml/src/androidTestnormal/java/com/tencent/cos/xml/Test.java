package com.tencent.cos.xml;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradyxiao on 2018/3/6.
 */
@RunWith(AndroidJUnit4.class)
public class Test {

    @org.junit.Test
    public void testSDK() throws Exception{
        Context context = InstrumentationRegistry.getContext();
        CreateBucketTool createBucketTool = new CreateBucketTool(context);
        createBucketTool.createBucket("mm");
    }

}
