package com.tencent.cos.xml.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class RegionTest {

    @Test
    public void valueOf() throws Exception {

        assertEquals(Region.AP_Beijing_1, Region.valueOf("AP_Beijing_1"));

    }

    @Test public void values() throws Exception {

        assertEquals(true, Region.values().length > 0);
    }

}