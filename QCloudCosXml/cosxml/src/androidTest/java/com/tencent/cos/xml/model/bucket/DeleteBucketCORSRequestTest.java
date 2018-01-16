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
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSResult;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/4.
 */
public class DeleteBucketCORSRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    DeleteBucketCORSRequest deleteBucketCORSRequest = new DeleteBucketCORSRequest("androidtest");

    public DeleteBucketCORSRequestTest() {
        super(Application.class);
    }

    @Test
    public void getMethod() throws Exception {
        assertEquals("DELETE", deleteBucketCORSRequest.getMethod());
    }

    @Test
    public void getQueryString() throws Exception {
    }

    @Test
    public void getRequestBody() throws Exception {
    }

////    @Test
////    public void test(){
////        try {
////            DeleteBucketCORSRequest deleteBucketCORSRequest = new DeleteBucketCORSRequest("androidtest");
////            DeleteBucketCORSResult getServiceResult = getServiceResult = QService.getCosXmlClient(getContext()).deleteBucketCORS(deleteBucketCORSRequest);
////            Log.d(TAG, getServiceResult.printResult());
////        } catch(Exception e) {
////
////        }
////    }
//
//    @Test
//    public void test2() throws CosXmlServiceException, CosXmlClientException {
//        DeleteBucketCORSRequest deleteBucketCORSRequest = new DeleteBucketCORSRequest("androidtest");
//        QService.getCosXmlClient(getContext()).deleteBucketCORSAsync(deleteBucketCORSRequest, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG, result.printResult());
//                isOver = true;
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.getRange());
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