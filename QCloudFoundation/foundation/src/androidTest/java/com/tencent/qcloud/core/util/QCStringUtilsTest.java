package com.tencent.qcloud.core.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by wjielai on 2017/10/16.
 */
public class QCStringUtilsTest {
    @Test
    public void isEmpty() throws Exception {
        assertTrue(QCloudStringUtils.isEmpty(""));
        assertTrue(QCloudStringUtils.isEmpty(null));
        assertFalse(QCloudStringUtils.isEmpty("aa"));
    }

    @Test
    public void equals() throws Exception {
        assertTrue(QCloudStringUtils.equals("aa", "aa"));
        assertFalse(QCloudStringUtils.equals("aa", "ab"));

        String letters = "letters";
        StringBuilder sb1 = new StringBuilder(letters);
        StringBuilder sb2 = new StringBuilder(letters);
        StringBuilder sb3 = new StringBuilder("lett3rs");

        assertTrue(QCloudStringUtils.equals(sb1, sb2));
        assertFalse(QCloudStringUtils.equals(sb1, sb3));

        assertFalse(QCloudStringUtils.regionMatches(sb1, true, 0, sb3,
                0, 7));
    }

}