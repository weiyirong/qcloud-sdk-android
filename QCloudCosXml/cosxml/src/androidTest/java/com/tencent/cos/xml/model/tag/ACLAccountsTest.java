package com.tencent.cos.xml.model.tag;

import android.text.TextUtils;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class ACLAccountsTest {

    ACLAccounts aclAccounts;

    @Before public void setUp() throws Exception {

        aclAccounts = new ACLAccounts();

    }

    @Test
    public void aclDesc() throws Exception {

        aclAccounts.addACLAccount(new ACLAccount("owner1"));
        aclAccounts.addACLAccount(new ACLAccount("owner2", "sub2"));
        assertEquals(2, aclAccounts.getAclAccounts().size());
        assertEquals("id=\"qcs::cam::uin/owner1:uin/owner1\",id=\"qcs::cam::uin/owner2:uin/sub2\"",
                aclAccounts.aclDesc());
        aclAccounts.clear();
        assertTrue(TextUtils.isEmpty(aclAccounts.aclDesc()));

    }

}