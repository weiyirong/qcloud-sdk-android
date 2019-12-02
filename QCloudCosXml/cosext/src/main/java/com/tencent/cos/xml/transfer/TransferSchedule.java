package com.tencent.cos.xml.transfer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.NetworkInfo;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.NetworkType;
import androidx.work.WorkManager;
import androidx.work.impl.constraints.WorkConstraintsCallback;
import androidx.work.impl.utils.SerialExecutor;

import com.tencent.cos.xml.constraints.TransferConstraintsTracker;
import com.tencent.cos.xml.constraints.TransferDatabase;
import com.tencent.cos.xml.constraints.TransferExecutor;
import com.tencent.cos.xml.constraints.TransferSpec;
import com.tencent.cos.xml.constraints.TransferSpecDao;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * 人、鬼封神
 *
 *
 * Created by rickenwang on 2019-11-28.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TransferSchedule {

    private final String TAG = "TransferSchedule";

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
        this.cosxmlTasks = new HashMap<>();
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


    public void schedule(TransferRequest transferRequest, COSXMLTask cosxmlTask, TransferWorkManager transferWorkManager) {

        executor.execute(() -> {

            String id = transferRequest.getId();

            // 如果之前存在 Worker 任务，则直接取消
            TransferSpec existSpec = getTransferSpecByEndpoint(transferRequest);
            if (existSpec != null) {
                if (!TextUtils.isEmpty(existSpec.workerId)) {
                    cancelBackgroundWorker(id, existSpec.workerId);
                    insertAndBindTransferTask(transferRequest, cosxmlTask, transferWorkManager);
                } else {
                    //
                    if (existSpec.state == TransferStatus.CANCELED
                            || existSpec.state == TransferStatus.COMPLETED
                            || existSpec.state == TransferStatus.FAILED) {
                        // 覆盖之前的任务
                        insertAndBindTransferTask(transferRequest, cosxmlTask, transferWorkManager);
                    } else {
                        QCloudLogger.w(TAG, transferRequest.toString() + " has already scheduled!");
                    }
                }
            } else {
                insertAndBindTransferTask(transferRequest, cosxmlTask, transferWorkManager);
            }
        });
    }

    @Nullable private TransferSpec getTransferSpecByEndpoint(TransferRequest transferRequest) {

        String bucket  = transferRequest.getBucket();
        String cosKey = "";
        String filePath = "";

        if (transferRequest instanceof UploadRequest) {
            UploadRequest uploadRequest = (UploadRequest) transferRequest;
            cosKey = uploadRequest.getCosKey();
            filePath = uploadRequest.getFilePath();
        }
        return transferSpecDao.getTransferSpecByEndpoint(bucket, cosKey, filePath);
    }

    private void insertAndBindTransferTask(TransferRequest transferRequest, COSXMLTask cosxmlTask, TransferWorkManager transferWorkManager) {

        cosxmlTasks.put(transferRequest.getId(), cosxmlTask);

        if (transferRequest instanceof UploadRequest) {
            transferSpecDao.insertTransferSpec(new TransferSpec((UploadRequest) transferRequest));
        } else {

        }
        transferSpecDao.updateTransferSpecServiceEgg(transferRequest.getId(), new String(transferWorkManager.toBytes()));
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
            if (isTransferByTask(transferSpec) && (transferSpec.state == TransferStatus.CONSTRAINED
                    || transferSpec.state == TransferStatus.UNKNOWN)) {

                COSXMLTask cosxmlTask = cosxmlTasks.get(transferSpec.id);

                // 2
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.PAUSED) {
                    cosxmlTask.resume();
                }

                // 1
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.UNKNOWN) {
                    startTask(cosxmlTask, transferSpec.uploadId);
                }

                observeCosTask(cosxmlTask, transferSpec.id);
            }
        }

    }

    private void observeCosTask(COSXMLTask cosxmlTask, String transferSpecId) {

        if (cosxmlTask == null || TextUtils.isEmpty(transferSpecId)) {
            return;
        }

        cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {

            private long lastComplete = 0;

            @Override
            public void onProgress(long complete, long target) {

                if (lastComplete == 0 && cosxmlTask instanceof COSXMLUploadTask) {
                    transferSpecDao.updateTransferSpecUploadId(transferSpecId, ((COSXMLUploadTask) cosxmlTask).getUploadId());
                }

                if (lastComplete == 0 || complete - lastComplete > target * 0.08) {
                    lastComplete = complete;
                    transferSpecDao.updateTransferSpecTransferProgress(transferSpecId, complete, target);
                }
            }
        });

        cosxmlTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                transferSpecDao.updateTransferSpecTransferStatus(transferSpecId, convertState(state));
            }
        });
    }

    private TransferStatus convertState(TransferState state) {

        switch (state) {

            case PAUSED:
                return TransferStatus.PAUSED;
            case IN_PROGRESS:
                return TransferStatus.IN_PROGRESS;
            case COMPLETED:
                return TransferStatus.COMPLETED;
            case WAITING:
                return TransferStatus.WAITING;
            case RESUMED_WAITING:
                return TransferStatus.RESUMED_WAITING;
            case FAILED:
                return TransferStatus.FAILED;
            case CANCELED:
                return TransferStatus.CANCELED;
            case UNKNOWN:
                return TransferStatus.UNKNOWN;
        }

        return TransferStatus.UNKNOWN;
    }

    /**
     *
     * @param transferSpecIds
     */
    private void constraintsNotMetTransfers(@NonNull List<String> transferSpecIds) {

        List<TransferSpec> transferSpecs = transferSpecDao.getTransferSpecs(transferSpecIds);

        for (TransferSpec transferSpec : transferSpecs) {

            if (isTransferByTask(transferSpec) && transferSpec.state != TransferStatus.CONSTRAINED) {

                COSXMLTask cosxmlTask = cosxmlTasks.get(transferSpec.id);
                if (cosxmlTask != null && isTaskRunning(cosxmlTask.getTaskState())) {
                    cosxmlTask.pause();
                }
            }
        }
    }

    private boolean isTaskRunning(TransferState state) {

        return state == TransferState.IN_PROGRESS
                || state == TransferState.WAITING
                || state == TransferState.RESUMED_WAITING;
    }


    private boolean isTransferByTask(@NonNull TransferSpec transferSpec) {

        return TextUtils.isEmpty(transferSpec.workerId);
    }


    private void cancelBackgroundWorker(String id, String workerId) {

        WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workerId));
        transferSpecDao.updateTransferSpecWorkerId(id, "");
    }

    private void startTask(@NonNull COSXMLTask cosxmlTask, String uploadId) {

        if (cosxmlTask instanceof COSXMLUploadTask) {
            COSXMLUploadTask uploadTask = (COSXMLUploadTask) cosxmlTask;
            if (!TextUtils.isEmpty(uploadId)) {
                uploadTask.setUploadId(uploadId);
            }
            uploadTask.upload();
        } else if (cosxmlTask instanceof COSXMLDownloadTask) {
            ((COSXMLDownloadTask) cosxmlTask).download();
        }

    }


}
