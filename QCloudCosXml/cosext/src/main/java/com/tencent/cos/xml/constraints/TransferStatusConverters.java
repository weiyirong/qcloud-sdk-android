package com.tencent.cos.xml.constraints;

import androidx.room.TypeConverter;

import com.tencent.cos.xml.transfer.TransferStatus;

/**
 * Created by rickenwang on 2019-11-28.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class TransferStatusConverters {

    @TypeConverter
    public TransferStatus convert2Status(String status) {

        return TransferStatus.getState(status);
    }

    @TypeConverter
    public String conver2Str(TransferStatus transferStatus) {

        return transferStatus.name();
    }
}
