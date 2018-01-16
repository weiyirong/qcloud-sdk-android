package com.tencent.cos.xml.model.object;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/6.
 */
public class UploadPartRequestTest extends ApplicationTestCase{

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public UploadPartRequestTest() {
        super(Application.class);
    }

    @Test
    public void setPartNumber() throws Exception {
    }

    @Test
    public void getPartNumber() throws Exception {
    }

    @Test
    public void setUploadId() throws Exception {
    }

    @Test
    public void getUploadId() throws Exception {
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
    public void setSrcPath() throws Exception {
    }

    @Test
    public void setSrcPath1() throws Exception {
    }

    @Test
    public void getSrcPath() throws Exception {
    }

    @Test
    public void setData() throws Exception {
    }

    @Test
    public void getData() throws Exception {
    }

    @Test
    public void setInputStream() throws Exception {
    }

    @Test
    public void getInputStream() throws Exception {
    }

    @Test
    public void getFileLength() throws Exception {
    }

    @Test
    public void setProgressListener() throws Exception {
    }

    @Test
    public void getProgressListener() throws Exception {
    }

//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        final String cosPath = "xml_test_multi.txt";
//        String uploadId = "1512553259e5ae996052e1cd243782ec2346dba7eb5a008998efd1813d1f068656ed5c9421";
//        int partNumber = 1;
//        String srcPath = QService.createFile(1024 * 1024);
//        UploadPartRequest request = new UploadPartRequest(bucket, cosPath,partNumber, srcPath, uploadId);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, "complete: " + complete + "| target: " + target);
//            }
//        });
//        UploadPartResult result = QService.getCosXmlClient(getContext()).uploadPart(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test_multi.txt";
//        String uploadId = "1512553259e5ae996052e1cd243782ec2346dba7eb5a008998efd1813d1f068656ed5c9421";
//        int partNumber = 1;
//        String srcPath = QService.createFile(1024 * 1024);
//        UploadPartRequest request = new UploadPartRequest(bucket, cosPath,partNumber, srcPath, uploadId);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, "complete: " + complete + "| target: " + target);
//            }
//        });
//        QService.getCosXmlClient(getContext()).uploadPartAsync(request, new CosXmlResultListener() {
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