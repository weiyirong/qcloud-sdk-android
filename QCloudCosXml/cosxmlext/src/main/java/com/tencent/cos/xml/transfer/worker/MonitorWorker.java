package com.tencent.cos.xml.transfer.worker;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tencent.cos.xml.transfer.CosXmlBackgroundService;
import com.tencent.cos.xml.transfer.constraint.NetworkType;
import com.tencent.cos.xml.transfer.request.WorkerUploadRequest;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class MonitorWorker extends Worker {

    private String TAG = "MonitorWorker";

    private TransferSpecDao transferSpecDao;

    public MonitorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        transferSpecDao = TransferDatabase.create(getApplicationContext()).transferSpecDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        QCloudLogger.i(TAG, "monitor worker start!");

        Context context = getApplicationContext();

        if (CosXmlBackgroundService.isIsServiceStarted()) {
            MonitorWorker.start(context, 5);
            QCloudLogger.i(TAG, "service is started, monitor worker start delay!");
        } else {

            QCloudLogger.i(TAG, "service is not started, monitor worker start transfer worker!");
            // 将所有
            List<TransferSpec> transferSpecs = transferSpecDao.getAllTransferSpecs();
            for (TransferSpec transferSpec : transferSpecs) {

                if (TextUtils.isEmpty(transferSpec.workerId)) {
                    String workerId = startUploadWorker(context, transferSpec);
                    transferSpecDao.setWorkerId(transferSpec.id, workerId);
                    QCloudLogger.i(TAG, "start transfer worker with id " + transferSpec.id);
                }
            }

        }

        return Result.success();
    }


    public static void startMonitor(Context context) {

        start(context, 0);
    }

    private static void start(Context context, int delayInSecond) {

        OneTimeWorkRequest monitorRequest = new OneTimeWorkRequest.Builder(MonitorWorker.class)
                .setInitialDelay(delayInSecond, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(context).enqueue(monitorRequest);
    }


    private static String startUploadWorker(Context context, TransferSpec transferSpec) {

        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(UploadWorker.class);
        Constraints.Builder constraintBuilder = new Constraints.Builder();

        if (transferSpec.constraints.getNetworkType() == NetworkType.WIFI) {
            constraintBuilder.setRequiredNetworkType(androidx.work.NetworkType.UNMETERED);
        }
        builder.setConstraints(constraintBuilder.build());
        WorkerUploadRequest workerUploadRequest = new WorkerUploadRequest(transferSpec.region, transferSpec.bucket,
                transferSpec.key, transferSpec.filePath);
        workerUploadRequest.setCosServiceEgg(transferSpec.getServiceEgg());
        WorkRequest workRequest = builder
                .setInputData(workerUploadRequest.toWorkerData())
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);

        return workRequest.getId().toString();
    }



}
