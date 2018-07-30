package com.tencent.cos.xml.transfer;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link TransferUtility} is a high-level class for application to upload local files to COS. Here is the usage:
 *
 * <pre>
 * // Initializes TransferUtility
 * TransferUtility transferUtility = new TransferUtility.Builder()
 *      .context(this)
 *      .cosService((CosXmlSimpleService) QServer.cosXml)
 *      .build();
 * // Start a upload
 * TransferObserver transferObserver = transferUtility.upload(&quot;bucketName&quot;, &quot;objectName&quot;, &quot;srcPath&quot;);
 * transferObserver.setTransferListener(new TransferListener() {
 *     public void onStateChanged(int id, String newState) {
 *         // Do something in the callback.
 *     }
 *
 *     public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
 *         // Do something in the callback.
 *     }
 *
 *     public void onError(int id, Exception e) {
 *         // Do something in the callback.
 *     }
 * });
 * </pre>
 *

 * For pausing and resuming tasks:
 *
 * <pre>
 * // Gets id of the transfer.
 * String id = transferObserver.getTransferId();
 *
 * // Pauses the transfer.
 * transferUtility.pause(id);
 *
 * // Resumes the transfer.
 * transferUtility.resume(id);
 * </pre>
 *
 * For canceling transfer:
 * <pre>
 * // Cancels the transfer.
 * transferUtility.cancel(id);
 * </pre>
 * <p>
 * Created by rickenwang on 2018/5/15.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

@Deprecated
public class TransferUtility {

    /**
     * common log tag of transfer files by TransferUtility.
     */
    static final String TRANSFER_UTILITY_TAG = "TransferUtility";

    static final int MB = 1024 * 1024;

    /**
     * Default part size for upload parts each time.
     */
    static final int DEFAULT_UPLOAD_PART_SIZE = 2 * MB;


    private Map<String, TransferRunnable> tasks;

    /**
     * actually COS client of TransferUtility
     */
    private final CosXmlSimpleService cosService;


    private final Context appContext;

    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    private static final int KEEP_ALIVE_TIME = 1;

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;


    private NetworkMonitor networkMonitor;

    private ThreadPoolExecutor transferExecutor;

    private TransferStatusManager transferStatusManager;

    private TransferUtility(Context context, CosXmlSimpleService cosService) {

        this.appContext = context;
        this.cosService = cosService;
        tasks = new HashMap<>();

        final BlockingQueue<Runnable> transferQueue;

        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        transferQueue = new LinkedBlockingQueue<Runnable>();

        transferExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                transferQueue);

        networkMonitor = new NetworkMonitor();
        networkMonitor.addNetworkListener(new NetworkMonitor.NetworkListener() {
            @Override
            public void onReconnect() {
                onNetworkReconnect();
            }

            @Override
            public void onDisconnect() {
                onNetworkDisconnect();
            }
        });
        networkMonitor.register(appContext);

