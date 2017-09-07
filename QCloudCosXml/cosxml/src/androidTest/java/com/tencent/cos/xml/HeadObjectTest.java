package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;

import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class HeadObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public HeadObjectTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    @Test
    public void test1() throws Exception{
        init();
        HeadObjectRequest request = new HeadObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath("/1503563341274.txt");
        request.setSign(600,null,null);
        HeadObjectResult result =  qBaseServe.cosXmlService.headObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        assertEquals(true,qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void test2() throws Exception{
        init();
        HeadObjectRequest request = new HeadObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath("/1503563341274.txt");
        request.setIfModifiedSince("Wed, 11 Jan 2017 07:30:07 GMT");
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.headObjectAsync(request, new CosXmlResultListener() {
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
        assertEquals(1, hasCompleted);
    }
}
