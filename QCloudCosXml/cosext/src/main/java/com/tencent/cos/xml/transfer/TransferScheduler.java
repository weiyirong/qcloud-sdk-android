package com.tencent.cos.xml.transfer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.NetworkType;
import androidx.work.WorkManager;
import androidx.work.impl.constraints.WorkConstraintsCallback;
import androidx.work.impl.utils.SerialExecutor;

import com.tencent.cos.xml.common.ClientErrorCode;
import com.tencent.cos.xml.constraints.TransferConstraintsTracker;
import com.tencent.cos.xml.constraints.TransferDatabase;
import com.tencent.cos.xml.constraints.TransferExecutor;
import com.tencent.cos.xml.constraints.TransferSpec;
import com.tencent.cos.xml.constraints.TransferSpecDao;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 *
 *
 * Created by rickenwang on 2019-11-28.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TransferScheduler {

    private final String TAG = "TransferSchedule";

    private static volatile TransferScheduler instance;

    private Context context;

    private SerialExecutor executor;

    private TransferDatabase transferDatabase;

    private TransferSpecDao transferSpecDao;

    private TransferConstraintsTracker constraintsTracker;

    private HashMap<String, COSXMLTask> cosxmlTasks;

    private List<TransferSpec> transferSpecs = new ArrayList<>();

    public static synchronized TransferScheduler getInstance(Context context) {

        if (instance == null) {
            instance = new TransferScheduler(context);
        }
        return instance;
    }



    private TransferScheduler(Context context) {

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

        // constraintsTracker 需要关注的是：
        // 1、用户的 upload/download、remove
//        transferSpecDao.getAllTransferSpecsLiveData().observeForever(transferSpecs -> {
//
//            if (transferSpecs == null) {
//                return;
//            }
//            constraintsTracker.replace(transferSpecs);
//        });
    }

    private void invokeTransferTaskComplete(COSXMLTask task) {

        task.taskState = TransferState.COMPLETED;
        if (task.cosXmlResultListener != null) {
            task.cosXmlResultListener.onSuccess(null, null);
        }

        if (task.transferStateListener != null) {
            task.transferStateListener.onStateChanged(TransferState.COMPLETED);
        }
    }

    private void invokeTransferTaskFailed(COSXMLTask task) {

        task.taskState = TransferState.FAILED;
        if (task.cosXmlResultListener != null) {
            task.cosXmlResultListener.onFail(null, new CosXmlClientException(ClientErrorCode.DUPLICATE_TASK.getCode(),
                    task.toString() + " has exist!"), null);
        }

        if (task.transferStateListener != null) {
            task.transferStateListener.onStateChanged(TransferState.FAILED);
        }
    }

    private void removeTransferSpecFromDb(TransferSpec transferSpec) {

        transferSpecDao.deleteTransferSpec(transferSpec.id);
    }


    public void schedule(TransferRequest transferRequest, COSXMLTask cosxmlTask, TransferWorkManager transferWorkManager) {

        executor.execute(() -> {

            TransferSpec existSpec =  getTransferSpecByTransferRequest(transferRequest);

            if (existSpec != null) { // 之前提交过相同的任务

                COSXMLTask existTask = cosxmlTasks.get(existSpec.id);


                /*
                 * 该任务通过 Worker 来上传
                 *
                 * App 被杀死后，如果 MonitorWorker 再次启动检测到 App 已经被杀死了
                 */
                if (!TextUtils.isEmpty(existSpec.workerId)) {

                    if (existSpec.state == TransferState.COMPLETED) { // 已经通过 Worker 上传完毕
                        invokeTransferTaskComplete(cosxmlTask);
                        removeTransferSpecFromDb(existSpec);
                        return;
                    } else {
                        cancelBackgroundWorker(existSpec.id, existSpec.workerId);
                    }
                }

                /*
                 * 该任务通过 task 来传输
                 *
                 * 在一个 App 运行周期内，调用了两次相同的任务
                 */
                if (existTask != null) {

                    if (existTask.taskState == TransferState.COMPLETED) {
                        invokeTransferTaskComplete(cosxmlTask);
                        removeTransferSpecFromDb(existSpec);
                    } else {
                        invokeTransferTaskFailed(cosxmlTask);
                    }
                } else { // 前一次提交任务和本次不在同一个 app 运行周期内
                    updateAndBindTransferTask(transferRequest, cosxmlTask, transferWorkManager, existSpec);
                }
            } else {
                insertAndBindTransferTask(transferRequest, cosxmlTask, transferWorkManager);
            }


        });
    }

    @Nullable private TransferSpec getTransferSpecByTransferRequest(TransferRequest transferRequest) {

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

        TransferSpec transferSpec = null;

        if (transferRequest instanceof UploadRequest) {
            transferSpec = new TransferSpec((UploadRequest) transferRequest);

        } else if (transferRequest instanceof DownloadRequest){
            transferSpec = new TransferSpec((DownloadRequest) transferRequest);
        }

        if (transferSpec != null) {

            transferSpec.setServiceEgg(transferWorkManager.toBytes());
            transferSpecDao.insertTransferSpec(transferSpec);

            transferSpecs.add(transferSpec);

            constraintsTracker.replace(transferSpecs);
        }
    }

    private void updateAndBindTransferTask(TransferRequest transferRequest, COSXMLTask cosxmlTask, TransferWorkManager transferWorkManager, TransferSpec transferSpec) {

        if (transferSpec != null) {
            cosxmlTasks.put(transferSpec.id, cosxmlTask);
            transferSpec.setServiceEgg(transferWorkManager.toBytes());
            transferSpecDao.insertTransferSpec(transferSpec);
            transferSpecs.add(transferSpec);
            constraintsTracker.replace(transferSpecs);
        }
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
            if (isTransferByTask(transferSpec)) {

                COSXMLTask cosxmlTask = cosxmlTasks.get(transferSpec.id);

                // 2
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.CONSTRAINED) {
                    cosxmlTask.constraintSatisfied();
                }

                // 1
                // if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.UNKNOWN) {
                if (cosxmlTask != null && transferSpec.state == TransferState.UNKNOWN) {
                    startTask(cosxmlTask, transferSpec.uploadId);
                }

                observeCosTask(cosxmlTask, transferSpec.id);
            }
        }

    }


    /**
     *
     * @param transferSpecIds
     */
    private void constraintsNotMetTransfers(@NonNull List<String> transferSpecIds) {

        List<TransferSpec> transferSpecs = transferSpecDao.getTransferSpecs(transferSpecIds);

        for (TransferSpec transferSpec : transferSpecs) {

            if (isTransferByTask(transferSpec) && transferSpec.state != TransferState.CONSTRAINED) {

                COSXMLTask cosxmlTask = cosxmlTasks.get(transferSpec.id);
                if (cosxmlTask != null && isTaskRunning(cosxmlTask.getTaskState())) {
                    cosxmlTask.constraintUnSatisfied();
                }
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

        cosxmlTask.setInternalStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                if (state != TransferState.COMPLETED) {
                    transferSpecDao.updateTransferSpecTransferStatus(transferSpecId, state);
                } else {
                    transferSpecDao.deleteTransferSpec(transferSpecId);
                }
            }
        });
    }


    private boolean isTaskRunning(TransferState state) {

        return state == TransferState.IN_PROGRESS
                || state == TransferState.WAITING
                || state == TransferState.RESUMED_WAITING;
    }


    private boolean isTransferByTask(@NonNull TransferSpec transferSpec) {

        return TextUtils.isEmpty(transferSpec.workerId);
    }

    private boolean isTransferByWorker(@NonNull TransferSpec transferSpec) {

        return !isTransferByTask(transferSpec);
    }

    private void cancelBackgroundWorker(String transferSpecId, String workerId) {

        WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workerId));
        transferSpecDao.updateTransferSpecWorkerId(transferSpecId, "");
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
