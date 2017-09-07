package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

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
    String uploadId = "xxxxxxxxxxxxxxxxxxxxxxxx";
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
        AbortMultiUploadRequest request = new AbortMultiUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.getCosPath();
        request.getUploadId();
        AbortMultiUploadResult result =  qBaseServe.cosXmlService.abortMultiUpload(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        assertEquals(false,qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void test2() throws Exception{
        init();
        AbortMultiUploadRequest request = new AbortMultiUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.getCosPath();
        request.getUploadId();
        qBaseServe.cosXmlService.abortMultiUploadAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders() + "|" + result.printError());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        assertEquals(2, hasCompleted);
    }
}
