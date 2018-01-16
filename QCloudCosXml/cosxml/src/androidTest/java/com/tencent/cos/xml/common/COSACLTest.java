package com.tencent.cos.xml.common;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/8.
 */
public class COSACLTest extends ApplicationTestCase {
    public COSACLTest() {
        super(Application.class);
    }

    @Test
    public void testGetAcl() throws Exception {
        assertEquals("private",COSACL.PRIVATE.getAcl());
        assertEquals("public-read",COSACL.PUBLIC_READ.getAcl());
        assertEquals("public-read-write",COSACL.PUBLIC_READ_WRITE.getAcl());
    }

}