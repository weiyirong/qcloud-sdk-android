package com.tencent.cos.xml.model.tag;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class ACLAccountTest {


    @Test
    public void aclDesc() throws Exception {

        ACLAccount aclAccount = new ACLAccount("OwnerUin");
        assertEquals("qcs::cam::uin/OwnerUin:uin/OwnerUin", aclAccount.aclDesc());

        aclAccount = new ACLAccount("OwnerUin", "SubUin");
        assertEquals("qcs::cam::uin/OwnerUin:uin/SubUin", aclAccount.aclDesc());
    }

}