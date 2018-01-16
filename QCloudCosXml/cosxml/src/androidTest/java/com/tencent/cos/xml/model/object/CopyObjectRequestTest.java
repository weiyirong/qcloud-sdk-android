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
public class CopyObjectRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public CopyObjectRequestTest() {
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
    public void setCosPath() throws Exception {
    }

    @Test
    public void getCosPath() throws Exception {
    }

    @Test
    public void setCopySource() throws Exception {
    }

    @Test
    public void getCopySource() throws Exception {
    }

    @Test
    public void setCopyMetaDataDirective() throws Exception {
    }

    @Test
    public void setCopyIfModifiedSince() throws Exception {
    }

    @Test
    public void setCopyIfUnmodifiedSince() throws Exception {
    }

    @Test
    public void setCopyIfMatch() throws Exception {
    }

    @Test
    public void setCopyIfNoneMatch() throws Exception {
    }

    @Test
    public void setCosStorageClass() throws Exception {
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
    public void setXCOSMeta() throws Exception {
    }
//
//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        String cosPath = "xml_test_copy.txt";
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                QService.appid, bucket, QService.region, "AWS-S3.doc");
//        CopyObjectRequest request = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
//        CopyObjectResult result = QService.getCosXmlClient(getContext()).copyObject(request);
//        Log.d(TAG, result.printResult());
//        QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        final String bucket = "androidtest";
//        final String cosPath = "xml_test_copy2.txt";
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                QService.appid, bucket, QService.region, "xml_test.txt");
//        CopyObjectRequest request = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
//        QService.getCosXmlClient(getContext()).copyObjectAsync(request, new CosXmlResultListener() {
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

}