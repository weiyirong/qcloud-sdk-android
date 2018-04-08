package com.tencent.cos.xml.common;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/3/14.
 */
@RunWith(AndroidJUnit4.class)
public class VersionInfoTest {

    @Test
    public void test() throws Exception{
        assertEquals(VersionInfo.platform , VersionInfo.getUserAgent());
    }
}