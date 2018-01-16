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
public class GetBucketRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public GetBucketRequestTest() {
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
    public void setPrefix() throws Exception {
    }

    @Test
    public void getPrefix() throws Exception {
    }

    @Test
    public void setDelimiter() throws Exception {
    }

    @Test
    public void getDelimiter() throws Exception {
    }

    @Test
    public void setEncodingType() throws Exception {
    }

    @Test
    public void getEncodingType() throws Exception {
    }

    @Test
    public void setMarker() throws Exception {
    }

    @Test
    public void getMarker() throws Exception {
    }

    @Test
    public void setMaxKeys() throws Exception {
    }

    @Test
    public void getMaxKeys() throws Exception {
    }

//    @Test
//    public void test() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        GetBucketRequest request = new GetBucketRequest(bucket);
//        GetBucketResult result = QService.getCosXmlClient(getContext()).getBucket(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        GetBucketRequest request = new GetBucketRequest(bucket);
//        QService.getCosXmlClient(getContext()).getBucketAsync(request, new CosXmlResultListener() {
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