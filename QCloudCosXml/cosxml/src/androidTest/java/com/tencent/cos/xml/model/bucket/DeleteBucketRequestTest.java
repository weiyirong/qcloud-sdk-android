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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/11.
 */
public class DeleteBucketRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";
    volatile boolean isOver = false;

    public DeleteBucketRequestTest() {
        super(Application.class);
    }

    @Test
    public void getMethod() throws Exception {
    }

    @Test
    public void getRequestBody() throws Exception {
    }


//    @Test
//    public void test() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest2";
//        DeleteBucketRequest request = new DeleteBucketRequest(bucket);
//        DeleteBucketResult result = QService.getCosXmlClient(getContext()).deleteBucket(request);
//        Log.d(TAG, result.printResult());
//    }

//    @Test
//    public void test2() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest2";
//        DeleteBucketRequest request = new DeleteBucketRequest(bucket);
//        QService.getCosXmlClient(getContext()).deleteBucketAsync(request, new CosXmlResultListener() {
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