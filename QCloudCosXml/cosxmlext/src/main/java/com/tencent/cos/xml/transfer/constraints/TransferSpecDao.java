package com.tencent.cos.xml.transfer.constraints;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.work.NetworkType;
import androidx.work.impl.model.WorkSpec;

import java.util.List;

import static androidx.room.OnConflictStrategy.*;

/**
 * Created by rickenwang on 2019-11-28.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@Dao
public interface TransferSpecDao {


    @Insert(onConflict = REPLACE)
    void insertTransferSpec(TransferSpec transferSpec);

    @Query("SELECT * FROM transferspec")
    LiveData<List<TransferSpec>> getAllTransferSpecsLiveData();

    @Query("SELECT * FROM transferspec WHERE id IN (:ids)")
    List<TransferSpec> getTransferSpecs(List<String> ids);

    @Query("SELECT * FROM transferspec WHERE id=:id")
    TransferSpec getTransferSpecs(String id);

    @Query("UPDATE transferspec SET networkType=:networkType WHERE id=:id")
    void updateTransferSpecNetworkConstraints(String id, NetworkType networkType);

    @Query("UPDATE transferspec SET networkType=:networkType WHERE id IN (:ids)")
    void updateTransferSpecsNetworkConstraints(List<String> ids, NetworkType networkType);

    @Query("UPDATE transferspec SET networkType=:networkType")
    void updateAllTransferSpecsNetworkConstraints(NetworkType networkType);
}
