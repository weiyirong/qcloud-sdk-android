package com.tencent.cos.xml.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class PermissionTest {


    @Test
    public void valueOf() throws Exception {

        assertEquals(Permission.FULL_CONTROL, Permission.valueOf("FULL_CONTROL"));


    }

    @Test public void values() throws Exception {

        assertEquals(true, Permission.values().length > 0);
    }

}