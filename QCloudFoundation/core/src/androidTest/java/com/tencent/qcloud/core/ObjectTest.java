package com.tencent.qcloud.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.qcloud.core.cos.object.CompleteMultiUploadRequest;
import com.tencent.qcloud.core.cos.object.CompleteMultiUploadResult;
import com.tencent.qcloud.core.cos.object.DeleteObjectRequest;
import com.tencent.qcloud.core.cos.object.DeleteObjectResult;
import com.tencent.qcloud.core.cos.object.GetObjectRequest;
import com.tencent.qcloud.core.cos.object.GetObjectResult;
import com.tencent.qcloud.core.cos.object.InitMultipartUploadRequest;
import com.tencent.qcloud.core.cos.object.InitMultipartUploadResult;
import com.tencent.qcloud.core.cos.object.UploadPartRequest;
import com.tencent.qcloud.core.cos.object.UploadPartResult;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
@RunWith(AndroidJUnit4.class)
public class ObjectTest {
    private QBaseServe qBaseServe;
    private String localDir;

    private String opCosPath = "/slice_upload.txt";

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        qBaseServe = QBaseServe.getInstance(appContext);
        localDir = appContext.getExternalCacheDir().getPath();

    }

    @Test
    public void testGetObjectRequest() throws Exception{
        GetObjectRequest request = new GetObjectRequest(localDir);
        request.setBucket(qBaseServe.bucket);
        request.setCosPath("/sample.txt");
        request.setSign(600,null,null);
        request.setRange(1);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                System.out.println("testGetBucketRequest : " + String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
        request.setRspContentType("text/json");
        request.setRspContentLanguage("CN");
        assertEquals("CN", request.getRspContentLanguage());

        GetObjectResult result = qBaseServe.cosXmlService.execute(request);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void testDeleteObjectRequest() throws Exception{
        DeleteObjectRequest request = new DeleteObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(opCosPath);
        request.setSign(600,null,null);
        DeleteObjectResult result =  qBaseServe.cosXmlService.execute(request);

        assertEquals(true,qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void testMultiUploadRequest() throws Exception{
        InitMultipartUploadResult initMultipartUploadResult = initUpload(opCosPath);
        assertEquals(true, qBaseServe.isSuccess(initMultipartUploadResult.getHttpCode()));
        String uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;

        UploadPartResult uploadPartResult = upload(opCosPath, uploadId);
        assertEquals(true, qBaseServe.isSuccess(uploadPartResult.getHttpCode()));
        String eTag = uploadPartResult.getETag();

        assertEquals(true, qBaseServe.isSuccess(complete(opCosPath, uploadId, eTag).getHttpCode()));
    }

    private InitMultipartUploadResult initUpload(String cosPath) throws QCloudClientException {
        InitMultipartUploadRequest request = new InitMultipartUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setSign(600,null,null);
        return qBaseServe.cosXmlService.execute(request);
    }

    private UploadPartResult upload(String cosPath, String uploadId) throws QCloudClientException {
        UploadPartRequest request = new UploadPartRequest();
        request.setBucket(qBaseServe.bucket);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.setPartNumber(1);
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        request.setData(data);
        request.setSign(600,null,null);
        return qBaseServe.cosXmlService.execute(request);
    }

    private CompleteMultiUploadResult complete(String cosPath, String uploadId, String etag)
            throws QCloudClientException {
        CompleteMultiUploadRequest request = new CompleteMultiUploadRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        request.setUploadId(uploadId);
        request.setPartNumberAndETag(1,etag);
        return qBaseServe.cosXmlService.execute(request);
    }


}
