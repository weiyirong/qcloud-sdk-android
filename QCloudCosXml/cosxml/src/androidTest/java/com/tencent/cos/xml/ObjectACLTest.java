package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        PutObjectACLRequest request = new PutObjectACLRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setXCOSACL("public-read");
        List<String> readIdList = new ArrayList<String>();
        readIdList.add("uin/1278687956:uin/1278687956");
        request.setXCOSGrantReadWithUIN(readIdList);
        List<String> writeIdList = new ArrayList<String>();
        writeIdList.add("uin/1278687956:uin/1278687956");
        request.setXCOSGrantWriteWithUIN(writeIdList);
        request.setSign(600,null,null);
        PutObjectACLResult result =  qBaseServe.cosXmlService.putObjectACL(request);
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

    public boolean testPutObjectACLRequest2(String cosPath) throws Exception{
        hasCompleted = 0;
        PutObjectACLRequest request = new PutObjectACLRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setXCOSACL("public-read");
        List<String> readIdList = new ArrayList<String>();
        readIdList.add("1278687956");
        request.setXCOSGrantReadWithUIN(readIdList);
        List<String> writeIdList = new ArrayList<String>();
        writeIdList.add("1278687956");
        request.setXCOSGrantWriteWithUIN(writeIdList);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.putObjectACLAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
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
        GetObjectACLRequest request = new GetObjectACLRequest();
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
        GetObjectACLRequest request = new GetObjectACLRequest();
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
           public void onFail(CosXmlRequest request, CosXmlResult result) {
               Log.d("TAG", "GetObjectAclRequest failed");
               Log.d(TAG, result.printHeaders() + "|" + result.printError());
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
        String cosPath = "/1503563341274.txt";
        testPutObjectACLRequest(cosPath);
        //testGetObjectACLRequest(cosPath);
        //assertEquals(true, testPutObjectACLRequest(cosPath));
        assertEquals(true, testGetObjectACLRequest(cosPath));

    }

    @Test
    public void test2() throws Exception{
        init();
        String cosPath = "/1503563341274.txt";
        testPutObjectACLRequest2(cosPath);
        //testGetObjectACLRequest2(cosPath);
//        assertEquals(false, testPutObjectACLRequest2(cosPath));
        assertEquals(true, testGetObjectACLRequest2(cosPath));
    }

}
