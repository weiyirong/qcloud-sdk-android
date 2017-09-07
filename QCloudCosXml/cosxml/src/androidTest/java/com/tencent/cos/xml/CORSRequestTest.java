package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QBaseServe;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSResult;
import com.tencent.cos.xml.model.bucket.GetBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.GetBucketCORSResult;
import com.tencent.cos.xml.model.bucket.PutBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.PutBucketCORSResult;
import com.tencent.cos.xml.model.object.OptionObjectRequest;
import com.tencent.cos.xml.model.object.OptionObjectResult;
import com.tencent.cos.xml.model.tag.CORSRule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class CORSRequestTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public CORSRequestTest() {
        super(Application.class);
    }
    //service
    private void init(){
        if(qBaseServe == null){
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }

    //cors
    public boolean testPutBucketCORSRequest() throws Exception{

        PutBucketCORSRequest request = new PutBucketCORSRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        CORSRule corsRule = new CORSRule();
        corsRule.id = "123";
        corsRule.allowedOrigin = "http://123.61.25.13";
        corsRule.maxAgeSeconds = "5000";
        List<String> methods = new LinkedList<String>();
        methods.add("put");
        corsRule.allowedMethod = methods;
        List<String> headers = new LinkedList<String>();
        headers.add("host");
        corsRule.allowedHeader = headers;
        List<String> exposeHeaders = new LinkedList<String>();
        exposeHeaders.add("x-cos-metha-1");
        corsRule.exposeHeader = exposeHeaders;
        request.setCORSRuleList(corsRule);

        List<CORSRule> list = new ArrayList<CORSRule>();

        CORSRule corsRule1 = new CORSRule();
        corsRule1.id = "123";
        corsRule1.allowedOrigin = "http://www.qcloud.com";
        corsRule1.maxAgeSeconds = "5000";
        List<String> methods1 = new LinkedList<String>();
        methods1.add("put");
        corsRule1.allowedMethod = methods1;
        List<String> headers1 = new LinkedList<String>();
        headers1.add("host");
        corsRule1.allowedHeader = headers1;
        List<String> exposeHeaders1 = new LinkedList<String>();
        exposeHeaders1.add("x-cos-metha-1");
        corsRule1.exposeHeader = exposeHeaders1;
        list.add(corsRule1);
        request.setCORSRuleList(list);

        PutBucketCORSResult result =  qBaseServe.cosXmlService.putBucketCORS(request);
        String headers_result = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers_result + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testGetBucketCORSRequest() throws Exception{
        init();
        GetBucketCORSRequest request = new GetBucketCORSRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        GetBucketCORSResult result =  qBaseServe.cosXmlService.getBucketCORS(request);
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

    public boolean testOptionObjectRequest(String cosPath) throws Exception{
        init();
        OptionObjectRequest request = new OptionObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setSign(600,null,null);
        request.setOrigin("http://123.61.25.13");
        request.setAccessControlMethod("put");
        request.setAccessControlHeaders("host");
        OptionObjectResult result =  qBaseServe.cosXmlService.optionObject(request);
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

    public boolean testDeleteBucketCORS() throws Exception{
        init();
        DeleteBucketCORSRequest request = new DeleteBucketCORSRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        DeleteBucketCORSResult result =  qBaseServe.cosXmlService.deleteBucketCORS(request);
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
    @Test
    public void test1() throws Exception {
        init();
       // testPutBucketCORSRequest();
        testGetBucketCORSRequest();
       // testOptionObjectRequest("1503563341274.txt");
      //  testDeleteBucketCORS();
//        assertEquals(true, testPutBucketCORSRequest());
//        assertEquals(true, testGetBucketCORSRequest());
//        assertEquals(true, testOptionObjectRequest("1503563341274.txt"));
//        assertEquals(true, testDeleteBucketCORS());
    }


    //cors
    public boolean testPutBucketCORSRequest2() throws Exception{
        hasCompleted = 0;
        PutBucketCORSRequest request = new PutBucketCORSRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        CORSRule corsRule = new CORSRule();
        corsRule.id = "123";
        corsRule.allowedOrigin = "http://123.61.25.13";
        corsRule.maxAgeSeconds = "5000";
        List<String> methods = new LinkedList<String>();
        methods.add("put");
        corsRule.allowedMethod = methods;
        List<String> headers = new LinkedList<String>();
        headers.add("host");
        corsRule.allowedHeader = headers;
        List<String> exposeHeaders = new LinkedList<String>();
        exposeHeaders.add("x-cos-metha-1");
        corsRule.exposeHeader = exposeHeaders;
        request.setCORSRuleList(corsRule);

        List<CORSRule> list = new ArrayList<CORSRule>();

        CORSRule corsRule1 = new CORSRule();
        corsRule1.id = "123";
        corsRule1.allowedOrigin = "http://www.qcloud.com";
        corsRule1.maxAgeSeconds = "5000";
        List<String> methods1 = new LinkedList<String>();
        methods1.add("put");
        corsRule1.allowedMethod = methods1;
        List<String> headers1 = new LinkedList<String>();
        headers1.add("host");
        corsRule1.allowedHeader = headers1;
        List<String> exposeHeaders1 = new LinkedList<String>();
        exposeHeaders1.add("x-cos-metha-1");
        corsRule1.exposeHeader = exposeHeaders1;
        list.add(corsRule1);
        request.setCORSRuleList(list);

        qBaseServe.cosXmlService.putBucketCORSAsync(request, new CosXmlResultListener() {
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

    public boolean testGetBucketCORSRequest2() throws Exception{
        hasCompleted = 0;
        GetBucketCORSRequest request = new GetBucketCORSRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.getBucketCORSAsync(request, new CosXmlResultListener() {
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

    public boolean testOptionObjectRequest2(String cosPath) throws Exception{
        hasCompleted = 0;
        OptionObjectRequest request = new OptionObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setSign(600,null,null);
        request.setOrigin("http://123.61.25.13");
        request.setAccessControlMethod("put");
        request.setAccessControlHeaders("host");
       qBaseServe.cosXmlService.optionObjectAsync(request, new CosXmlResultListener() {
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

    public boolean testDeleteBucketCORS2() throws Exception{
        hasCompleted = 0;
        DeleteBucketCORSRequest request = new DeleteBucketCORSRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.deleteBucketCORSAsync(request, new CosXmlResultListener() {
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

    @Test
    public void test2() throws Exception {
        init();
        testPutBucketCORSRequest2();
        testGetBucketCORSRequest2();
        testOptionObjectRequest2("1503563341274.txt");
        testDeleteBucketCORS2();
//        assertEquals(true, testPutBucketCORSRequest2());
//        assertEquals(true, testGetBucketCORSRequest2());
//        assertEquals(true, testOptionObjectRequest2("1503563341274.txt"));
//        assertEquals(true, testDeleteBucketCORS2());
    }
}