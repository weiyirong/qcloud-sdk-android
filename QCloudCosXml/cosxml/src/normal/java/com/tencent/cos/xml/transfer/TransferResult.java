package com.tencent.cos.xml.transfer;

import java.util.UUID;

/**
 * Created by bradyxiao on 2018/8/23.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 *
 * this is a class of identify a result;
 * id is the key of this;
 * some property of this before operator;
 *
 */

public class TransferResult {

    private int id;

    public TransferResult(){
        id = UUID.randomUUID().hashCode();
    }

    private int getId(){
        return id;
    }


}
