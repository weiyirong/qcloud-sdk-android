package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.QCloudHttpRequest;

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

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = new QBaseServe(appContext, new ShortTimeCredentialProvider(QBaseServe.secretId,
                QBaseServe.secretKey, 600));
    }

    @Test
    public void testGetBucketRequest() throws Exception{
        GetBucketRequest request = new GetBucketRequest();
        request.setBucket(qBaseServe.bucket);
        request.setPrefix("2");
        request.setDelimiter('7');
        request.setMarker("100");
        QCloudHttpRequest<GetBucketResult> getBucketResultQCloudHttpRequest = qBaseServe.cosXmlService
                .buildGetBucketRequest(request);

        HttpResult<GetBucketResult> result =
                qBaseServe.cosXmlService.execute(getBucketResultQCloudHttpRequest);
        assertEquals(true, qBaseServe.isSuccess(result.code()));
    }
}
