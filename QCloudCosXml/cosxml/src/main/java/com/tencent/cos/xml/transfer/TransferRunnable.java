package com.tencent.cos.xml.transfer;

import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.qcloud.core.logger.QCloudLogger;

/**
 * this is a async task for upload objects, and it will pause/resume the task when the network is disconnect/reconnect.
 *
 * Created by rickenwang on 2018/5/15.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public class TransferRunnable implements Runnable {


    private TransferObserver transferObserver;

    private String id;

    private CosXmlProgressListener progressListener;

    private UploadService.ResumeData resumeData;

    private UploadService uploadService;

    private TransferStatusManager transferStatusManager;

    private CosXmlClientException clientException;

    private CosXmlServiceException serviceException;

    TransferRunnable(UploadService uploadService, final String id, TransferStatusManager transferStatusManager) {

        this.uploadService = uploadService;

        this.id = id;

        transferObserver = new TransferObserver(id);
        progressListener = new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {

                notifyTransferProgress(complete, target);
            }
        };
        uploadService.setProgressListener(progressListener);
        this.transferStatusManager = transferStatusManager;
    }

    @Override
    public void run() {

        if (Thread.interrupted()) {
            return;
        }

        clientException = null;
        serviceException = null;

        try {
            if (resumeData != null) {
                uploadService.init(resumeData);
            }
            uploadService.upload();
        } catch (CosXmlClientException e) {

            clientException = e;

            if (!isDetectNetworkDisconnectCauseClientException(e) &&
                    !isInitiativePauseCauseClientException(e) &&
                    !isInitiativeCancelCauseClientException(e)) {

                notifyTransferFailed();
                transferStatusManager.updateState(id, TransferState.FAILED);
            }

        } catch (CosXmlServiceException e) {

            serviceException = e;
            notifyTransferFailed();
            transferStatusManager.updateState(id, TransferState.FAILED);
        }

        /**
         * The transfer task is success,
         */
        if (serviceException == null && clientException == null) {

            transferStatusManager.updateState(id, TransferState.COMPLETED);
        }

    }

    /**
     * pause the transfer task.
     */
    void pause() {

        resumeData = uploadService.pause();
    }


    void cancel() {

        uploadService.abort(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {

                QCloudLogger.i(TransferUtility.TRANSFER_UTILITY_TAG, "cancel task("+id+") success");
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {

                /**
                 * cancel task failed, it shouldn't take place in general.
                 */
                QCloudLogger.w(TransferUtility.TRANSFER_UTILITY_TAG, "cancel task("+id+") failed");
            }
        });
    }

    TransferObserver getTransferObserver() {
        return transferObserver;
    }


    private void notifyTransferFailed() {

        if (transferObserver != null && transferObserver.getTransferListener() != null) {

            transferObserver.getTransferListener().onError(id, clientException, serviceException);
        }
    }

    void notifyTransferStateChange(TransferState state) {

        if (transferObserver != null && transferObserver.getTransferListener() != null) {

            transferObserver.getTransferListener().onStateChanged(id, state);
        }
    }

    private void notifyTransferProgress(long progress, long total) {

        if (transferObserver != null && transferObserver.getTransferListener() != null) {

            transferObserver.getTransferListener().onProgressChanged(id, progress, total);
        }
    }

    /**
     * If the ClientException throws because of sdk detect network disconnect and call pause() method, it return true.
     * If sdk didn't initiative detect network disconnect and ClientException throws by UnknownHostException or TimeoutException, it return false.
     *
     *
     * @param clientException ClientException
     * @return whether the ClientException throws because of detect network disconnect.
     */
    private boolean isDetectNetworkDisconnectCauseClientException(CosXmlClientException clientException) {

        return transferObserver.getTransferState()  == TransferState.WAITING_FOR_NETWORK && // network disconnect
                clientException != null && clientException.getMessage().toLowerCase().contains("request is cancelled by manual pause"); //
    }


    private boolean isInitiativePauseCauseClientException(CosXmlClientException clientException) {

        return transferObserver.getTransferState()  == TransferState.PAUSED && // network disconnect
                clientException != null && clientException.getMessage().toLowerCase().contains("request is cancelled by manual pause"); //

    }

    private boolean isInitiativeCancelCauseClientException(CosXmlClientException clientException) {

        return transferObserver.getTransferState() == TransferState.CANCELED && // network disconnect
                clientException != null && clientException.getMessage().toLowerCase().contains("request is cancelled by abort request"); //

    }


}
