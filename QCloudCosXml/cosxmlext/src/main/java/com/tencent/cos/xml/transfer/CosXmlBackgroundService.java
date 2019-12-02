package com.tencent.cos.xml.transfer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.WorkManager;
import androidx.work.impl.constraints.ConstraintListener;
import androidx.work.impl.constraints.NetworkState;
import androidx.work.impl.constraints.trackers.NetworkStateTracker;
import androidx.work.impl.constraints.trackers.Trackers;

import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.transfer.constraint.Constraints;
import com.tencent.cos.xml.transfer.constraint.ConstraintsDao;
import com.tencent.cos.xml.transfer.constraint.ConstraintsDatabase;
import com.tencent.cos.xml.transfer.constraint.NetworkMonitor;
import com.tencent.cos.xml.transfer.cos.ParcelableCredentialProvider;
import com.tencent.cos.xml.transfer.utils.MapUtils;
import com.tencent.cos.xml.transfer.worker.DbExecutors;
import com.tencent.cos.xml.transfer.worker.MonitorWorker;
import com.tencent.cos.xml.transfer.worker.TransferDatabase;
import com.tencent.cos.xml.transfer.worker.TransferSpec;
import com.tencent.cos.xml.transfer.worker.TransferSpecDao;
import com.tencent.qcloud.core.task.TaskExecutors;

import java.util.List;
import java.util.UUID;

