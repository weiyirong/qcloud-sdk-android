package com.tencent.cos.xml.transfer;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.cos.xml.QServer.TAG;
import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/9/26.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class COSXMLCopyTaskTest {

    TransferManager transferManager;
    @Before
    public void init() throws Exception{
        QServer.init(InstrumentationRegistry.getContext());
    }

    @Test
    public void pause() throws Exception{
        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().
                setDividsionForCopy(20*1024 * 1024).setSliceSizeForCopy(1024*1024).build());
        String cosPath = "upload_rn.copy";
        String sourceCosPath = "upload_rn";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                QServer.appid, QServer.bucketForObject, QServer.region, sourceCosPath);
        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.bucketForObject, cosPath, copySourceStruct);
        cosxmlCopyTask.setCosXmlProgressListener(null);
        cosxmlCopyTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        cosxmlCopyTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });

        while (cosxmlCopyTask.getTaskState() != TransferState.IN_PROGRESS){
            Thread.sleep(100);
        }
        cosxmlCopyTask.pause();
        while (cosxmlCopyTask.getTaskState() != TransferState.PAUSED){
            Thread.sleep(100);
        }
    }

    @Test
    public void cancel() throws Exception{
        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().
                setDividsionForCopy(20*1024 * 1024).setSliceSizeForCopy(1024*1024).build());
        String cosPath = "upload_rn.copy";
        String sourceCosPath = "upload_rn";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                QServer.appid, QServer.bucketForObject, QServer.region, sourceCosPath);
        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.bucketForObject, cosPath, copySourceStruct);
        cosxmlCopyTask.setCosXmlProgressListener(null);
        cosxmlCopyTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        cosxmlCopyTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });

        while (cosxmlCopyTask.getTaskState() != TransferState.IN_PROGRESS){
            Thread.sleep(100);
        }
        cosxmlCopyTask.cancel();
        while (cosxmlCopyTask.getTaskState() != TransferState.CANCELED){
            Thread.sleep(100);
        }
    }

    @Test
    public void resume() throws Exception{

        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().
                setDividsionForCopy(1024 * 1024).setSliceSizeForCopy(1024*1024).build());
        String cosPath = "upload_rn.copy";
        String sourceCosPath = "upload_rn";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                QServer.appid, QServer.bucketForObject, QServer.region, sourceCosPath);
        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.bucketForObject, cosPath, copySourceStruct);
        cosxmlCopyTask.setCosXmlProgressListener(null);
        cosxmlCopyTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d(TAG,  state.name());
            }
        });
        cosxmlCopyTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
            }
        });

        while (cosxmlCopyTask.getTaskState() != TransferState.IN_PROGRESS){
            Thread.sleep(100);
        }
        cosxmlCopyTask.pause();
        Thread.sleep(100);
        cosxmlCopyTask.resume();
        while (cosxmlCopyTask.getTaskState() != TransferState.COMPLETED){
            Thread.sleep(100);
        }
    }

}