package com.tencent.cos.xml;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * Created by bradyxiao on 2018/11/15.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

@RunWith(AndroidJUnit4.class)
public class ServiceTest {
     Context appContext;
    public void  init(){
        appContext = InstrumentationRegistry.getContext();
        QServer.init(appContext);
    }

    @Test
    public void testGetService() throws Exception{
        init();
        GetServiceRequest getServiceRequest = new GetServiceRequest();
        GetServiceResult result = QServer.cosXml.getService(getServiceRequest);
        Log.d(QServer.TAG, result.printResult());
    }

    Exception exception = null;
    public void testGerService() throws Exception{
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetServiceRequest getServiceRequest = new GetServiceRequest();
        QServer.cosXml.getServiceAsync(getServiceRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(QServer.TAG, result.printResult());
                countDownLatch.countDown();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException clientEx, CosXmlServiceException serviceEx) {
                exception = clientEx == null ? serviceEx : clientEx;
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if(exception != null){
            throw exception;
        }
    }
}
