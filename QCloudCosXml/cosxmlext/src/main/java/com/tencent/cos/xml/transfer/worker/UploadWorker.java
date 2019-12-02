package com.tencent.cos.xml.transfer.worker;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.transfer.CosXmlBackgroundService;
import com.tencent.cos.xml.transfer.cos.CosXmlExtServiceHolder;
import com.tencent.cos.xml.transfer.request.WorkerUploadRequest;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.TaskExecutors;

/**
 * Created by rickenwang on 2019-11-18.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class UploadWorker extends ListenableWorker {

    private final String TAG = "UploadWorker";

    private UploadIdManager uploadIdManager;

    private String uploadId;

    private String workerId;

    private COSXMLUploadTask cosxmlUploadTask;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public UploadWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        uploadIdManager = UploadIdManager.getInstance();
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {


        return CallbackToFutureAdapter.getFuture(completer -> {

            Context context = getApplicationContext();

            QCloudLogger.i(TAG, "UploadWorker start work " + getId());

            Data inputData = getInputData();
            PutObjectRequest putObjectRequest = WorkerUploadRequest.fromWorkerData(inputData);

            if (putObjectRequest == null) {
                completer.set(Result.failure());
                return null;
            }

            CosXmlExtServiceHolder cosXmlExtServiceHolder = CosXmlExtServiceHolder.getInstance();
            CosXmlBackgroundService cosXmlExtService = cosXmlExtServiceHolder.getDefaultService();

            if (cosXmlExtService == null) {
                cosXmlExtService = WorkerUploadRequest.fromWorkerData(getApplicationContext(), inputData);
            }


            if (cosXmlExtService == null) {
                QCloudLogger.e(TAG, "Please init TransferExtManager first!");
                return Result.failure();
            }

            workerId = getId().toString();

            uploadId = uploadIdManager.loadUploadId(context, workerId);
            QCloudLogger.i(TAG, "load upload id " + uploadId);
            cosxmlUploadTask = cosXmlExtService.upload(putObjectRequest,
                    uploadId);

            cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    uploadIdManager.clearUploadId(context, workerId);
                    completer.set(Result.success());
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                    uploadIdManager.clearUploadId(context, workerId);
                    completer.setException(clientException == null ? serviceException : clientException);
                }
            });

            completer.addCancellationListener(cosxmlUploadTask::pause, TaskExecutors.COMMAND_EXECUTOR);

            cosxmlUploadTask.setCosXmlProgressListener((complete, target) -> {

                // QCloudLogger.i("UploadWorker-" + getId(), "complete is " + complete + ", target is " + target);
                if (TextUtils.isEmpty(uploadId)) {
                    uploadId = cosxmlUploadTask.getUploadId();
                    uploadIdManager.saveUploadId(getApplicationContext(), workerId, uploadId);
                }
            });

            return cosxmlUploadTask;
        });
    }

    @Override
    public void onStopped() {

        super.onStopped();
    }
}
