package com.tencent.cos.xml.model.bucket;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/11.
 */
public class PutBucketReplicationRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public PutBucketReplicationRequestTest() {
        super(Application.class);
    }


    @Test
    public void setReplicationConfigurationWithRole() throws Exception {
    }

    @Test
    public void setReplicationConfigurationWithRule() throws Exception {
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


//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketReplicationRequest request = new PutBucketReplicationRequest(bucket);
//        PutBucketReplicationRequest.RuleStruct ruleStruct = new PutBucketReplicationRequest.RuleStruct();
//        ruleStruct.id = "replication_id";
//        ruleStruct.isEnable = true;
//        ruleStruct.appid = "1253960454";
//        ruleStruct.bucket = "replicationtest";
//        ruleStruct.region = "ap-beijing";
//        request.setReplicationConfigurationWithRule(ruleStruct);
//        PutBucketReplicationResult result = QService.getCosXmlClient(getContext()).putBucketReplication(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketReplicationRequest request = new PutBucketReplicationRequest(bucket);
//        PutBucketReplicationRequest.RuleStruct ruleStruct = new PutBucketReplicationRequest.RuleStruct();
//        ruleStruct.id = "replication_id";
//        ruleStruct.isEnable = true;
//        ruleStruct.appid = "1253960454";
//        ruleStruct.bucket = "replicationtest";
//        ruleStruct.region = "ap-beijing";
//        request.setReplicationConfigurationWithRule(ruleStruct);
//        QService.getCosXmlClient(getContext()).putBucketReplicationAsync(request, new CosXmlResultListener() {
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
//
//        while (!isOver){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
}