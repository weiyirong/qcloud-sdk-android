package com.tencent.cos.xml.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class COSStorageClassTest {

    @Test
    public void valueOf() throws Exception {

        assertEquals(COSStorageClass.NEARLINE, COSStorageClass.valueOf("NEARLINE"));

    }

    @Test public void values() throws Exception {

        assertEquals(true, COSStorageClass.values().length > 0);
    }

}