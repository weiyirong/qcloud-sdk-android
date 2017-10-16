package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.model.tag.ACLAccounts;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class ObjectACLTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public ObjectACLTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    public boolean testPutObjectACLRequest(String cosPath) throws Exception{
        PutObjectACLRequest request = new PutObjectACLRequest(qBaseServe.bucket, cosPath);
        request.setXCOSACL("public-read");
        ACLAccounts readAccounts = new ACLAccounts();
        readAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantRead(readAccounts);

        ACLAccounts writeAccounts = new ACLAccounts();
        writeAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantWrite(writeAccounts);

        request.setSign(600,null,null);
        PutObjectACLResult result =  qBaseServe.cosXmlService.putObjectACL(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        Log.d(TAG, "put bucket acl http code "+result.getHttpCode());
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testPutObjectACLRequest2(String cosPath) throws Exception{
        hasCompleted = 0;
        PutObjectACLRequest request = new PutObjectACLRequest(qBaseServe.bucket, cosPath);
        request.setXCOSACL("public-read");
        ACLAccounts readAccounts = new ACLAccounts();
        readAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantRead(readAccounts);

        ACLAccounts writeAccounts = new ACLAccounts();
        writeAccounts.addACLAccount(new ACLAccount("1131975903", "1131975903"));
        request.setXCOSGrantWrite(writeAccounts);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.putObjectACLAsync(request, new CosXmlResultListener() {
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

    public boolean testGetObjectACLRequest(String cosPath) throws Exception{
        GetObjectACLRequest request = new GetObjectACLRequest(qBaseServe.bucket, cosPath);
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.getCosPath();
        GetObjectACLResult result =  qBaseServe.cosXmlService.getObjectACL(request);
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
    public boolean testGetObjectACLRequest2(String cosPath) throws Exception{
        hasCompleted = 0;
        GetObjectACLRequest request = new GetObjectACLRequest(qBaseServe.bucket, cosPath);
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.getCosPath();
       qBaseServe.cosXmlService.getObjectACLAsync(request, new CosXmlResultListener() {
           @Override
           public void onSuccess(CosXmlRequest request, CosXmlResult result) {
               Log.d("TAG", "GetObjectAclRequest success");
               Log.d(TAG, result.printHeaders());
               hasCompleted = 1;
           }

           @Override
           public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
               Log.d("TAG", "GetObjectAclRequest failed");
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
        String cosPath = "/readMe.md";
        boolean result1 = testPutObjectACLRequest(cosPath);
        boolean result2 = testGetObjectACLRequest(cosPath);
        assertEquals(true, result1 && result2);

    }

    @Test
    public void test2() throws Exception{
        init();
        String cosPath = "/readMe.md";
        boolean result1 = testPutObjectACLRequest2(cosPath);
        boolean result2 = testGetObjectACLRequest2(cosPath);
        assertEquals(true, result1 && result2);
    }

}
