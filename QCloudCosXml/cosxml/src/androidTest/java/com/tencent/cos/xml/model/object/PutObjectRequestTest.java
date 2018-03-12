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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/5.
 */
public class PutObjectRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;
    public PutObjectRequestTest() {
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
    public void setProgressListener() throws Exception {
    }

    @Test
    public void getProgressListener() throws Exception {
    }

    @Test
    public void setSrcPath() throws Exception {
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
    public void setCacheControl() throws Exception {
    }

    @Test
    public void setContentDisposition() throws Exception {
    }

    @Test
    public void setContentEncodeing() throws Exception {
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

//    @Test
//    public void test1() throws UnsupportedEncodingException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        byte[] data = new byte[1024 * 1024];
//        String cosPath = "中文//.../aa.txt";
//        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
//                data);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, " complete：" + complete + "| target: " + target);
//            }
//        });
//        PutObjectResult result = QService.getCosXmlClient(getContext()).putObject(request);
//        Log.d(TAG, result.printResult());
//
//        QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String srcPath = QService.createFile(1024 * 1024);
//        String cosPath = "putobject.txt";
//        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
//                srcPath);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, " complete：" + complete + "| target: " + target);
//            }
//        });
//        PutObjectResult result = QService.getCosXmlClient(getContext()).putObject(request);
//        Log.d(TAG, result.printResult());
//
//        QService.delete(QService.getCosXmlClient(getContext()), bucket, "append.txt");
//    }
//
//    @Test
//    public void test3() throws IOException, CosXmlServiceException, CosXmlClientException {
//        final String bucket = "androidtest";
//        String srcPath = QService.createFile(1024 * 1024);
//        final String cosPath = "putobject.txt";
//        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
//                srcPath);
//        request.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//                Log.d(TAG, " complete：" + complete + "| target: " + target);
//            }
//        });
//        QService.getCosXmlClient(getContext()).putObjectAsync(request, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG, result.printResult());
//                try {
//                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
//                } catch (CosXmlServiceException e) {
//                    e.printStackTrace();
//                } catch (CosXmlClientException e) {
//                    e.printStackTrace();
//                }
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
//
    @Test
    public void test3() throws IOException, CosXmlServiceException, CosXmlClientException {
        final String bucket = "androidtest";
        String srcPath = QService.createFile(2 * 1024 * 1024);
        final String cosPath = "putobject.txt";
        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
                new FileInputStream(srcPath));
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target);
            }
        });
        QService.getCosXmlClient(getContext()).putObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(100);
                QService.getCosXmlClient(getContext()).cancel(request);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void test4() throws IOException, CosXmlServiceException, CosXmlClientException {
        final String bucket = "androidtest";
        String srcPath = QService.createFile(2 * 1024 * 1024);
        final String cosPath = "putobject.txt";
        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
                new FileInputStream(srcPath));
        request.setRequestHeaders("x-cos-server-side-encryption", "AES256");
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target);
            }
        });
        QService.getCosXmlClient(getContext()).putObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                Log.d(TAG, result.headers.toString());
                assertEquals(true, result.headers.get("x-cos-server-side-encryption").contains("AES256"));
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}