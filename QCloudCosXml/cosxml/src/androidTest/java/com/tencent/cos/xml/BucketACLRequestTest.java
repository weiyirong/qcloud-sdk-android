package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.GetBucketACLRequest;
import com.tencent.cos.xml.model.bucket.GetBucketACLResult;
import com.tencent.cos.xml.model.bucket.PutBucketACLRequest;
import com.tencent.cos.xml.model.bucket.PutBucketACLResult;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.model.tag.ACLAccounts;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class BucketACLRequestTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public BucketACLRequestTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    public boolean testPutBucketACLRequest() throws Exception{
        PutBucketACLRequest request = new PutBucketACLRequest(qBaseServe.bucket);

        request.setXCOSACL("public-read");
        ACLAccounts readAccounts = new ACLAccounts();
        readAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantRead(readAccounts);

        ACLAccounts writeAccounts = new ACLAccounts();
        writeAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantWrite(writeAccounts);

        request.setSign(600,null,null);
        PutBucketACLResult result =  qBaseServe.cosXmlService.putBucketACL(request);
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

    public boolean testPutBucketACLRequest2() throws Exception{
        hasCompleted = 0;
        PutBucketACLRequest request = new PutBucketACLRequest(qBaseServe.bucket);
        request.setSign(600,null,null);

        request.setXCOSACL("public-read");
        ACLAccounts readAccounts = new ACLAccounts();
        readAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantRead(readAccounts);

        ACLAccounts writeAccounts = new ACLAccounts();
        writeAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantWrite(writeAccounts);

        qBaseServe.cosXmlService.putBucketACLAsync(request, new CosXmlResultListener() {
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

    public boolean testGetBucketACLRequest() throws Exception{
        GetBucketACLRequest request = new GetBucketACLRequest(qBaseServe.bucket);
        request.setSign(600,null,null);
        GetBucketACLResult result =  qBaseServe.cosXmlService.getBucketACL(request);
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

    public boolean testGetBucketACLRequest2() throws Exception{
        hasCompleted = 0;
        GetBucketACLRequest request = new GetBucketACLRequest(qBaseServe.bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.getBucketACLAsync(request, new CosXmlResultListener() {
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
        boolean result1 = testPutBucketACLRequest();
        boolean result2 = testGetBucketACLRequest();
        assertEquals(true, result1 && result2);
    }

    @Test
    public void test2() throws Exception{
        init();
        boolean result1 = testPutBucketACLRequest2();
        boolean result2 = testGetBucketACLRequest2();
        assertEquals(true,  result1 && result2);
    }



}
