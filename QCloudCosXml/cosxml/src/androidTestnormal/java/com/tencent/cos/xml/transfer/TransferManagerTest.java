package com.tencent.cos.xml.transfer;

import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.utils.DigestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tencent.cos.xml.QServer.TAG;
import static com.tencent.cos.xml.QServer.appid;
import static com.tencent.cos.xml.QServer.bucketForObject;
import static com.tencent.cos.xml.QServer.region;
import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/9/14.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class TransferManagerTest {
    TransferManager transferManager;
    @Before
    public void init(){
        QServer.init(InstrumentationRegistry.getContext());
        transferManager = new TransferManager((CosXmlService) QServer.cosXml, new TransferConfig.Builder().build());
    }

    @Test
    public void upload() throws Exception {
        String cosPath = "uploadTask_" + System.currentTimeMillis();
        final String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 1024 * 1024);
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(QServer.bucketForObject, cosPath, srcPath, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
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
    }

    @Test
    public void upload1() throws Exception {

        String cosPath = "uploadTask_" + System.currentTimeMillis();
        final String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 20 * 1024 * 1024);
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(QServer.bucketForObject, cosPath, srcPath, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
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
    }

    @Test
    public void uploadTask() throws Exception {
        String cosPath = "uploadTask_" + System.currentTimeMillis();
        final String srcPath = QServer.createFile(InstrumentationRegistry.getContext(), 20 * 1024 * 1024);
        PutObjectRequest putObjectRequest = new PutObjectRequest(QServer.bucketForObject, cosPath, srcPath);
        putObjectRequest.setSign(700);
        putObjectRequest.setRequestHeaders("cos-xml-metate", "meta");
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(putObjectRequest, null);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
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
    }

    @Test
    public void download() throws Exception {
        String cosPath = "transfer_" + 1;
        final String localDir = Environment.getExternalStorageDirectory().getPath();
        final String localFileName = "download.pdf";
        COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(InstrumentationRegistry.getContext(),  QServer.bucketForObject, cosPath, localDir, localFileName);
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
            }
        });
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
                try {
                    String srcMd5 = DigestUtils.getMD5(Environment.getExternalStorageDirectory().getPath() + "/Java nio.pdf");
                    String destMd5 = DigestUtils.getMD5(localDir + "/" + localFileName);
                    Log.d(TAG, String.format("src = %s | dst = %s | %s", srcMd5, destMd5, String.valueOf(srcMd5.equals(destMd5))));
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
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
    }

    @Test
    public void download1() throws Exception {
        String cosPath = "transfer_" + 1;
        final String localDir = Environment.getExternalStorageDirectory().getPath();
        final String localFileName = "download.pdf";
        GetObjectRequest getObjectRequest = new GetObjectRequest(QServer.bucketForObject, cosPath, localDir, localFileName);
        COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(InstrumentationRegistry.getContext(), getObjectRequest);
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d(TAG,  String.format("progress = %d%%", (int)progress));
            }
        });
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG,  result.printResult());
                try {
                    String srcMd5 = DigestUtils.getMD5(Environment.getExternalStorageDirectory().getPath() + "/Java nio.pdf");
                    String destMd5 = DigestUtils.getMD5(localDir + "/" + localFileName);
                    Log.d(TAG, String.format("src = %s | dst = %s | %s", srcMd5, destMd5, String.valueOf(srcMd5.equals(destMd5))));
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
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
    }


    @Test
    public void copy() throws Exception{
        String cosPath = "copyTask.png";
        String sourceCosPath = "52B7B13D-8030-42BD-A299-BDE65B97E951.png";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                appid, QServer.bucketForObject, region, sourceCosPath);
        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(QServer.bucketForObject, cosPath, copySourceStruct);
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

    }

    @Test
    public void copy1()throws Exception{
        String cosPath = "copyTask.png";
        String sourceCosPath = "52B7B13D-8030-42BD-A299-BDE65B97E951.png";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                appid, QServer.bucketForObject, region, sourceCosPath);
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(QServer.bucketForObject, cosPath, copySourceStruct);
        COSXMLCopyTask cosxmlCopyTask = transferManager.copy(copyObjectRequest);
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
    }
}