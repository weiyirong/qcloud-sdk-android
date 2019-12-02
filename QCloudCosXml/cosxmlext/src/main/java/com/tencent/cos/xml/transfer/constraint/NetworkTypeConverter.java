package com.tencent.cos.xml.transfer.constraint;

import androidx.room.TypeConverter;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class NetworkTypeConverter {

    @TypeConverter
    public NetworkType deserialize(int value) {
        return NetworkType.deserialize(value);
    }

    @TypeConverter
    public int serialize(NetworkType state) {
        return NetworkType.serialize(state);
    }
}
