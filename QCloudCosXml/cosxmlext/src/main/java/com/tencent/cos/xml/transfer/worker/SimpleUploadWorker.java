package com.tencent.cos.xml.transfer.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.transfer.CosXmlBackgroundService;
import com.tencent.cos.xml.transfer.cos.CosXmlExtServiceHolder;
import com.tencent.cos.xml.transfer.request.WorkerUploadRequest;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.qcloud.core.logger.QCloudLogger;

/**
 *
 * Created by rickenwang on 2019-11-19.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class SimpleUploadWorker extends Worker {

    public SimpleUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        QCloudLogger.i("SimpleUploadWorker", "SimpleUploadWorker start work");

        Data inputData = getInputData();
        PutObjectRequest putObjectRequest = WorkerUploadRequest.fromWorkerData(inputData);

        CosXmlExtServiceHolder cosXmlExtServiceHolder = CosXmlExtServiceHolder.getInstance();
        CosXmlBackgroundService cosXmlExtService = cosXmlExtServiceHolder.getDefaultService();

        if (cosXmlExtService == null) {
            cosXmlExtService = WorkerUploadRequest.fromWorkerData(getApplicationContext(), inputData);
        }


        if (cosXmlExtService == null) {
            QCloudLogger.e("SimpleUploadWorker", "Please init TransferExtManager first!");
            return Result.failure();
        }

        try {
            PutObjectResult putObjectResult = cosXmlExtService.getCosXmlService().putObject(putObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
            return Result.failure();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }
}
