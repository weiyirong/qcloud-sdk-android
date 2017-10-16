package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by wjielai on 2017/10/13.
 */

@RunWith(AndroidJUnit4.class)
public class ServiceTest {
    private QBaseServe qBaseServe;
    String bucket = "androidsample";
    String opBucket = "wjbk01";

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getSecondInstance(appContext);
    }

    @Test
    public void testGetBucketRequest() throws Exception{
        GetBucketRequest request = new GetBucketRequest();
        request.setBucket(bucket);
        request.setSign(600,null,null);
        GetBucketResult result =  qBaseServe.cosXmlService.execute(request);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }
}
