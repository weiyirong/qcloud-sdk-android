package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.cos.bucket.DeleteBucketRequest;
import com.tencent.qcloud.core.cos.bucket.DeleteBucketResult;
import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.cos.bucket.PutBucketRequest;
import com.tencent.qcloud.core.cos.bucket.PutBucketResult;
import com.tencent.qcloud.core.network.QCloudResultListener;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.exception.QCloudServiceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
@RunWith(AndroidJUnit4.class)
public class BucketTest {
    private QBaseServe qBaseServe;
    String bucket = "androidsample";
    String opBucket = "wjbk01";

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getInstance(appContext);

    }

    @Test
    public void testGetBucketRequest() throws Exception{
        GetBucketRequest request = new GetBucketRequest();
        request.setBucket(bucket);
        request.setSign(600,null,null);
        request.setPrefix("2");
        request.setDelimiter('7');
        request.setMarker("100");
        GetBucketResult result =  qBaseServe.cosXmlService.execute(request);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void testGetBucketRequest2() throws Exception {
        GetBucketRequest request = new GetBucketRequest();
        request.setBucket(bucket);
        request.setSign(600,null,null);
        request.setPrefix("2");
        request.setDelimiter('7');
        request.setMarker("100");

        final Object syncObject = new Object();

        qBaseServe.cosXmlService.getBucketAsync(request, new QCloudResultListener<GetBucketRequest, GetBucketResult>() {
            @Override
            public void onSuccess(GetBucketRequest request, GetBucketResult result) {
                assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailed(GetBucketRequest request, QCloudClientException clientException, QCloudServiceException serviceException) {
                assertNull(clientException);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });


        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void testPutBucketRequest() throws Exception{
        PutBucketRequest request = new PutBucketRequest();
        request.setBucket(opBucket);
        request.setSign(600,null,null);
        //request.setXCOSACL(COSACL.PUBLIC_READ);
        List<String> readIdList = new ArrayList<String>();
        readIdList.add("uin/1278687956:uin/2779643970");
        //request.setXCOSReadWrite(readIdList);
        List<String> writeIdList = new ArrayList<String>();
        writeIdList.add("uin/1278687956:uin/2779643970");
        //request.setXCOSGrantWriteWithUIN(writeIdList);
        PutBucketResult result =  qBaseServe.cosXmlService.execute(request);

        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()) || result.getHttpCode() == 409);
    }

    @Test
    public void testDeleteBucketRequest() throws Exception{
        DeleteBucketRequest request = new DeleteBucketRequest();
        request.setBucket(opBucket);
        request.setSign(600,null,null);
        DeleteBucketResult result =  qBaseServe.cosXmlService.execute(request);

        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }


}
