package com.tencent.cos.xml.model.object;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/6.
 */
public class DeleteMultiObjectRequestTest extends ApplicationTestCase {
    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public DeleteMultiObjectRequestTest() {
        super(Application.class);
    }

    @Test
    public void getMethod() throws Exception {
    }

    @Test
    public void getQueryString() throws Exception {
    }

    @Test
    public void getRequestBody() throws Exception {
    }

    @Test
    public void checkParameters() throws Exception {
    }

    @Test
    public void getPath() throws Exception {
    }

    @Test
    public void setQuiet() throws Exception {
    }

    @Test
    public void setObjectList() throws Exception {
    }

    @Test
    public void setObjectList1() throws Exception {
    }

    @Test
    public void getDelete() throws Exception {
    }

//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        List<String> listObject = new ArrayList<>();
//        listObject.add("/xml_test_copy.txt");
//        listObject.add("/1511858966419.txt");
//        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest(bucket, null);
//        request.setQuiet(false);
//        request.setObjectList(listObject);
//        DeleteMultiObjectResult result = QService.getCosXmlClient(getContext()).deleteMultiObject(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        List<String> listObject = new ArrayList<>();
//        listObject.add("/xml_test_copy.txt");
//        listObject.add("/1511858966419.txt");
//        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest(bucket, null);
//        request.setQuiet(true);
//        request.setObjectList(listObject);
//        QService.getCosXmlClient(getContext()).deleteMultiObjectAsync(request, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG, result.printResult());
//                isOver = true;
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
//                isOver = true;
//            }
//        });
//
//        while (!isOver){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

}