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
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/11.
 */
public class PutBucketLifecycleRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public PutBucketLifecycleRequestTest() {
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
    public void setRuleList() throws Exception {
    }

    @Test
    public void setRuleList1() throws Exception {
    }

    @Test
    public void getLifecycleConfiguration() throws Exception {
    }
//
//    @Test
//    public void test() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketLifecycleRequest request = new PutBucketLifecycleRequest(bucket);
//        LifecycleConfiguration.Rule rule = new LifecycleConfiguration.Rule();
//        rule.id = "LifeID";
//        rule.status = "Enabled";
//        rule.filter = new LifecycleConfiguration.Filter();
//        rule.filter.prefix = "aws";
//        rule.expiration = new LifecycleConfiguration.Expiration();
//        rule.expiration.days = 1;
//       // rule.expiration.date = "Mon, 11 Dec 2017 15:43:39 GMT";
//        request.setRuleList(rule);
//        PutBucketLifecycleResult result = QService.getCosXmlClient(getContext()).putBucketLifecycle(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketLifecycleRequest request = new PutBucketLifecycleRequest(bucket);
//        LifecycleConfiguration.Rule rule = new LifecycleConfiguration.Rule();
//        rule.id = "LifeID2";
//        rule.status = "Enabled";
//        rule.filter = new LifecycleConfiguration.Filter();
//        rule.filter.prefix = "aws";
//        rule.expiration = new LifecycleConfiguration.Expiration();
//        rule.expiration.days = 1;
//        //rule.expiration.date = "Mon, 11 Dec 2017 14:43:39 GMT";
//        request.setRuleList(rule);
//        QService.getCosXmlClient(getContext()).putBucketLifecycleAsync(request, new CosXmlResultListener() {
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