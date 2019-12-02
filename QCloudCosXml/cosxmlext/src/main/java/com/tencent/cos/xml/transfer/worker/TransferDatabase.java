package com.tencent.cos.xml.transfer.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.tencent.cos.xml.transfer.constraint.NetworkTypeConverter;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */

@Database(entities = {TransferSpec.class},
        exportSchema = false,
        version = 1)
@TypeConverters(value = {NetworkTypeConverter.class})
public abstract class TransferDatabase extends RoomDatabase {

    private static final String DB_NAME = "com.tencent.cos.xml.transfer.transferdb";

    public static TransferDatabase create(@NonNull Context context) {

        RoomDatabase.Builder<TransferDatabase> builder = Room.databaseBuilder(context, TransferDatabase.class, DB_NAME);

        return builder.fallbackToDestructiveMigration()
                .build();
    }

    public abstract TransferSpecDao transferSpecDao();

}
