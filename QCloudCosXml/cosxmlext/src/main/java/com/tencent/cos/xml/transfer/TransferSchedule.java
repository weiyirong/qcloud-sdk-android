package com.tencent.cos.xml.transfer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.NetworkType;
import androidx.work.WorkManager;
import androidx.work.impl.constraints.WorkConstraintsCallback;
import androidx.work.impl.utils.SerialExecutor;

import com.tencent.cos.xml.transfer.constraints.ConstraintsTransferState;
import com.tencent.cos.xml.transfer.constraints.TransferConstraintsTracker;
import com.tencent.cos.xml.transfer.constraints.TransferDatabase;
import com.tencent.cos.xml.transfer.constraints.TransferExecutor;
import com.tencent.cos.xml.transfer.constraints.TransferSpec;
import com.tencent.cos.xml.transfer.constraints.TransferSpecDao;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Created by rickenwang on 2019-11-28.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TransferSchedule {

    private static volatile TransferSchedule instance;

    private Context context;

    private SerialExecutor executor;

    private TransferDatabase transferDatabase;

    private TransferSpecDao transferSpecDao;

    private TransferConstraintsTracker constraintsTracker;

    private HashMap<String, COSXMLTask> cosxmlTasks;

    public static synchronized TransferSchedule getInstance(Context context) {

        if (instance == null) {
            instance = new TransferSchedule(context);
        }
        return instance;
    }



    private TransferSchedule(Context context) {

        this.context = context;
        this.executor = new SerialExecutor(Executors.newFixedThreadPool(1));
        transferDatabase = TransferDatabase.create(context);
        transferSpecDao = transferDatabase.transferSpecDao();
        constraintsTracker = new TransferConstraintsTracker(context, new TransferExecutor(executor),
                new WorkConstraintsCallback() {
                    @Override
                    public void onAllConstraintsMet(@NonNull List<String> transferSpecIds) {

                        executor.execute(() -> {
                            constraintsMetTransfers(transferSpecIds);
                        });

                    }

                    @Override
                    public void onAllConstraintsNotMet(@NonNull List<String> transferSpecIds) {

                        executor.execute(() -> {
                            constraintsNotMetTransfers(transferSpecIds);
                        });
                    }
                });

        // 关心两个
        transferSpecDao.getAllTransferSpecsLiveData().observeForever(transferSpecs -> {

            if (transferSpecs == null) {
                return;
            }
            constraintsTracker.replace(transferSpecs);
        });
    }


    public void schedule(TransferSpec transferSpec, COSXMLTask cosxmlTask) {

        executor.execute(() -> {

            // 取消 WorkManager 中的任务
            TransferSpec currentSpec = transferSpecDao.getTransferSpecs(transferSpec.id);
            if (currentSpec != null) {
                cancelWorkIfExist(currentSpec.workId);
            }

            cosxmlTask.setInternalStateListener(state ->  {

                executor.execute(() -> {



                });
            });

            transferSpecDao.insertTransferSpec(transferSpec);
            cosxmlTasks.put(transferSpec.id, cosxmlTask);
        });
    }

    public TransferSpecDao getTransferSpecDao() {
        return transferSpecDao;
    }

    public void updateTransferNetworkConstraint(String transferSpecId, NetworkType networkType) {

        transferSpecDao.updateTransferSpecNetworkConstraints(transferSpecId, networkType);
    }

    public void updateTransferNetworkConstraint(List<String> transferSpecIds, NetworkType networkType) {

        transferSpecDao.updateTransferSpecsNetworkConstraints(transferSpecIds, networkType);
    }

    public void updateAllTransferNetworkConstraints(NetworkType networkType) {

        transferSpecDao.updateAllTransferSpecsNetworkConstraints(networkType);
    }

    /**
     * 约束条件被满足
     *
     *
     *
     *
     * @param transferSpecIds
     */
    private void constraintsMetTransfers(@NonNull List<String> transferSpecIds) {

        List<TransferSpec> transferSpecs = transferSpecDao.getTransferSpecs(transferSpecIds);

        for (TransferSpec transferSpec : transferSpecs) {

            /**
             * 1、调用 TransferManager#upload() 方法后，一直处于 CONSTRAINED 状态
             * 2、调用 TransferManager#upload() 方法后，先 IN_PROGRESS 后 CONSTRAINED
             * 3、本次启动没有调用  TransferManager#upload() 方法，而是上次 app 启动时调用的，
             *    这种交给 WorkManager 处理，TransferSchedule 可以直接忽略
             */
            if (transferSpec.state == ConstraintsTransferState.CONSTRAINED) {

                COSXMLTask cosxmlTask = cosxmlTasks.get(transferSpec.id);

                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.PAUSED) {
                    cosxmlTask.resume();
                }

            }
        }

    }

    private void cancelWorkIfExist(String workId) {

        if (!TextUtils.isEmpty(workId)) {
            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workId));
        }
    }


    /**
     *
     * @param transferSpecIds
     */
    private void constraintsNotMetTransfers(@NonNull List<String> transferSpecIds) {

    }
}