/**
 * Created by rickenwang on 2019-11-18.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class CosXmlBackgroundService extends TransferManager {

    private static boolean isServiceStarted = false;

    private BackgroundConfig backgroundConfig;

    private TransferSpecDao transferSpecDao;

    private ConstraintsDao constraintsDao;

    private Context context;

    private CosXmlServiceConfig cosXmlServiceConfig;

    private ParcelableCredentialProvider credentialProvider;

    private byte[] serviceEgg;

    private Handler mainHandler;

    private NetworkMonitor networkMonitor;

    public CosXmlBackgroundService(Context context, CosXmlServiceConfig cosXmlServiceConfig, BackgroundConfig backgroundConfig, ParcelableCredentialProvider credentialProvider) {
        super(new CosXmlSimpleService(context, cosXmlServiceConfig, credentialProvider), backgroundConfig);
        this.backgroundConfig = backgroundConfig;
        isServiceStarted = true;
        this.context = context.getApplicationContext();
        this.cosXmlServiceConfig = cosXmlServiceConfig;
        this.credentialProvider = credentialProvider;

        transferSpecDao = TransferDatabase.create(this.context).transferSpecDao();
        constraintsDao = ConstraintsDatabase.create(this.context).constraintsDao();

        mainHandler = new Handler(Looper.getMainLooper());

        networkMonitor = NetworkMonitor.getDefault(context);

        MonitorWorker.startMonitor(context);

        saveToServiceEgg();



    }

    @Override
    public COSXMLUploadTask upload(String bucket, String cosPath, String srcPath, String uploadId, COSXMLTask.OnSignatureListener onSignatureListener) {

        COSXMLUploadTask cosxmlUploadTask = new COSXMLUploadTask(cosXmlService, null, bucket, cosPath, srcPath, uploadId);
        cosxmlUploadTask.multiUploadSizeDivision = transferConfig.divisionForUpload; // 分片上传的界限
        cosxmlUploadTask.sliceSize = transferConfig.sliceSizeForUpload; // 分片上传的分片大小
        cosxmlUploadTask.setOnSignatureListener(onSignatureListener);
        startTaskWithConstrains(cosxmlUploadTask, backgroundConfig.getConstraints());

        return cosxmlUploadTask;
    }

    @Override
    public COSXMLUploadTask upload(String bucket, String cosPath, String srcPath, String uploadId) {
        return this.upload(bucket, cosPath, srcPath, uploadId, null);
    }

    private void startTaskWithConstrains(COSXMLUploadTask task, Constraints constraints) {

        DbExecutors.DB_EXECUTOR.execute(() -> {

            TransferSpec transferSpec = transferSpecDao.getTransferSpec(task.bucket, task.cosPath, task.srcPath);

            if (transferSpec != null) {

                if (serviceEgg != null) {
                    transferSpec.setServiceEgg(serviceEgg);
                }

                if (!TextUtils.isEmpty(task.getUploadId())) {
                    transferSpec.setUploadId(task.getUploadId());
                }

                // 该任务已经通过 WorkManager 上传完毕了
                if (transferSpec.isCompleteBackground()) {

                    PutObjectResult putObjectResult = new PutObjectResult();
                    putObjectResult.eTag = transferSpec.getEtag();
                    task.updateState(TransferState.COMPLETED, null, putObjectResult, false);
                    transferSpecDao.delete(transferSpec.getWorkerId());
                    return;

                } else {

                    String workerId = transferSpec.getWorkerId();
                    if (!TextUtils.isEmpty(workerId)) {
                        WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workerId));
                    }
                    transferSpecDao.setWorkerId(transferSpec.id, "");
                }

            } else {

                TransferSpec newTransferSpec = new TransferSpec(
                        cosXmlServiceConfig.getRegion(), task.bucket, task.cosPath, task.srcPath, MapUtils.map2Json(task.headers), true);
                if (serviceEgg != null) {
                    newTransferSpec.setServiceEgg(serviceEgg);
                }
                transferSpecDao.insertTransferSpec(newTransferSpec);
            }

            startTaskWithConstraints(task, constraints);
        });
    }

    private void startTaskWithConstraints(COSXMLUploadTask task, Constraints constraints) {

        mainHandler.post(() -> {


            LiveData<Constraints> constraintsLiveData = constraintsDao.getNetworkConstraintsLiveData();
            constraintsLiveData.observeForever(new Observer<Constraints>() {
                @Override
                public void onChanged(Constraints dbConstrains) {

//                    if (dbConstrainsList == null || dbConstrainsList.isEmpty()) {
////                        return;
////                    }
////
////                    Constraints dbConstrains = dbConstrainsList.get(0);

                    if (dbConstrains == null) {
                        return;
                    }

                    if (dbConstrains.isSatisfyNetwork(constraints.getNetworkType())) {
                        tryToStartTask(task);
                    } else if (!dbConstrains.isSatisfyNetwork(constraints.getNetworkType())
                            && task.getTaskState() == TransferState.IN_PROGRESS) {
                        tryToPauseTask(task);
                    }
                }
            });
        });
    }

    private void tryToStartTask(COSXMLTask task) {

        if (task.getTaskState() == TransferState.RESUMED_WAITING
                || task.getTaskState() == TransferState.WAITING
                || task.getTaskState() == TransferState.PAUSED) {

            if (task instanceof COSXMLUploadTask) {
                ((COSXMLUploadTask) task).upload();
            } else if (task instanceof COSXMLDownloadTask) {
                ((COSXMLDownloadTask) task).download();
            }
        }
    }

    private void tryToPauseTask(COSXMLTask task) {

        if (task.getTaskState() == TransferState.RESUMED_WAITING
                || task.getTaskState() == TransferState.IN_PROGRESS
                || task.getTaskState() == TransferState.WAITING) {
            task.pause();
        }
    }


    public static boolean isIsServiceStarted() {
        return isServiceStarted;
    }


    //    private Context context;
//
//    private CosXmlServiceConfig cosXmlServiceConfig;
//
//    private TransferConfig transferConfig;
//
//    private ParcelableCredentialProvider credentialProvider;
//
//    private CosXmlService cosXmlService;
//
//    private TransferManager transferManager;
//
//    private byte[] serviceEgg;
//
//
//    public CosXmlBackgroundService(Context context, CosXmlServiceConfig cosXmlServiceConfig, TransferConfig transferConfig,
//                                   ParcelableCredentialProvider credentialProvider) {
//
//        this.context = context;
//        this.cosXmlServiceConfig = cosXmlServiceConfig;
//        this.transferConfig = transferConfig;
//        this.credentialProvider = credentialProvider;
//
//        cosXmlService = new CosXmlService(context, cosXmlServiceConfig, credentialProvider);
//        transferManager = new TransferManager(cosXmlService, transferConfig);
//
//        saveToServiceEgg();
//        CosXmlExtServiceHolder.getInstance().setDefaultService(this);
//    }
//
//    public UUID upload(WorkerUploadRequest uploadRequest) {
//
//        if (serviceEgg != null) {
//            uploadRequest.setCosServiceEgg(serviceEgg);
//        }
//
//        WorkRequest.Builder builder = uploadRequest.isPeriod() ?
//                new PeriodicWorkRequest.Builder(UploadWorker.class, uploadRequest.getInterval(), uploadRequest.getIntervalTimeUnit()) :
//                new OneTimeWorkRequest.Builder(UploadWorker.class);
//
//        builder.setInputData(uploadRequest.toWorkerData());
//
//        if (uploadRequest.getBackoffPolicy() != null) {
//            builder.setBackoffCriteria(uploadRequest.getBackoffPolicy(), uploadRequest.getBackOffDelay(),
//                    uploadRequest.getBackOffDelayTimeUnit());
//        }
//
//        if (!TextUtils.isEmpty(uploadRequest.getTag())) {
//            builder.addTag(uploadRequest.getTag());
//        }
//
//        if (uploadRequest.getConstraints() != null) {
//            builder.setConstraints(uploadRequest.getConstraints());
//        }
//
//        if (uploadRequest.getDelay() != 0) {
//            builder.setInitialDelay(uploadRequest.getDelay(), uploadRequest.getDelayTimeUnit());
//        }
//
//        WorkRequest workRequest = builder.build();
//
//        WorkManager.getInstance(context).enqueue(workRequest);
//        return workRequest.getId();
//    }
//
//    public UUID download(WorkerDownloadRequest downloadRequest) {
//
//        throw new RuntimeException("Not Support");
//    }
//
    private void saveToServiceEgg() {

        Parcel parcel = Parcel.obtain();

        // parcel.writeString(credentialProvider.getClass().getName());
        parcel.writeParcelable(cosXmlServiceConfig, 0);
        parcel.writeParcelable((BackgroundConfig) transferConfig, 0);
        parcel.writeParcelable(credentialProvider, 0);

        serviceEgg = parcel.marshall();
        parcel.recycle();

    }
//
//
//    public CosXmlService getCosXmlService() {
//        return cosXmlService;
//    }
//
//    public TransferManager getTransferManager() {
//        return transferManager;
//    }
}
