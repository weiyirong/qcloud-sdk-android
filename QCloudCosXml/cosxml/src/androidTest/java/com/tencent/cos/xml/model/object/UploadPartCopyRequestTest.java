package com.tencent.cos.xml.model.object;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.CopyObjectService;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/12.
 */
public class UploadPartCopyRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public UploadPartCopyRequestTest() {
        super(Application.class);
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
    public void setCopySource() throws Exception {
    }

    @Test
    public void setCopyRanage() throws Exception {
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
    public void getCopySource() throws Exception {
    }

//    @Test
//    public void test1() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String sourceBucket = "androidtest";
//        String sourceAppid = "1253960454";
//        String sourceOrigin = Region.AP_Guangzhou.getRegion();
//        String sourceCosPath = "upload_service3.txt";
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                sourceAppid, sourceBucket, sourceOrigin, sourceCosPath);
//        String bucket = "androidtest";
//        String cosPath = "copy_test_3.txt";
//        String uploadId = "";
//
//        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
//        InitMultipartUploadResult initMultipartUploadResult = QService.getCosXmlClient(getContext()).initMultipartUpload(initMultipartUploadRequest);
//        uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
//        Log.d(TAG, initMultipartUploadResult.printResult());
//
//        UploadPartCopyRequest request = new UploadPartCopyRequest(bucket, cosPath,
//                1, uploadId, copySourceStruct);
//        UploadPartCopyResult result = QService.getCosXmlClient(getContext()).copyObject(request);
//        Log.d(TAG, result.printResult());
//
//        CompleteMultiUploadRequest completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath, uploadId,
//                null);
//        completeMultiUploadRequest.setPartNumberAndETag(1, result.copyObject.eTag);
//        CompleteMultiUploadResult completeMultiUploadResult = QService.getCosXmlClient(getContext()).completeMultiUpload(completeMultiUploadRequest);
//        Log.d(TAG, completeMultiUploadResult.printResult());
//    }
//
//    @Test
//    public void test2() throws IOException, CosXmlServiceException, CosXmlClientException {
//        String sourceBucket = "androidtest";
//        String sourceAppid = "1253960454";
//        String sourceOrigin = Region.AP_Guangzhou.getRegion();
//        String sourceCosPath = "upload_service3.txt";
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                sourceAppid, sourceBucket, sourceOrigin, sourceCosPath);
//        String bucket = "androidtest";
//        String cosPath = "copy_test_4.txt";
//        String uploadId = "";
//
//        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
//        InitMultipartUploadResult initMultipartUploadResult = QService.getCosXmlClient(getContext()).initMultipartUpload(initMultipartUploadRequest);
//        uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
//        Log.d(TAG, initMultipartUploadResult.printResult());
//
//
//        final String[] eTag = new String[1];
//        UploadPartCopyRequest request = new UploadPartCopyRequest(bucket, cosPath,
//                1, uploadId, copySourceStruct);
//        QService.getCosXmlClient(getContext()).copyObjectAsync(request, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                Log.d(TAG, result.printResult());
//                eTag[0] = ((UploadPartCopyResult)result).copyObject.eTag;
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
//        CompleteMultiUploadRequest completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath, uploadId,
//                null);
//        completeMultiUploadRequest.setPartNumberAndETag(1, eTag[0]);
//        CompleteMultiUploadResult completeMultiUploadResult = QService.getCosXmlClient(getContext()).completeMultiUpload(completeMultiUploadRequest);
//        Log.d(TAG, completeMultiUploadResult.printResult());
//
//    }


}