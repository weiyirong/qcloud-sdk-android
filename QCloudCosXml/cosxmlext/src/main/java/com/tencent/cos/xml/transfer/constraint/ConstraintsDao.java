package com.tencent.cos.xml.transfer.constraint;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@Dao
public interface ConstraintsDao {

    @Insert(onConflict = REPLACE)
    void insert(Constraints constraints);

    @Query("SELECT * FROM constraints")
    Constraints getConstraints();

    @Query("SELECT * FROM constraints")
    LiveData<Constraints> getNetworkConstraintsLiveData();

    @Query("UPDATE constraints SET networkType=:networkType")
    int updateNetworkType(NetworkType networkType);


}
