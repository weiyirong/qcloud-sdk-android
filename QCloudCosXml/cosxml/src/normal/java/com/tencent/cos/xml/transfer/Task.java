package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlService;

/**
 * Created by bradyxiao on 2018/8/23.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public abstract class Task {
    String region;
    String bucket;
    String cosPath;

    String taskId;
    OnRemoveTaskListener onRemoveTaskListener;
    TransferState taskState  = TransferState.WAITING;

    protected void setOnRemoveTaskListenter(OnRemoveTaskListener removeTaskListenter){
        this.onRemoveTaskListener = removeTaskListenter;
    }

    protected void setTaskId(int id){
        this.taskId = String.valueOf(id);
    }

    protected abstract void pause(CosXmlService cosXmlService);

    protected abstract void cancel(CosXmlService cosXmlService);

    protected abstract void resume(CosXmlService cosXmlService);
}
