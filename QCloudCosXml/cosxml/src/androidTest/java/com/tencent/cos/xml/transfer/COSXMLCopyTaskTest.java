//package com.tencent.cos.xml.transfer;
//
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.tencent.cos.xml.CosXmlSimpleService;
//import com.tencent.cos.xml.QServer;
//import com.tencent.cos.xml.exception.CosXmlClientException;
//import com.tencent.cos.xml.exception.CosXmlServiceException;
//import com.tencent.cos.xml.listener.CosXmlResultListener;
//import com.tencent.cos.xml.model.CosXmlRequest;
//import com.tencent.cos.xml.model.CosXmlResult;
//import com.tencent.cos.xml.model.object.CopyObjectRequest;
//import com.tencent.cos.xml.model.object.PutObjectRequest;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static com.tencent.cos.xml.QServer.TAG;
//
///**
// * Created by bradyxiao on 2018/9/26.
// * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
// */
//@RunWith(AndroidJUnit4.class)
//public class COSXMLCopyTaskTest {
//
//    TransferManager transferManager;
//    @Before
//    public void init() throws Exception{
//        QServer.init(InstrumentationRegistry.getContext());
//    }
//
//    @Test
//    public void pause() throws Exception{
//        String sourceCosPath = "upload_rn";
//        String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
//        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.persistBucket, sourceCosPath, srcPath);
//        QServer.cosXml.putObject(putObjectRequest);
//        QServer.deleteLocalFile(srcPath);
//        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().
//                setDividsionForCopy(20*1024 * 1024).setSliceSizeForCopy(1024*1024).build());
//        String cosPath = "upload_rn.copy";
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                QServer.appid, QServer.persistBucket, QServer.region, sourceCosPath);
//        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.persistBucket, cosPath, copySourceStruct);
//        cosxmlCopyTask.setCosXmlProgressListener(null);
//        cosxmlCopyTask.setTransferStateListener(new TransferStateListener() {
//            @Override
//            public void onStateChanged(TransferState state) {
//                Log.d(TAG,  state.name());
//            }
//        });
//        cosxmlCopyTask.setCosXmlResultListener(new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG,  result.printResult());
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
//            }
//        });
//
//        while (cosxmlCopyTask.getTaskState() != TransferState.IN_PROGRESS){
//            Thread.sleep(100);
//        }
//        cosxmlCopyTask.pause();
//        Thread.sleep(1000);
//        Assert.assertEquals(cosxmlCopyTask.getTaskState(), TransferState.PAUSED);
//    }
//
//    @Test
//    public void cancel() throws Exception{
//        String sourceCosPath = "upload_rn";
//        String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
//        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.persistBucket, sourceCosPath, srcPath);
//        QServer.cosXml.putObject(putObjectRequest);
//        QServer.deleteLocalFile(srcPath);
//        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().
//                setDividsionForCopy(20*1024 * 1024).setSliceSizeForCopy(1024*1024).build());
//        String cosPath = "upload_rn.copy";
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                QServer.appid, QServer.persistBucket, QServer.region, sourceCosPath);
//        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.persistBucket, cosPath, copySourceStruct);
//        cosxmlCopyTask.setCosXmlProgressListener(null);
//        cosxmlCopyTask.setTransferStateListener(new TransferStateListener() {
//            @Override
//            public void onStateChanged(TransferState state) {
//                Log.d(TAG,  state.name());
//            }
//        });
//        cosxmlCopyTask.setCosXmlResultListener(new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG,  result.printResult());
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
//            }
//        });
//        while (cosxmlCopyTask.getTaskState() != TransferState.IN_PROGRESS){
//            Thread.sleep(100);
//        }
//        cosxmlCopyTask.cancel();
//        Thread.sleep(1000);
//        Assert.assertEquals(cosxmlCopyTask.getTaskState(), TransferState.CANCELED);
//    }
//
//    @Test
//    public void resume() throws Exception{
//        String sourceCosPath = "upload_rn";
//        String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
//        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.persistBucket, sourceCosPath, srcPath);
//        QServer.cosXml.putObject(putObjectRequest);
//        QServer.deleteLocalFile(srcPath);
//        transferManager = new TransferManager((CosXmlSimpleService) QServer.cosXml, new TransferConfig.Builder().
//                setDividsionForCopy(1024 * 1024).setSliceSizeForCopy(1024*1024).build());
//        String cosPath = "upload_rn.copy";
//
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                QServer.appid, QServer.persistBucket, QServer.region, sourceCosPath);
//        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.persistBucket, cosPath, copySourceStruct);
//        cosxmlCopyTask.setCosXmlProgressListener(null);
//        cosxmlCopyTask.setTransferStateListener(new TransferStateListener() {
//            @Override
//            public void onStateChanged(TransferState state) {
//                Log.d(TAG,  state.name());
//            }
//        });
//        cosxmlCopyTask.setCosXmlResultListener(new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG,  result.printResult());
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d(TAG,  exception == null ? serviceException.getMessage() : exception.toString());
//            }
//        });
//
//        while (cosxmlCopyTask.getTaskState() != TransferState.IN_PROGRESS){
//            Thread.sleep(100);
//        }
//        cosxmlCopyTask.pause();
//        Thread.sleep(500);
//        cosxmlCopyTask.resume();
//        Assert.assertTrue(cosxmlCopyTask.getTaskState() == TransferState.RESUMED_WAITING);
//        Thread.sleep(500);
//        Assert.assertTrue(cosxmlCopyTask.getTaskState() == TransferState.IN_PROGRESS ||
//                cosxmlCopyTask.getTaskState() == TransferState.COMPLETED ||
//                cosxmlCopyTask.getTaskState() == TransferState.FAILED);
//    }
//
//}