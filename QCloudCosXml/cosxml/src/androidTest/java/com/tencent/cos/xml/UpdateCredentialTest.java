package com.tencent.cos.xml;


import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.qcloud.core.network.auth.LocalSessionCredentialProvider;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class UpdateCredentialTest {

    private String bucket = "xy2";

    private String appid = "1253653367";

    private String region = "cn-south";

    private CosXmlService cosXmlService;

    private LocalSessionCredentialProvider localSessionCredentialProvider;

    private CountDownLatch countDownLatch;

    @Before public void setUp() {

        countDownLatch = new CountDownLatch(4);
//        localSessionCredentialProvider = new LocalSessionCredentialProvider(new SessionCredential("AKIDgAWeYF6vueARTGI7B0aw3wZx61h9xK3Y", "PaWEhwxjGXYKq68wVptiJfmeHfnfIfme",
//                "6b6f5c4038e760f1b3d66797b1fbd6e4de1aeef23", 1504274278));
//        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig(appid,region);
//        cosXmlService = new CosXmlService(InstrumentationRegistry.getContext(), cosXmlServiceConfig, localSessionCredentialProvider);

    }

    @After public void tearDown() throws Exception {

        if (cosXmlService != null) {
            cosXmlService.release();
        }
    }


    @Test
    public void test1() throws Exception{

        test();
        test();
        test();
        test();

        countDownLatch.await();
    }

    public void test2() throws Exception {


    }

    private void test() {

        String cosPath = "/1503563341274.txt";
        HeadObjectRequest request = new HeadObjectRequest(bucket, cosPath);

        request.setSign(600,null,null);
        cosXmlService.headObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {

                Log.d("TAG", "test on success");
                countDownLatch.countDown();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if (exception != null) {
                    Log.d("TAG", exception.toString());
                }
                if (serviceException != null) {
                    Log.d("TAG", serviceException.toString());
                }
                countDownLatch.countDown();
            }
        });
    }
}
