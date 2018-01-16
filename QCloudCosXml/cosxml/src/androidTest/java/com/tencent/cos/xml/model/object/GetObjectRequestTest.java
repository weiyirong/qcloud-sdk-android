package com.tencent.cos.xml.model.object;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/5.
 */
public class GetObjectRequestTest extends ApplicationTestCase {
    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public GetObjectRequestTest() {
        super(Application.class);
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void setRspContentType() throws Exception {
    }

    @Test
    public void getRspContentType() throws Exception {
    }

    @Test
    public void setRspContentLanguage() throws Exception {
    }

    @Test
    public void getRspContentLanguage() throws Exception {
    }

    @Test
    public void setRspExpires() throws Exception {
    }

    @Test
    public void getRspExpires() throws Exception {
    }

    @Test
    public void setRspCacheControl() throws Exception {
    }

    @Test
    public void getRspCacheControl() throws Exception {
    }

    @Test
    public void setRspContentDispositon() throws Exception {
    }

    @Test
    public void getRspContentDispositon() throws Exception {
    }

    @Test
    public void setRspContentEncoding() throws Exception {
    }

    @Test
    public void getRspContentEncoding() throws Exception {
    }

    @Test
    public void setRange() throws Exception {
    }

    @Test
    public void setRange1() throws Exception {
    }

    @Test
    public void getRange() throws Exception {
    }

    @Test
    public void setIfModifiedSince() throws Exception {
    }

    @Test
    public void setProgressListener() throws Exception {
    }

    @Test
    public void getProgressListener() throws Exception {
    }

    @Test
    public void setSavePath() throws Exception {
    }

    @Test
    public void getSavePath() throws Exception {
    }

    @Test
    public void getDownloadPath() throws Exception {
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
//        String cosPath = "xml_test.txt";
//        String savePath = Environment.getExternalStorageDirectory().getPath() + "/";
//        GetObjectRequest request = new GetObjectRequest(bucket, cosPath, savePath);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, " complete：" + complete + "| target: " + target);
//            }
//        });
//        GetObjectResult result = QService.getCosXmlClient(getContext()).getObject(request);
//        Log.d(TAG, result.printResult());
//
//        QService.delete(request.getDownloadPath());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test.txt";
//        String savePath = Environment.getExternalStorageDirectory().getPath() + "/";
//        GetObjectRequest request = new GetObjectRequest(bucket, cosPath, savePath);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, " complete：" + complete + "| target: " + target);
//            }
//        });
//        QService.getCosXmlClient(getContext()).getObjectAsync(request, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG, result.printResult());
//                QService.delete(((GetObjectRequest)request).getDownloadPath());
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