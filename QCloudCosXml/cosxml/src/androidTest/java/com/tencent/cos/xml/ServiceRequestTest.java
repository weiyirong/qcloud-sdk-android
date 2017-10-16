package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class ServiceRequestTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public ServiceRequestTest() {
        super(Application.class);
    }
    //service
    private void init(){
        if(qBaseServe == null){
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    @Test
    public void test1() throws Exception {
        init();
        GetServiceRequest request = new GetServiceRequest();

        request.setSign(600, null, null);
        GetServiceResult result = qBaseServe.cosXmlService.getService(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        final String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void test2() throws Exception {
        init();
        GetServiceRequest request = new GetServiceRequest();
        request.setSign(600, null, null);
        qBaseServe.cosXmlService.getServiceAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if (exception != null) {
                    Log.d(TAG, exception.toString());
                }
                if (serviceException != null) {
                    Log.d(TAG, serviceException.toString());
                }
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        assertEquals(1, hasCompleted);
    }

}