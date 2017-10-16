package com.tencent.cos.xml.model.tag;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ACLAccount {

    private String ownerUin;

    private String subUin;

    public ACLAccount(String ownerUin, String subUin) {

        this.ownerUin = ownerUin;
        this.subUin = subUin;
    }

    public ACLAccount(String ownerUin) {
        this(ownerUin, null);
    }

    public String aclDesc() {

        return String.format(Locale.ENGLISH, "qcs::cam::uin/%s:uin/%s",
                ownerUin, TextUtils.isEmpty(subUin) ? ownerUin : subUin);
    }

}