        transferStatusManager = new TransferStatusManager(this);
    }

    /**
     * If the transfer task is waiting for network, resume it.
     */
    private void onNetworkReconnect() {

        String[] taskIdArray = new String[tasks.size()];
        tasks.keySet().toArray(taskIdArray);

        for (int i = 0; i < taskIdArray.length; i++) {

            if (transferStatusManager.getState(taskIdArray[i]) == TransferState.WAITING_FOR_NETWORK) {
                resume(taskIdArray[i]);
            }
        }
    }

    /**
     * If the transfer task is in progress, pause it.
     */
    private void onNetworkDisconnect() {

        String[] taskIdArray = new String[tasks.size()];
        tasks.keySet().toArray(taskIdArray);

        for (int i = 0; i < taskIdArray.length; i++) {

            if (transferStatusManager.getState(taskIdArray[i]) == TransferState.IN_PROGRESS) {
                pause(tasks.get(taskIdArray[i]));
                transferStatusManager.updateState(taskIdArray[i], TransferState.WAITING_FOR_NETWORK);
            }
        }
    }

    /**
     * Start upload a file to COS.
     *
     * @param bucket The name of the bucket containing the object to upload.
     * @param object The key which the file to store.
     * @param srcPath The local file path which you want to upload to cos.
     * @return A TransferObserver used to track upload progress and state.
     */
    @Deprecated
    public TransferObserver upload(String bucket, String object, String srcPath) {

        UploadService.ResumeData resumeData = new UploadService.ResumeData();
        resumeData.sliceSize = DEFAULT_UPLOAD_PART_SIZE;
        resumeData.srcPath = srcPath;
        resumeData.cosPath = object;
        resumeData.bucket = bucket;

        UploadService uploadService = new UploadService(cosService, resumeData);

        String taskId = getUUID();

        TransferRunnable transferRunnable = new TransferRunnable(uploadService, taskId, transferStatusManager);
        tasks.put(taskId, transferRunnable);
        QCloudLogger.i(TRANSFER_UTILITY_TAG, "add upload task(" + taskId + ").");

        transferStatusManager.updateState(taskId, TransferState.IN_PROGRESS);
        transferExecutor.execute(transferRunnable);

        return transferRunnable.getTransferObserver();
    }


    @Deprecated
    public boolean pause(String id) {

        TransferRunnable task = tasks.get(id);
        if (task == null) {
            QCloudLogger.w(TRANSFER_UTILITY_TAG, "The task("+ id + ") you want to pause is not exist!");
            return false;
        } else {
            QCloudLogger.i(TRANSFER_UTILITY_TAG, "Pause the task("+ id + ")");
        }
        /**
         * The pause(String) method must be call initiative by user, so the transfer status must be PAUSE instead of WAITING_NETWORK.
         * Should distinguish the difference of trigger by network disconnect.
         */
        transferStatusManager.updateState(id, TransferState.PAUSED);

        return pause(task);
    }

    private boolean pause(@NonNull TransferRunnable task) {

        task.pause();
        return true;
    }

    /**
     * resume the transfer task by specify id, you must call {@link TransferUtility#pause(String)} first.
     *
     * @param id transfer id
     * @return
     */
    @Deprecated
    public boolean resume(String id) {

        TransferRunnable task = tasks.get(id);
        if (task == null) {
            QCloudLogger.w(TRANSFER_UTILITY_TAG, "The task("+ id + ") you want to resume is not exist!");
            return false;
        } else {
            QCloudLogger.i(TRANSFER_UTILITY_TAG, "Resume task("+ id + ")");
        }

        return resume(task);
    }

    private boolean resume(@NonNull TransferRunnable task) {
        /**
         * after call resume method, the transfer status must be IN_PROGRESS.
         */
        transferStatusManager.updateState(task.getTransferObserver().getTransferId(), TransferState.IN_PROGRESS);

        transferExecutor.execute(task);
        return true;
    }

    /**
     * cancel the transfer task by specify id .
     *
     * @param id transfer task id
     * @return the transfer task whether exist.
     */
    @Deprecated
    public boolean cancel(String id) {

        TransferRunnable task = tasks.get(id);
        if (task == null) {
            QCloudLogger.w(TRANSFER_UTILITY_TAG, "The task("+ id + ") you want to cancel is not exist!");
            return false;
        } else {
            QCloudLogger.i(TRANSFER_UTILITY_TAG, "Cancel the task("+ id + ")");
        }

        return cancel(task);
    }

    private boolean cancel(TransferRunnable task) {

        /**
         * update transfer status to CANCEL, and remove it from transferStatusManger.
         */
        transferStatusManager.updateState(task.getTransferObserver().getTransferId(), TransferState.CANCELED);

        task.cancel();
        tasks.remove(task.getTransferObserver().getTransferId());
        return true;
    }

    /**
     * cancel all transfer task which have not complete.
     *
     */
    @Deprecated
    public void cancelAll() {

        TransferRunnable[] taskArray = new TransferRunnable[tasks.size()];
        tasks.values().toArray(taskArray);

        for (int i = 0; i < taskArray.length; i++) {

            cancel(taskArray[i]);
        }
    }

    /**
     * release this TransferUtility instance when your don't want to use it.
     * once you have call this method, this TransferUtility instance would be unusable.
     *
     */
    @Deprecated
    public void release() {

        cancelAll();
        networkMonitor.unregister(appContext);
        transferExecutor.shutdown();
    }

    TransferRunnable getTransferRunnable(String id) {

        return tasks.get(id);
    }

    @Deprecated
    public static class Builder {

        private CosXmlSimpleService cosService;
        private Context appContext;



        public Builder cosService(CosXmlSimpleService cosXmlSimpleService) {
            cosService = cosXmlSimpleService;
            return this;
        }

        public Builder context(Context appContext) {
            this.appContext = appContext.getApplicationContext();
            return this;
        }

        public TransferUtility build() {

            return new TransferUtility(appContext, cosService);
        }
    }

    private String getUUID() {

        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
