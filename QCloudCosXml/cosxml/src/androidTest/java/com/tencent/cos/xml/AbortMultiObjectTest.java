package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class AbortMultiObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;
    String cosPath = "/slice_upload.txt";
    String uploadId = "150649955338b28f46b5cd718b53c56b966a7892c73eca6b3968a0a24f82e5e26095846aed";
    public AbortMultiObjectTest() {
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
        AbortMultiUploadRequest request = new AbortMultiUploadRequest(qBaseServe.bucket, cosPath, uploadId);
        request.setSign(600,null,null);
        request.getCosPath();
        request.getUploadId();
        try{
            AbortMultiUploadResult result =  qBaseServe.cosXmlService.abortMultiUpload(request);
            String headers = result.printHeaders();
            String body = result.printBody();
            String error = result.printError();
            String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
            Log.d(TAG,response);
            assertEquals(true,qBaseServe.isSuccess(result.getHttpCode()));
        }catch (Exception e){

        }
    }

    @Test
    public void test2() throws Exception{
        init();
        AbortMultiUploadRequest request = new AbortMultiUploadRequest(qBaseServe.bucket, cosPath, uploadId);
        request.setSign(600,null,null);
        request.getCosPath();
        request.getUploadId();
        qBaseServe.cosXmlService.abortMultiUploadAsync(request, new CosXmlResultListener() {
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
        assertEquals(2, hasCompleted);
    }
}
