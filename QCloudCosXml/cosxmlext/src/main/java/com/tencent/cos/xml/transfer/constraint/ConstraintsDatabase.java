package com.tencent.cos.xml.transfer.constraint;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.work.Data;
import androidx.work.impl.model.WorkTypeConverters;

import com.tencent.cos.xml.transfer.worker.TransferSpec;
import com.tencent.cos.xml.transfer.worker.TransferSpecDao;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */

@Database(entities = {Constraints.class},
        exportSchema = false,
        version = 1)
@TypeConverters(value = {NetworkTypeConverter.class})
public abstract class ConstraintsDatabase extends RoomDatabase {

    private static final String DB_NAME = "com.tencent.cos.xml.transfer.constraints.db";

    public static ConstraintsDatabase create(@NonNull Context context) {

        Builder<ConstraintsDatabase> builder = Room.databaseBuilder(context, ConstraintsDatabase.class, DB_NAME);

        return builder.fallbackToDestructiveMigration()
                .build();
    }

    public abstract ConstraintsDao constraintsDao();

}
