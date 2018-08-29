package com.tencent.cos.xml.transfer;



import com.tencent.cos.xml.CosXmlService;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * Created by bradyxiao on 2018/8/22.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class TransferManager{

    private CosXmlService cosXmlService;
    private OnRemoveTaskListener onRemoveTaskListenerImpl = new OnRemoveTaskListener() {
        @Override
        public void onRemove(int id) {
            remove(id);
        }
    };

    /**
     * 1、异步操作、实时记录任务的状态
     * 2、封装了uploadTask, downloadTask, copyTask
     * 3、针对task Map保护措施，避免出现不安全现象
     * 4、最多一次性可以操作 3 个tasks
     */

    private Map<Integer, Task> taskMap;
    private Object SYNC_TASK = new Object();
    private int uploadTaskNum = 0;
    private int downloadTaskNum = 0;
    private int copyTaskNum = 0;
//    private BlockingQueue<>


    public TransferManager(CosXmlService cosXmlService){
        if(cosXmlService == null){
            throw new RuntimeException("CosXmlService must not be null");
        }
        this.cosXmlService = cosXmlService;
        taskMap = new HashMap<>();
    }


    /**
     * 只针对文件上传
     * @param bucket
     * @param cosPath
     * @param srcPath
     * @param uploadId
     */
    public int upload(String bucket, String cosPath, String srcPath, String uploadId, UploadListener uploadListener){
        return upload(null, bucket, cosPath, srcPath, uploadId, uploadListener);
    }

    public int upload(String region, String bucket, String cosPath, String srcPath, String uploadId, UploadListener uploadListener){
//        UploadTask uploadTask = new UploadTask(region, bucket, cosPath, srcPath, uploadId, uploadListener);
//        int id = UUID.randomUUID().hashCode();
//        uploadTask.setTaskId(id);
//        taskMap.put(id, uploadTask);
//        uploadTask.setOnRemoveTaskListenter(onRemoveTaskListenerImpl);
//        uploadTask.upload(cosXmlService);
        SliceUploadTask sliceUploadTask = new SliceUploadTask(region, bucket, cosPath, srcPath, uploadId, uploadListener);
        int id = UUID.randomUUID().hashCode();
        sliceUploadTask.setTaskId(id);
        taskMap.put(id, sliceUploadTask);
        sliceUploadTask.setOnRemoveTaskListenter(onRemoveTaskListenerImpl);
        sliceUploadTask.upload(cosXmlService);
        return id;
    }

    public int download(String bucket, String cosPath, String localSaveDirPath, DownloadListener downloadListener){
        return download(null, bucket, cosPath, localSaveDirPath, null, downloadListener);
    }
    public int download(String bucket, String cosPath, String localSaveDirPath, String localSaveFileName, DownloadListener downloadListener){
        return download(null, bucket, cosPath, localSaveDirPath, localSaveFileName, downloadListener);
    }

    public int download(String region, String bucket, String cosPath, String localSaveDirPath, String localSaveFileName, DownloadListener downloadListener){
        DownloadTask downloadTask = new DownloadTask(region, bucket, cosPath, localSaveDirPath, localSaveFileName, downloadListener);
        downloadTask.download();
        int id = UUID.randomUUID().hashCode();
        taskMap.put(id, downloadTask);
        return id;
    }


    /**
     * running
     * @param id
     */
    public void pause(int id){
        synchronized (SYNC_TASK){
            Task task = taskMap.get(id);
            if( task != null){
                task.pause(cosXmlService);
            }
        }
    }

    /**
     * running
     * @param id
     */
    public void cancel(int id){
        synchronized (SYNC_TASK){
            Task task = taskMap.get(id);
            if( task != null){
                task.cancel(cosXmlService);
            }
        }
    }

    /**
     * all
     *
     */
    public void resume(int id){
        synchronized (SYNC_TASK){
            Task task = taskMap.get(id);
            if( task != null){
                task.resume(cosXmlService);
            }
        }
    }

    private void remove(int id){
        synchronized (SYNC_TASK){
            if(taskMap.containsKey(id)){
                taskMap.remove(id);
            }
        }
    }

}
