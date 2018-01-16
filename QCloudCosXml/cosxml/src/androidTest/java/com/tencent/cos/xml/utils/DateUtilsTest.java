package com.tencent.cos.xml.utils;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by bradyxiao on 2017/12/12.
 */

public class DateUtilsTest extends ApplicationTestCase {

    public DateUtilsTest() {
        super(Application.class);
    }

    @Test
    public void test() throws ParseException, UnsupportedEncodingException, CosXmlClientException {
       Log.d("Unit_Test", DateUtils.toString(new Date()));

      Log.d("Unit_Test", DateUtils.toString(System.currentTimeMillis()));

      Log.d("Unit_Test", DateUtils.toDate("Tue, 12 Dec 2017 14:59:40 GMT").toString());

    }
}
