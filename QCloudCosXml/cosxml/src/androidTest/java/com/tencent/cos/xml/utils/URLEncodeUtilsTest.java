package com.tencent.cos.xml.utils;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/1/2.
 */
public class URLEncodeUtilsTest extends ApplicationTestCase {


    public URLEncodeUtilsTest() {
        super(Application.class);
    }

    @Test
    public void testCosPathEncode() throws Exception {
       String url = URLEncodeUtils.cosPathEncode("/test/1.txt/");
        Log.d("Unit_Test", url);
    }

}