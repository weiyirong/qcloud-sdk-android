package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.GetBucketACLRequest;
import com.tencent.cos.xml.model.bucket.GetBucketACLResult;
import com.tencent.cos.xml.model.bucket.PutBucketACLRequest;
import com.tencent.cos.xml.model.bucket.PutBucketACLResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        PutBucketACLRequest request = new PutBucketACLRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setXCOSACL("public-read");
        List<String> readIdList = new ArrayList<String>();
        readIdList.add("uin/1278687956:uin/1278687956");
        request.setXCOSGrantReadWithUIN(readIdList);
        List<String> writeIdList = new ArrayList<String>();
        writeIdList.add("uin/1278687956:uin/1278687956");
        request.setXCOSGrantWriteWithUIN(writeIdList);
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
        PutBucketACLRequest request = new PutBucketACLRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setXCOSACL(COSACL.PUBLIC_READ);
        List<String> readIdList = new ArrayList<String>();
        readIdList.add("uin/1278687956:uin/2779643970");
        request.setXCOSReadWriteWithUIN(readIdList);
        qBaseServe.cosXmlService.putBucketACLAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
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

    public boolean testGetBucketACLRequest() throws Exception{
        GetBucketACLRequest request = new GetBucketACLRequest();
        request.setBucket(qBaseServe.bucket);
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
        GetBucketACLRequest request = new GetBucketACLRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.getBucketACLAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
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
        testPutBucketACLRequest();
        testGetBucketACLRequest();
//        assertEquals(true, testPutBucketACLRequest());
//        assertEquals(true, testGetBucketACLRequest());
    }

    @Test
    public void test2() throws Exception{
        init();
        testPutBucketACLRequest2();
        testGetBucketACLRequest2();
//        assertEquals(true, testPutBucketACLRequest2());
//        assertEquals(true, testGetBucketACLRequest2());
    }

}
