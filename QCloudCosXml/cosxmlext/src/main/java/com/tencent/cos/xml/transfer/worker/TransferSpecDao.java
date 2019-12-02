package com.tencent.cos.xml.transfer.worker;

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
public interface TransferSpecDao {

    @Insert(onConflict = REPLACE)
    void insertTransferSpec(TransferSpec transferSpec);

    @Query("DELETE FROM transferspec WHERE id=:id")
    void delete(String id);

    @Query("SELECT * FROM transferspec")
    List<TransferSpec> getAllTransferSpecs();

    @Query("SELECT * FROM transferspec WHERE id=:id")
    LiveData<TransferSpec> getTransferSpecLiveData(String id);

    @Query("SELECT * FROM transferspec WHERE bucket=:bucket AND `key`=:key AND filePath=:filePath")
    LiveData<TransferSpec> getTransferSpecLiveData(String bucket, String key, String filePath);

    @Query("SELECT * FROM transferspec WHERE bucket=:bucket AND `key`=:key AND filePath=:filePath")
    TransferSpec getTransferSpec(String bucket, String key, String filePath);

    @Query("UPDATE transferspec SET completeBackground=:isComplete WHERE id=:id")
    int setSuccess(String id, boolean isComplete);

    @Query("UPDATE transferspec SET workerId=:workerId WHERE id=:id")
    int setWorkerId(String id, String workerId);

    @Query("DELETE FROM transferspec WHERE id=:id")
    void deleteTransferSpec(String id);
}
