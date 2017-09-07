package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteMultiObjectResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class DeleteMultiObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public DeleteMultiObjectTest() {
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
        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setQuiet(false);
        request.setObjectList("/2/1491967729774.jpg");
        request.setObjectList("2/1491967729775.jpg");
        List<String> listObject = new ArrayList<String>();
        listObject.add("/2/1491967730522.jpg");
        listObject.add("2/1491968133919.jpg");
        request.setObjectList(listObject);
        request.getDelete();
        DeleteMultiObjectResult result =  qBaseServe.cosXmlService.deleteMultiObject(request);
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
        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setQuiet(false);
        request.setObjectList("/2/1491967729774.jpg");
        request.setObjectList("2/1491967729775.jpg");
        List<String> listObject = new ArrayList<String>();
        listObject.add("/2/1491967730522.jpg");
        listObject.add("2/1491968133919.jpg");
        request.setObjectList(listObject);
        request.getDelete();
        qBaseServe.cosXmlService.deleteMultiObjectAsync(request, new CosXmlResultListener() {
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
        assertEquals(1, hasCompleted);
    }

}
