package com.tencent.cos.xml;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Created by bradyxiao on 2018/9/26.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class MTAServerTest {

    @Before
    public void init(){
        MTAProxy.init(InstrumentationRegistry.getContext().getApplicationContext());
    }

    @Test
    public void testReportClientException() throws Exception{
        MTAProxy.getInstance().reportCosXmlClientException(new Date().toString(), new CosXmlClientException("client exception 1").getMessage());
        Thread.sleep(15000);
    }

    @Test
    public void testReportServerException() throws Exception{
        CosXmlServiceException cosXmlServiceException = new CosXmlServiceException("server exception 1");
        cosXmlServiceException.setRequestId("requestId");
        MTAProxy.getInstance().reportCosXmlServerException(new Date().toString(), cosXmlServiceException.getRequestId());
        Thread.sleep(15000);
    }

    @Test
    public void testReportClientException2() throws Exception{
        MTAProxy.getInstance().reportCosXmlClientException(new CosXmlClientException("client exception 2").getMessage());
        Thread.sleep(15000);
    }

    @Test
    public void testReportServerException2() throws Exception{
        CosXmlServiceException cosXmlServiceException = new CosXmlServiceException("server exception 2");
        cosXmlServiceException.setRequestId("requestId");
        MTAProxy.getInstance().reportCosXmlServerException(cosXmlServiceException.getRequestId());
        Thread.sleep(15000);
    }
}