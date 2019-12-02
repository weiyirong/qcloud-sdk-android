package com.tencent.cos.xml.transfer.constraints;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

/**
 * Created by rickenwang on 2019-11-28.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Database(entities = {TransferSpec.class}, version = 1)
@TypeConverters(value = {TransferStateConverters.class})
public abstract class TransferDatabase extends RoomDatabase {

    private static final String DB_NAME = "com.tencent.cos.xml.constraints.transfer.db";


    public static TransferDatabase create(@NonNull Context context) {

        return Room.databaseBuilder(context, TransferDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }


    public abstract TransferSpecDao transferSpecDao();

}
