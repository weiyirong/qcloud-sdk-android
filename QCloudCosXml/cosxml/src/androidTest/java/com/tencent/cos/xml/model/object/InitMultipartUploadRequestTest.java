package com.tencent.cos.xml.model.object;

import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/6.
 */
public class InitMultipartUploadRequestTest {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;


    @Test
    public void setCacheControl() throws Exception {
    }

    @Test
    public void setContentDisposition() throws Exception {
    }

    @Test
    public void setContentEncoding() throws Exception {
    }

    @Test
    public void setExpires() throws Exception {
    }

    @Test
    public void setXCOSMeta() throws Exception {
    }

    @Test
    public void setXCOSACL() throws Exception {
    }

    @Test
    public void setXCOSACL1() throws Exception {
    }

    @Test
    public void setXCOSGrantRead() throws Exception {
    }

    @Test
    public void setXCOSGrantWrite() throws Exception {
    }

    @Test
    public void setXCOSReadWrite() throws Exception {
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

//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test_multi.txt";
//        InitMultipartUploadRequest request = new InitMultipartUploadRequest(bucket, cosPath);
//        InitMultipartUploadResult result = QService.getCosXmlClient(getContext()).initMultipartUpload(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test_multi.txt";
//        InitMultipartUploadRequest request = new InitMultipartUploadRequest(bucket, cosPath);
//        QService.getCosXmlClient(getContext()).initMultipartUploadAsync(request, new CosXmlResultListener() {
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