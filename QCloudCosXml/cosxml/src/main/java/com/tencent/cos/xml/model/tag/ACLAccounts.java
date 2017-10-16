package com.tencent.cos.xml.model.tag;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ACLAccounts {

    private List<ACLAccount> aclAccounts;

    public ACLAccounts() {
        aclAccounts = new LinkedList<>();
    }

    public List<ACLAccount> getAclAccounts() {
        return aclAccounts;
    }

    public void addACLAccount(ACLAccount aclAccount) {

        aclAccounts.add(aclAccount);
    }

    public ACLAccount remove(int index) {

        return aclAccounts.remove(index);
    }

    public void clear() {

        aclAccounts.clear();
    }

    public String aclDesc() {

        StringBuilder desc = new StringBuilder();
        int count = 0;
        for (ACLAccount aclAccount : aclAccounts) {

            desc.append(String.format(Locale.ENGLISH, "id=\"%s\"", aclAccount.aclDesc()));
            count++;
            if (count < aclAccounts.size()) {
                desc.append(",");
            }
        }
        return desc.toString();
    }
}
