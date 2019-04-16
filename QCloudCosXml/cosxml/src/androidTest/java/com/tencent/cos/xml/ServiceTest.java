package com.tencent.cos.xml;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

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

}
