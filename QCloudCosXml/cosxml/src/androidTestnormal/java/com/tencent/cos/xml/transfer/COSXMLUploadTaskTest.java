package com.tencent.cos.xml.transfer;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tencent.cos.xml.QServer.TAG;

/**
 * Created by bradyxiao on 2018/9/14.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class COSXMLUploadTaskTest {

    TransferManager transferManager;

    @Test
    public void testPause()throws Exception{
        QServer.init(InstrumentationRegistry.getContext());
        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().build());
        String cosPath = "uploadTask_pause" + System.currentTimeMillis();
        final String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
        final COSXMLUploadTask cosxmlUploadTask = transferManager.upload(QServer.bucketForObjectAPITest, cosPath, srcPath, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
                if((int)progress > 50){
                    cosxmlUploadTask.pause();
                }
            }
        });
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
                QServer.deleteLocalFile(srcPath);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
                QServer.deleteLocalFile(srcPath);
            }
        });
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        while (cosxmlUploadTask.getTaskState() != TransferState.PAUSED){
            Thread.sleep(100);
        }
    }

    @Test
    public void cancel() throws Exception{
        QServer.init(InstrumentationRegistry.getContext());
        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().build());
        String cosPath = "uploadTask_cancel" + System.currentTimeMillis();
        final String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
        final COSXMLUploadTask cosxmlUploadTask = transferManager.upload(QServer.bucketForObjectAPITest, cosPath, srcPath, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
                if((int)progress > 50){
                    cosxmlUploadTask.cancel();
                }
            }
        });
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
                QServer.deleteLocalFile(srcPath);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
                QServer.deleteLocalFile(srcPath);
            }
        });
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });

        while (cosxmlUploadTask.getTaskState() != TransferState.CANCELED){
            Thread.sleep(100);
        }
    }

    private volatile boolean isResume = false;

    @Test
    public void resume() throws Exception{
        QServer.init(InstrumentationRegistry.getContext());
        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder()
                .setDivisionForUpload(1024 * 1024).build());
        String cosPath = "uploadTask_resume" + System.currentTimeMillis();
        final String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 2 * 1024 * 1024);
        final COSXMLUploadTask cosxmlUploadTask = transferManager.upload(QServer.bucketForObjectAPITest, cosPath, srcPath, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
                if((int)progress > 50 && !isResume){
                    cosxmlUploadTask.pause();
                }
            }
        });
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
                QServer.deleteLocalFile(srcPath);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
               // QServer.deleteLocalFile(srcPath);
            }
        });
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });

        while (cosxmlUploadTask.getTaskState() != TransferState.PAUSED){
            Thread.sleep(100);
        }
        Thread.sleep(100);
        isResume = true;
        cosxmlUploadTask.resume();
        while (cosxmlUploadTask.getTaskState() != TransferState.COMPLETED){
            Thread.sleep(100);
        }
    }
}