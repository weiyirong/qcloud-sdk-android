package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.bucket.HeadBucketRequest;
import com.tencent.cos.xml.model.bucket.HeadBucketResult;
import com.tencent.cos.xml.model.bucket.PutBucketRequest;
import com.tencent.cos.xml.model.bucket.PutBucketResult;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class bucketTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public bucketTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    public boolean testPutBucketRequest(String bucket) throws Exception{
        PutBucketRequest request = new PutBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);

        request.setXCOSACL(COSACL.PRIVATE);

//        ACLAccounts aclAccounts = new ACLAccounts();
//        aclAccounts.addACLAccount(new ACLAccount("1278687956", "1131975903"));
//        request.setXCOSGrantRead(aclAccounts);
//        request.setXCOSGrantWrite(aclAccounts);

        PutBucketResult result =  qBaseServe.cosXmlService.putBucket(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }
    public boolean testPutBucketRequest2(String bucket) throws Exception{
        hasCompleted = 0;
        PutBucketRequest request = new PutBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        request.setXCOSACL(COSACL.PRIVATE);

//        ACLAccounts aclAccounts = new ACLAccounts();
//        aclAccounts.addACLAccount(new ACLAccount("1278687956", "2779643970"));
//        request.setXCOSGrantRead(aclAccounts);
//        request.setXCOSGrantWrite(aclAccounts);

        qBaseServe.cosXmlService.putBucketAsync(request, new CosXmlResultListener() {
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

    public boolean testHeadBucketRequest(String bucket) throws Exception{
        HeadBucketRequest request = new HeadBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        HeadBucketResult result =  qBaseServe.cosXmlService.headBucket(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }
    public boolean testHeadBucketRequest2(String bucket) throws Exception{
        hasCompleted = 0;
        HeadBucketRequest request = new HeadBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.headBucketAsync(request, new CosXmlResultListener() {
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

    public boolean testGetBucketRequest(String bucket) throws Exception{
        GetBucketRequest request = new GetBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        request.setPrefix("2");
        request.setDelimiter('7');
        request.setMarker("100");
        GetBucketResult result =  qBaseServe.cosXmlService.getBucket(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testGetBucketRequest2(String bucket) throws Exception{
        hasCompleted = 0;
        GetBucketRequest request = new GetBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.getBucketAsync(request, new CosXmlResultListener() {
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

    public boolean testDeleteBucketRequest(String bucket) throws Exception{
        DeleteBucketRequest request = new DeleteBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        DeleteBucketResult result =  qBaseServe.cosXmlService.deleteBucket(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testDeleteBucketRequest2(String bucket) throws Exception{
        hasCompleted = 0;
        DeleteBucketRequest request = new DeleteBucketRequest(bucket);
        request.setBucket(bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.deleteBucketAsync(request, new CosXmlResultListener() {
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
        Log.d("TAG", "bucket test1");
        String bucket = String.valueOf(System.currentTimeMillis()/1000);
        assertEquals(true, testPutBucketRequest(bucket));
        assertEquals(true, testHeadBucketRequest(bucket));
        assertEquals(true, testGetBucketRequest(bucket));
        assertEquals(true, testDeleteBucketRequest(bucket));
    }

    @Test
    public void test2() throws Exception{
        init();
        Log.d("TAG", "bucket test2");
        String bucket = String.valueOf(System.currentTimeMillis()/1000);
        assertEquals(true, testPutBucketRequest2(bucket));
        assertEquals(true, testHeadBucketRequest2(bucket));
        assertEquals(true, testGetBucketRequest2(bucket));
        assertEquals(true, testDeleteBucketRequest2(bucket));
    }


}
