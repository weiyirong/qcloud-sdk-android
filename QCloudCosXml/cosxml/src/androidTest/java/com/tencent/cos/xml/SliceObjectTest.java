package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class SliceObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public SliceObjectTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    public String testInitMultipartUploadRequest(String cosPath) throws Exception{
        InitMultipartUploadRequest request = new InitMultipartUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setSign(600,null,null);
        InitMultipartUploadResult result =  qBaseServe.cosXmlService.initMultipartUpload(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return result.initMultipartUpload.uploadId;
        }else {
            return null;
        }
    }

    public boolean testInitMultipartUploadRequest2(String cosPath,final InitMultipartUploadResult initMultipartUploadResult ) throws Exception{
        hasCompleted = 0;
        InitMultipartUploadRequest request = new InitMultipartUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.initMultipartUploadAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
                initMultipartUploadResult.initMultipartUpload = ((InitMultipartUploadResult)result).initMultipartUpload;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    public void testListPartsRequest(String cosPath, String uploadId) throws Exception{
        ListPartsRequest request = new ListPartsRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        Set<String> checkHeader = new HashSet<String>();
        checkHeader.add("host");
        Set<String> checkParams = new HashSet<String>();
        checkParams.add("uploadId");
        request.setSign(600,checkHeader,checkParams);
        ListPartsResult result =  qBaseServe.cosXmlService.listParts(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

    public boolean testListPartsRequest2(String cosPath, String uploadId) throws Exception{
        ListPartsRequest request = new ListPartsRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        Set<String> checkHeader = new HashSet<String>();
        checkHeader.add("host");
        Set<String> checkParams = new HashSet<String>();
        checkParams.add("uploadId");
        request.setSign(600,checkHeader,checkParams);
        qBaseServe.cosXmlService.listPartsAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    public String testUploadPartRequest(String cosPath, String uploadId) throws Exception{
        UploadPartRequest request = new UploadPartRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.setPartNumber(1);
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        request.setData(data);
        request.setSign(600,null,null);
        UploadPartResult result =  qBaseServe.cosXmlService.uploadPart(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return result.getETag();
        }else {
            return null;
        }
    }



    public boolean testUploadPartRequest2(String cosPath, String uploadId, final UploadPartResult uploadPartResult) throws Exception{
        UploadPartRequest request = new UploadPartRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.setPartNumber(1);
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        request.setData(data);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.uploadPartAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
                uploadPartResult.setHeaders(result.getHeaders());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    public boolean testCompleteMultiUploadRequest(String cosPath, String uploadId, String etag) throws Exception{
        CompleteMultiUploadRequest request = new CompleteMultiUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.setPartNumberAndETag(1,etag);
        CompleteMultiUploadResult result =  qBaseServe.cosXmlService.completeMultiUpload(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testCompleteMultiUploadRequest2(String cosPath, String uploadId, String etag) throws Exception{
        CompleteMultiUploadRequest request = new CompleteMultiUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.setPartNumberAndETag(1,etag);
        qBaseServe.cosXmlService.completeMultiUploadAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    @Test
    public void test1() throws Exception{
        init();
        String cosPath = "/slice_upload.txt";
        String uploadId  = testInitMultipartUploadRequest(cosPath);
        testListPartsRequest(cosPath,uploadId);
        String etag = testUploadPartRequest(cosPath,uploadId);
        boolean result = testCompleteMultiUploadRequest(cosPath, uploadId, etag);
        assertEquals(true, result);
    }

    @Test
    public void test2() throws Exception{
        init();
        String cosPath = "/slice_upload.txt";
        InitMultipartUploadResult initMultipartUploadResult = new InitMultipartUploadResult();
        assertEquals(true,testInitMultipartUploadRequest2(cosPath,initMultipartUploadResult));
        assertEquals(true,testListPartsRequest2(cosPath, initMultipartUploadResult.initMultipartUpload.uploadId));
        UploadPartResult uploadPartResult = new UploadPartResult();
        assertEquals(true,testUploadPartRequest2(cosPath,initMultipartUploadResult.initMultipartUpload.uploadId,uploadPartResult));
        assertEquals(true,testCompleteMultiUploadRequest2(cosPath,initMultipartUploadResult.initMultipartUpload.uploadId,
                uploadPartResult.getETag()));
    }
}
