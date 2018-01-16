package com.tencent.cos.xml.model.bucket;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ACLAccount;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/11.
 */
public class PutBucketACLRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public PutBucketACLRequestTest() {
        super(Application.class);
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
//
//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketACLRequest request = new PutBucketACLRequest(bucket);
//        request.setXCOSACL(COSACL.PRIVATE);
//        ACLAccount aclAccount = new ACLAccount();
//        aclAccount.addAccount("1131975903", "1131975903");
//        request.setXCOSGrantRead(aclAccount);
//        request.setXCOSGrantWrite(aclAccount);
//
//        PutBucketACLResult result = QService.getCosXmlClient(getContext()).putBucketACL(request);
//        Log.d(TAG, result.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String bucket = "androidtest";
//        PutBucketACLRequest request = new PutBucketACLRequest(bucket);
//        ACLAccount aclAccount = new ACLAccount();
//        aclAccount.addAccount("2832742109", "2832742109");
//        request.setXCOSGrantRead(aclAccount);
//        QService.getCosXmlClient(getContext()).putBucketACLAsync(request, new CosXmlResultListener() {
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