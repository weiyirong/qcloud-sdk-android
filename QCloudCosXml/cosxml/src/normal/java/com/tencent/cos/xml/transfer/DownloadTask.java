package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlService;

/**
 * Created by bradyxiao on 2018/8/23.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class DownloadTask extends Task {

    String region;
    String bucket;
    String cosPath;
    String localSaveDirPath;
    String localSaveFileName;
    DownloadListener downloadListener;

    public DownloadTask(String region, String bucket, String cosPath, String localSaveDirPath, String localSaveFileName, DownloadListener downloadListener){
        this.region = region;
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.localSaveDirPath = localSaveDirPath;
        this.localSaveFileName = localSaveFileName;
        this.downloadListener = downloadListener;
    }

    public void download(){}

    @Override
    protected void pause(CosXmlService cosXmlService) {

    }

    @Override
    protected void cancel(CosXmlService cosXmlService) {

    }

    @Override
    protected void resume(CosXmlService cosXmlService) {

    }
}
