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
import com.tencent.cos.xml.model.object.PutObjectRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tencent.cos.xml.QServer.TAG;

/**
 * Created by bradyxiao on 2018/9/14.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class COSXMLDownloadTaskTest {

    TransferManager transferManager;

    @Before
    public void init() throws Exception{
        QServer.init(InstrumentationRegistry.getContext());
        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().build());
    }

    @Test
    public void pause() throws Exception{
        String cosPath = "ip1.txt";
        String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.persistBucket, cosPath, srcPath);
        QServer.cosXml.putObject(putObjectRequest);
        QServer.deleteLocalFile(srcPath);
        final String localDir = InstrumentationRegistry.getContext().getExternalCacheDir().getPath();
        final String localFileName = cosPath;
        final COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(InstrumentationRegistry.getContext(), QServer.persistBucket, cosPath, localDir, localFileName);
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
                if((int)progress > 50){
                    cosxmlDownloadTask.pause();
                }
            }
        });
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        while (cosxmlDownloadTask.getTaskState() != TransferState.PAUSED){
            Thread.sleep(100);
        }
    }

    @Test
    public void cancel() throws Exception{
        String cosPath = "objecttest.txt";
        String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.persistBucket, cosPath, srcPath);
        QServer.cosXml.putObject(putObjectRequest);
        QServer.deleteLocalFile(srcPath);
        final String localDir = InstrumentationRegistry.getContext().getExternalCacheDir().getPath();
        final String localFileName = cosPath;
        final COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(InstrumentationRegistry.getContext(), QServer.persistBucket, cosPath, localDir, localFileName);
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
                if((int)progress > 50){
                    cosxmlDownloadTask.cancel();
                }
            }
        });
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        while (cosxmlDownloadTask.getTaskState() != TransferState.CANCELED){
            Thread.sleep(100);
        }
    }

    private volatile boolean isResume = false;
    @Test
    public void resume() throws Exception{
        String cosPath = "ip2.txt";
        String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.persistBucket, cosPath, srcPath);
        QServer.cosXml.putObject(putObjectRequest);
        QServer.deleteLocalFile(srcPath);
        final String localDir = InstrumentationRegistry.getContext().getExternalCacheDir().getPath();
        final String localFileName = cosPath;
        final COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(InstrumentationRegistry.getContext(), QServer.persistBucket, cosPath, localDir, localFileName);
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
                if((int)progress > 50 && !isResume){
                    cosxmlDownloadTask.pause();
                }
            }
        });
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });

        while (cosxmlDownloadTask.getTaskState() != TransferState.PAUSED){
            Thread.sleep(100);
        }
        Thread.sleep(100);
//        isResume = true;
//        cosxmlDownloadTask.resume();
//        while (cosxmlDownloadTask.getTaskState() != TransferState.COMPLETED ||
//                cosxmlDownloadTask.getTaskState() != TransferState.FAILED){
//            Thread.sleep(100);
//        }
    }


    @Test
    public void failed() throws Exception{
        String cosPath = "objecttest2xxx.txt";
        final String localDir = InstrumentationRegistry.getContext().getExternalCacheDir().getPath();
        final String localFileName = cosPath;
        final COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(InstrumentationRegistry.getContext(), QServer.persistBucket, cosPath, localDir, localFileName);
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
//                if((int)progress > 50){
//                    cosxmlDownloadTask.cancel();
//                }
            }
        });
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        while (cosxmlDownloadTask.getTaskState() != TransferState.FAILED){
            Thread.sleep(100);
        }
    }
}