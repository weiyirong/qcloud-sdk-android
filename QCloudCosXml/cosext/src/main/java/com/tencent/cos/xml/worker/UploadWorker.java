package com.tencent.cos.xml.worker;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.constraints.TransferDatabase;
import com.tencent.cos.xml.constraints.TransferSpecDao;
import com.tencent.cos.xml.transfer.ParcelableCredentialProvider;
import com.tencent.cos.xml.transfer.TransferStatus;
import com.tencent.cos.xml.transfer.TransferWorkManager;
import com.tencent.cos.xml.transfer.WorkTransferConfig;
import com.tencent.qcloud.core.logger.QCloudLogger;

/**
 * Created by rickenwang on 2019-11-18.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class UploadWorker extends ListenableWorker {

    private final String TAG = "UploadWorker";

    private TransferSpecDao transferSpecDao;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public UploadWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        transferSpecDao = TransferDatabase.create(getApplicationContext()).transferSpecDao();
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {


        return CallbackToFutureAdapter.getFuture(completer -> {

            Context context = getApplicationContext();

            QCloudLogger.i(TAG, "UploadWorker start work " + getId());

            Data inputData = getInputData();
            String transferSpecId = inputData.getString("id");

            if (TextUtils.isEmpty(transferSpecId)) {
                completer.set(Result.failure());
                return null;
            }

            transferSpecDao.getTransferSpecsLiveData(transferSpecId).observeForever(transferSpec -> {

                if (transferSpec == null) {
                    completer.set(Result.failure());
                    return;
                }

                TransferWorkManager transferWorkManager = fromServiceEgg(context, transferSpec.getServiceEgg());
                if (transferWorkManager == null) {
                    QCloudLogger.e(TAG, "Please init TransferExtManager first!");
                    completer.set(Result.failure());
                    return;
                }

                transferWorkManager.upload(transferSpec.bucket, transferSpec.key, transferSpec.filePath);

                transferSpecDao.getTransferSpecsStateLiveData(transferSpecId).observeForever(state -> {

                    if (state == TransferStatus.FAILED || state == TransferStatus.CANCELED) {
                        completer.set(Result.failure());
                    } else if (state == TransferStatus.COMPLETED) {
                        completer.set(Result.success());
                    }
                });
            });
            return new Object();
        });
    }

    @Override
    public void onStopped() {

        super.onStopped();
    }

    @Nullable
    public static TransferWorkManager fromServiceEgg(Context context, byte[] egg) {

        if (egg != null) {

            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(egg, 0, egg.length);
            parcel.setDataPosition(0);

            CosXmlServiceConfig cosXmlServiceConfig = parcel.readParcelable(UploadWorker.class.getClassLoader());
            WorkTransferConfig transferConfig = parcel.readParcelable(UploadWorker.class.getClassLoader());
            ParcelableCredentialProvider credentialProvider = parcel.readParcelable(UploadWorker.class.getClassLoader());

            parcel.recycle();
            return new TransferWorkManager(context, cosXmlServiceConfig, transferConfig, credentialProvider);
        }
        return null;
    }

}
