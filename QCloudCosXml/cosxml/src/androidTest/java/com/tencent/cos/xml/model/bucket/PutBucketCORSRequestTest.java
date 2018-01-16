package com.tencent.cos.xml.model.bucket;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.CORSConfiguration;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/11.
 */
public class PutBucketCORSRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";
    volatile boolean isOver = false;

    public PutBucketCORSRequestTest() {
        super(Application.class);
    }

    @Test
    public void getMethod() throws Exception {
    }

    @Test
    public void getQueryString() throws Exception {
    }

    @Test
    public void getRequestBody() throws Exception {
    }

    @Test
    public void addCORSRules() throws Exception {
    }

    @Test
    public void addCORSRule() throws Exception {
    }

    @Test
    public void getCorsConfiguration() throws Exception {
    }

//    @Test
//    public void test() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketCORSRequest request = new PutBucketCORSRequest(bucket);
//        CORSConfiguration.CORSRule corsRule = new CORSConfiguration.CORSRule();
//        corsRule.allowedOrigin = "http://cloud.tencent.com";
//        corsRule.allowedHeader = new ArrayList<>();
//        corsRule.allowedHeader.add("Host");
//        corsRule.allowedHeader.add("Authorization");
//        corsRule.allowedMethod = new ArrayList<>();
//        corsRule.allowedMethod.add("PUT");
//        corsRule.allowedMethod.add("GET");
//        corsRule.exposeHeader = new ArrayList<>();
//        corsRule.exposeHeader.add("x-cos-meta");
//        corsRule.exposeHeader.add("x-cos-meta-2");
//        corsRule.id = "CORSID";
//        corsRule.maxAgeSeconds = 5000;
//        request.addCORSRule(corsRule);
//        PutBucketCORSResult result = QService.getCosXmlClient(getContext()).putBucketCORS(request);
//        Log.d(TAG, result.printResult());
//    }

//    @Test
//    public void test2() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketCORSRequest request = new PutBucketCORSRequest(bucket);
//        CORSConfiguration.CORSRule corsRule = new CORSConfiguration.CORSRule();
//        corsRule.allowedOrigin = "http://cloud.tencent.com";
//        corsRule.allowedHeader = new ArrayList<>();
//        corsRule.allowedHeader.add("Host");
//        corsRule.allowedHeader.add("Authorization");
//        corsRule.allowedMethod = new ArrayList<>();
//        corsRule.allowedMethod.add("PUT");
//        corsRule.allowedMethod.add("GET");
//        corsRule.exposeHeader = new ArrayList<>();
//        corsRule.exposeHeader.add("x-cos-meta");
//        corsRule.exposeHeader.add("x-cos-meta-2");
//        corsRule.id = "CORSID2";
//        corsRule.maxAgeSeconds = 5000;
//        request.addCORSRule(corsRule);
//        QService.getCosXmlClient(getContext()).putBucketCORSAsync(request, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG, result.printResult());
//                isOver = true;
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
//                isOver = true;
//            }
//        });
//        while (!isOver){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}