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
import com.tencent.cos.xml.model.bucket.ListMultiUploadsRequest;
import com.tencent.cos.xml.model.bucket.ListMultiUploadsResult;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/12.
 */
public class OptionObjectRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public OptionObjectRequestTest() {
        super(Application.class);
    }
    @Test
    public void getMethod() throws Exception {
    }

    @Test
    public void getRequestBody() throws Exception {
    }

    @Test
    public void checkParameters() throws Exception {
    }

    @Test
    public void setOrigin() throws Exception {
    }

    @Test
    public void getOrigin() throws Exception {
    }

    @Test
    public void setAccessControlMethod() throws Exception {
    }

    @Test
    public void getAccessControlMethod() throws Exception {
    }

    @Test
    public void setAccessControlHeaders() throws Exception {
    }

    @Test
    public void getAccessControlHeaders() throws Exception {
    }

//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "multi.txt";
//        String origin = "cloud.tencent.com";
//        String method = "GET";
//        OptionObjectRequest request = new OptionObjectRequest(bucket, cosPath, origin, method);
//        OptionObjectResult result = QService.getCosXmlClient(getContext()).optionObject(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "multi.txt";
//        String origin = "cloud.tencent.com";
//        String method = "GET";
//        OptionObjectRequest request = new OptionObjectRequest(bucket, cosPath, origin, method);
//        QService.getCosXmlClient(getContext()).optionObjectAsync(request, new CosXmlResultListener() {
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