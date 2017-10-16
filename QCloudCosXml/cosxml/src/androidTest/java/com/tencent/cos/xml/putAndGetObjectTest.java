package com.tencent.cos.xml;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.qcloud.core.network.QCloudProgressListener;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class putAndGetObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public putAndGetObjectTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }

    public boolean testPutObjectRequest(String cosPath) throws Exception{
        String srcPath = qBaseServe.crateFile(1024 * 1024);
        PutObjectRequest request = new PutObjectRequest(qBaseServe.bucket, cosPath, srcPath);
        request.setSign(600,null,null);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d(TAG, String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        request.getProgressListener();
        assertEquals(cosPath, request.getCosPath());
        assertEquals(srcPath, request.getSrcPath());
        request.setCacheControl(null);
        //request.setCacheControl("no-cache");
        request.setContentDisposition(null);
        //request.setContentDisposition("attachment");
        request.setContentEncodeing(null);
        //request.setContentEncoding("utf-8");
        request.setExpect(null);

        PutObjectResult result =  qBaseServe.cosXmlService.putObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else{
            return false;
        }
    }

    public boolean testPutObjectRequest2(String cosPath) throws Exception{
        hasCompleted = 0;
        String srcPath = qBaseServe.crateFile(1024 * 1024);
        PutObjectRequest request = new PutObjectRequest(qBaseServe.bucket, cosPath, srcPath);

        request.setSign(600,null,null);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d(TAG, String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
       qBaseServe.cosXmlService.putObjectAsync(request, new CosXmlResultListener() {
           @Override
           public void onSuccess(CosXmlRequest request, CosXmlResult result) {
               Log.d(TAG, result.printHeaders());
               hasCompleted = 1;
           }

           @Override
           public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
               if (exception != null) {
                   Log.d(TAG, exception.toString());
               }
               if (serviceException != null) {
                   Log.d(TAG, serviceException.toString());
               }
               hasCompleted = 2;
           }
       });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    public boolean testGetObjectRequest(String cosPath) throws Exception{
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/test";
        GetObjectRequest request = new GetObjectRequest(qBaseServe.bucket, cosPath, savePath);
        request.setSign(600,null,null);
        request.setRange(1);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d(TAG, String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        request.setRspContentType("text/json");
        request.setRspContentLanguage("CN");
        assertEquals("CN", request.getRspContentLanguage());

        GetObjectResult result = qBaseServe.cosXmlService.getObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else{
            return false;
        }
    }

    public boolean testGetObjectRequest2(String cosPath) throws Exception{
        hasCompleted = 0;
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/test";
        GetObjectRequest request = new GetObjectRequest(qBaseServe.bucket, cosPath, savePath);
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setSign(600,null,null);
        request.setRange(1);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d(TAG, String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        qBaseServe.cosXmlService.getObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if (exception != null) {
                    Log.d(TAG, exception.toString());
                }
                if (serviceException != null) {
                    Log.d(TAG, serviceException.toString());
                }
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    @Test
    public void test1() throws Exception{
        init();
        String cosPath = "/putobject_上传文件" + System.currentTimeMillis() + ".txt";
        assertEquals(true,testPutObjectRequest(cosPath) );
        assertEquals(true,testGetObjectRequest(cosPath) );
    }

    @Test
    public void test2() throws Exception{
        init();
        String cosPath = "putobject_" + System.currentTimeMillis() + ".txt";
        assertEquals(true,testPutObjectRequest2(cosPath) );
        assertEquals(true,testGetObjectRequest2(cosPath) );
    }
}
