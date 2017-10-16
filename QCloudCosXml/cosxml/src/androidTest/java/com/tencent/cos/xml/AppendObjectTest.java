package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.AppendObjectResult;
import com.tencent.qcloud.core.network.QCloudProgressListener;


import org.junit.Test;

import java.util.Arrays;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class AppendObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public AppendObjectTest() {
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
        String cosPath = "/" + System.currentTimeMillis() + ".txt";
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        AppendObjectRequest request = new AppendObjectRequest(qBaseServe.bucket, cosPath, data, 0);
        request.setSign(600,null,null);
        assertEquals(cosPath, request.getCosPath());
        request.setData(data);
        assertEquals(true, request.getData() != null);
        assertEquals(0, request.getPosition());
        request.setSign(600,null,null);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d(TAG, String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });

        AppendObjectResult result =  qBaseServe.cosXmlService.appendObject(request);
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
        String cosPath = "/" + System.currentTimeMillis() + ".txt";
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        AppendObjectRequest request = new AppendObjectRequest(qBaseServe.bucket, cosPath, data, 0);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.appendObjectAsync(request, new CosXmlResultListener() {
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
