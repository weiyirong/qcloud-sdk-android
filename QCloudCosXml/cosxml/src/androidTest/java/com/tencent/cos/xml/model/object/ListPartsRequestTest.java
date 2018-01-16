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

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/6.
 */
public class ListPartsRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public ListPartsRequestTest() {
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
    public void setUploadId() throws Exception {
    }

    @Test
    public void getUploadId() throws Exception {
    }

    @Test
    public void setMaxParts() throws Exception {
    }

    @Test
    public void getMaxParts() throws Exception {
    }

    @Test
    public void setPartNumberMarker() throws Exception {
    }

    @Test
    public void getPartNumberMarker() throws Exception {
    }

    @Test
    public void setEncodingType() throws Exception {
    }

    @Test
    public void getEncodingType() throws Exception {
    }

//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test_multi.txt";
//        String uploadId = "1512529271877fc41c822dc6c8dd877c9e338b50c7aca043ef6a8d309ee1474aec8652c907";
//        ListPartsRequest request = new ListPartsRequest(bucket, cosPath, uploadId);
//        ListPartsResult result = QService.getCosXmlClient(getContext()).listParts(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test_multi.txt";
//        String uploadId = "1512529271877fc41c822dc6c8dd877c9e338b50c7aca043ef6a8d309ee1474aec8652c907";
//        ListPartsRequest request = new ListPartsRequest(bucket, cosPath, uploadId);
//        QService.getCosXmlClient(getContext()).listPartsAsync(request, new CosXmlResultListener() {
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