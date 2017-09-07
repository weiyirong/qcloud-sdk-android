package com.tencent.cos.xml.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class COSACLTest {


    @Test
    public void valueOf() throws Exception {

        assertEquals(COSACL.PRIVATE, COSACL.valueOf("PRIVATE"));

    }

    @Test public void values() throws Exception {

        assertEquals(true, COSACL.values().length > 0);
    }

}