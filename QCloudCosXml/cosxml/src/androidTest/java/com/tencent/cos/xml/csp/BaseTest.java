package com.tencent.cos.xml.csp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.MyQCloudSigner;
import com.tencent.cos.xml.QServer;
import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketResult;
import com.tencent.cos.xml.model.bucket.GetBucketACLRequest;
import com.tencent.cos.xml.model.bucket.GetBucketACLResult;
import com.tencent.cos.xml.model.bucket.GetBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.GetBucketCORSResult;
import com.tencent.cos.xml.model.bucket.GetBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.GetBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.GetBucketLocationRequest;
import com.tencent.cos.xml.model.bucket.GetBucketLocationResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.bucket.HeadBucketRequest;
import com.tencent.cos.xml.model.bucket.HeadBucketResult;
import com.tencent.cos.xml.model.bucket.ListMultiUploadsRequest;
import com.tencent.cos.xml.model.bucket.ListMultiUploadsResult;
import com.tencent.cos.xml.model.bucket.PutBucketACLRequest;
import com.tencent.cos.xml.model.bucket.PutBucketACLResult;
import com.tencent.cos.xml.model.bucket.PutBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.PutBucketCORSResult;
import com.tencent.cos.xml.model.bucket.PutBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.PutBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.PutBucketRequest;
import com.tencent.cos.xml.model.bucket.PutBucketResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteMultiObjectResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.OptionObjectRequest;
import com.tencent.cos.xml.model.object.OptionObjectResult;
import com.tencent.cos.xml.model.object.PostObjectRequest;
import com.tencent.cos.xml.model.object.PostObjectResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.tencent.cos.xml.model.tag.ListAllMyBuckets;
import com.tencent.cos.xml.model.tag.ListMultipartUploads;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.tencent.cos.xml.QServer.TAG;

/**
 * Created by rickenwang on 2018/8/29.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class BaseTest {


    private static CosXmlService cosXmlService;

    private static String bucketName = "rickenwang";
    private static String fileName = "tce.txt";
    private static String copyFileName = "copy_tce.txt";
    private static String postFileName = "post_tce.txt";
    private static String multiFileName = "multi_tce.txt";

    /**
     * 简单上传文件大小
     */
    private static final long simpleUploadFileSize = 64 * 1024;

    private Context context;
    private final String appid;
    private final String region;
    private final String fullBucketName;

    public BaseTest(final Context context, String appid, String bucket, String secretId, String secretKey,
                    final String region, String endpointSuffix) {

        this.context = context;
        this.appid = appid;
        this.region = region;
        bucketName = bucket;
        this.fullBucketName = TextUtils.isEmpty(appid)? bucketName : bucketName + "-" + appid;

        CosXmlServiceConfig.Builder configBuilder = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .setBucketInPath(true);

        if (!TextUtils.isEmpty(endpointSuffix)) {
            configBuilder.setEndpointSuffix(endpointSuffix);
        }

        CosXmlServiceConfig cosXmlServiceConfig = configBuilder.builder();

        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(secretId, secretKey, 3600);

        //cosXmlService = new CosXmlService(context, cosXmlServiceConfig, credentialProvider);

        cosXmlService = new CosXmlService(context, cosXmlServiceConfig, new MyQCloudSigner());

//        /** CSP 测试需要自定 DNS 解析  */
//        try {
//            String[] ips = new String[]{"203.195.206.83"};
//            cosXmlService.addCustomerDNS("yun.ccb.com", ips);
//            cosXmlService.addCustomerDNS("cos.wh.yun.ccb.com", ips);
//            cosXmlService.addCustomerDNS("rickenwang.wh.yun.ccb.com", ips);
//            cosXmlService.addCustomerDNS("service.cos.wh.yun.ccb.com", ips);
//        } catch (CosXmlClientException e) {
//            e.printStackTrace();
//        }
    }


    private void putBucketTest() {

        PutBucketRequest putBucketRequest = new PutBucketRequest(bucketName);
        PutBucketResult putBucketResult = null;

        try {
            putBucketResult = cosXmlService.putBucket(putBucketRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(putBucketResult);
        Assert.assertEquals(putBucketResult.httpCode, 200);
    }

    private void headBucketTest() {

        HeadBucketRequest headBucketRequest = new HeadBucketRequest(bucketName);
        HeadBucketResult headBucketResult = null;
        try {
            headBucketResult = cosXmlService.headBucket(headBucketRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(headBucketResult);
        Assert.assertEquals(headBucketResult.httpCode, 200);
    }


    private void getBucketTest() {

        GetBucketRequest getBucketRequest = new GetBucketRequest(bucketName);
        GetBucketResult getBucketResult = null;
        try {
            getBucketResult = cosXmlService.getBucket(getBucketRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(getBucketResult);
        Assert.assertEquals(getBucketResult.listBucket.name, fullBucketName);
    }


    private void getServiceTest() {

        GetServiceRequest getServiceRequest = new GetServiceRequest();
        GetServiceResult getServiceResult = null;
        try {
            getServiceResult = cosXmlService.getService(getServiceRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(getServiceResult);
        Assert.assertEquals(200, getServiceResult.httpCode);

        boolean hasBucketRickenwang = false;
        for (ListAllMyBuckets.Bucket bucket : getServiceResult.listAllMyBuckets.buckets) {
            if (bucket.name.equals(fullBucketName)) {
                hasBucketRickenwang = true;
            }
        }
        Assert.assertEquals(hasBucketRickenwang, true);
    }

    private void putBucketLifecycle() {

        PutBucketLifecycleRequest putBucketLifecycleRequest = new PutBucketLifecycleRequest(bucketName);
        LifecycleConfiguration.Rule rule = new LifecycleConfiguration.Rule();
        rule.id = "lifecycle_" + new Random(System.currentTimeMillis()).nextInt();
        rule.status = "Enabled";
        rule.expiration = new LifecycleConfiguration.Expiration();
        rule.expiration.days = 1;
        rule.filter = new LifecycleConfiguration.Filter();
        rule.filter.prefix = "logs";
        putBucketLifecycleRequest.setRuleList(rule);
        PutBucketLifecycleResult putBucketLifecycleResult = null;
        try {
            putBucketLifecycleResult = cosXmlService.putBucketLifecycle(putBucketLifecycleRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(200, putBucketLifecycleResult.httpCode);
    }


    private void getBucketLifecycle() {
        GetBucketLifecycleRequest getBucketLifecycleRequest = new GetBucketLifecycleRequest(bucketName);
        GetBucketLifecycleResult getBucketLifecycleResult = null;
        try {
            getBucketLifecycleResult = cosXmlService.getBucketLifecycle(getBucketLifecycleRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(200, getBucketLifecycleResult.httpCode);
        Assert.assertNotNull(getBucketLifecycleResult.lifecycleConfiguration);
    }


    private void deleteBucketLifecycle()  {
        DeleteBucketLifecycleRequest deleteBucketLifecycleRequest = new DeleteBucketLifecycleRequest(bucketName);
        DeleteBucketLifecycleResult deleteBucketLifecycleResult = null;
        try {
            deleteBucketLifecycleResult = cosXmlService.deleteBucketLifecycle(deleteBucketLifecycleRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(deleteBucketLifecycleResult);
        Assert.assertTrue(204 == deleteBucketLifecycleResult.httpCode);
    }

    private void putBucketCORS() {
        PutBucketCORSRequest putBucketCORSRequest = new PutBucketCORSRequest(bucketName);
        CORSConfiguration.CORSRule corsRule = new CORSConfiguration.CORSRule();
        corsRule.id = "cors" + new Random(System.currentTimeMillis()).nextInt();
        corsRule.maxAgeSeconds = 5000;
        corsRule.allowedOrigin = "cloud.tencent.com";
        corsRule.allowedMethod = new ArrayList<>();
        corsRule.allowedMethod.add("PUT");
        corsRule.allowedMethod.add("GET");
        corsRule.allowedHeader = new ArrayList<>();
        corsRule.allowedHeader.add("Host");
        corsRule.allowedHeader.add("Except");
        corsRule.exposeHeader = new ArrayList<>();
        corsRule.exposeHeader.add("x-cos-meta-1");
        List<CORSConfiguration.CORSRule> corsRules = new ArrayList<>();
        corsRules.add(corsRule);
        putBucketCORSRequest.addCORSRules(corsRules);
        PutBucketCORSResult putBucketCORSResult = null;
        try {
            putBucketCORSResult = cosXmlService.putBucketCORS(putBucketCORSRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(putBucketCORSRequest);
        Assert.assertEquals(200, putBucketCORSResult.httpCode);
    }

    private void getBucketCORS()  {
        GetBucketCORSRequest getBucketCORSRequest = new GetBucketCORSRequest(bucketName);
        GetBucketCORSResult getBucketCORSResult = null;
        try {
            getBucketCORSResult = cosXmlService.getBucketCORS(getBucketCORSRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(getBucketCORSResult);
        Assert.assertEquals(200, getBucketCORSResult.httpCode);
        Assert.assertNotNull(getBucketCORSResult.corsConfiguration);
    }

    private void deleteBucketCORS() {
        DeleteBucketCORSRequest deleteBucketCORSRequest = new DeleteBucketCORSRequest(bucketName);
        DeleteBucketCORSResult deleteBucketCORSResult = null;
        try {
            deleteBucketCORSResult = cosXmlService.deleteBucketCORS(deleteBucketCORSRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(deleteBucketCORSResult);
        Assert.assertTrue(204 == deleteBucketCORSResult.httpCode);
    }

    private void putBucketACL() {
        PutBucketACLRequest putBucketACLRequest = new PutBucketACLRequest(bucketName);
        ACLAccount aclAccount = new ACLAccount();
        aclAccount.addAccount(QServer.ownUin, QServer.ownUin);
        putBucketACLRequest.setXCOSGrantRead(aclAccount);
        putBucketACLRequest.setXCOSGrantWrite(aclAccount);
        putBucketACLRequest.setXCOSACL(COSACL.PRIVATE);
        PutBucketACLResult putBucketACLResult = null;
        try {
            putBucketACLResult = cosXmlService.putBucketACL(putBucketACLRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(putBucketACLResult);
        Assert.assertEquals(200, putBucketACLResult.httpCode);
    }

    private void getBucketACL() {
        GetBucketACLRequest getBucketACLRequest = new GetBucketACLRequest(bucketName);
        GetBucketACLResult getBucketACLResult = null;
        try {
            getBucketACLResult = cosXmlService.getBucketACL(getBucketACLRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(getBucketACLResult);
        Assert.assertEquals(200, getBucketACLResult.httpCode);
        Assert.assertNotNull(getBucketACLResult.accessControlPolicy);
    }

    private void getBucketLocation() {
        GetBucketLocationRequest getBucketLocationRequest = new GetBucketLocationRequest(bucketName);
        GetBucketLocationResult getBucketLocationResult = null;
        try {
            getBucketLocationResult = cosXmlService.getBucketLocation(getBucketLocationRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(getBucketLocationResult);
        Assert.assertEquals(200, getBucketLocationResult.httpCode);
        Assert.assertNotNull(getBucketLocationResult.locationConstraint);
    }


    private void putObject() {

        String localFilePath = null;

        try {
            localFilePath = createFile(context, fileName, simpleUploadFileSize);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "/"+fileName, localFilePath);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, complete + "/" + target);
            }
        });
        PutObjectResult putObjectResult = null;
        try {
            putObjectResult = cosXmlService.putObject(putObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(putObjectResult);
        Assert.assertEquals(200, putObjectResult.httpCode);
    }


    private void headObject() {

        HeadObjectRequest headObjectRequest = new HeadObjectRequest(bucketName, fileName);
        HeadObjectResult headObjectResult = null;
        try {
            headObjectResult = cosXmlService.headObject(headObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(headObjectResult);
        Assert.assertEquals(200, headObjectResult.httpCode);
    }


    private void optionObject() {

        String origin = "cloud.tencent.com";
        String method = "GET";
        OptionObjectRequest optionObjectRequest = new OptionObjectRequest(bucketName, fileName, origin, method);
        // optionObjectRequest.setAccessControlHeaders("Authorization");
        OptionObjectResult optionObjectResult = null;
        try {
            optionObjectResult = cosXmlService.optionObject(optionObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(optionObjectResult);
        Assert.assertEquals(200, optionObjectResult.httpCode);
    }

    private void putObjectACL()  {
        PutObjectACLRequest putObjectACLRequest = new PutObjectACLRequest(bucketName, fileName);
        putObjectACLRequest.setXCOSACL(COSACL.PRIVATE);
        ACLAccount aclAccount = new ACLAccount();
        aclAccount.addAccount(QServer.ownUin, QServer.ownUin);
        putObjectACLRequest.setXCOSGrantRead(aclAccount);
        putObjectACLRequest.setXCOSGrantWrite(aclAccount);
        PutObjectACLResult putObjectACLResult = null;
        try {
            putObjectACLResult = cosXmlService.putObjectACL(putObjectACLRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(putObjectACLResult);
        Assert.assertEquals(200, putObjectACLResult.httpCode);
    }



    private void getObjectACL()  {
        GetObjectACLRequest getObjectACLRequest = new GetObjectACLRequest(bucketName, fileName);
        GetObjectACLResult getObjectACLResult = null;
        try {
            getObjectACLResult = cosXmlService.getObjectACL(getObjectACLRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(getObjectACLResult);
        Assert.assertEquals(200, getObjectACLResult.httpCode);
        Assert.assertNotNull(getObjectACLResult.accessControlPolicy);
    }

    private void copyObject() {

        String destCosPath = copyFileName;
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                appid, bucketName, region, fileName);
        CopyObjectRequest copyObjectRequest = null;
        CopyObjectResult copyObjectResult = null;
        try {
            copyObjectRequest = new CopyObjectRequest(bucketName, destCosPath, copySourceStruct);
            copyObjectResult = cosXmlService.copyObject(copyObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
//        copyObjectRequest.setCopyMetaDataDirective(MetaDataDirective.REPLACED);
//        copyObjectRequest.setRequestHeaders("x-cos-meta-xml", "cos");
        Assert.assertNotNull(copyObjectResult);
        Assert.assertEquals(200, copyObjectResult.httpCode);
        Assert.assertNotNull(copyObjectResult.copyObject);
    }


    private void getObject() {

        String savePath = context.getExternalCacheDir().getPath();
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName, savePath);
        getObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, complete + "/" + target);
            }
        });
        GetObjectResult getObjectResult = null;
        try {
            getObjectResult = cosXmlService.getObject(getObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(getObjectResult);
        Assert.assertEquals(200, getObjectResult.httpCode);
        File file = new File(savePath, fileName);
        System.out.println("file path is " + getObjectRequest.getDownloadPath());
        Assert.assertEquals(file.length(), simpleUploadFileSize);

        //QServer.deleteLocalFile(getObjectRequest.getDownloadPath());
    }


    /**
     * Test initMultipartUpload、ListParts、UploadPart、CompleteMultiUpload
     *
     * @throws CosXmlServiceException
     * @throws CosXmlClientException
     */
    private void sliceUploadObject() {

        String fileName = multiFileName;

        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(bucketName, fileName);
        InitMultipartUploadResult initMultipartUploadResult = null;
        try {
            initMultipartUploadResult = cosXmlService.initMultipartUpload(initMultipartUploadRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(initMultipartUploadResult);
        Assert.assertEquals(200, initMultipartUploadResult.httpCode);
        Assert.assertNotNull(initMultipartUploadResult.initMultipartUpload.uploadId);

        String uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, fileName, uploadId);
        ListPartsResult listPartsResult = null;
        try {
            listPartsResult = cosXmlService.listParts(listPartsRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(listPartsResult);
        Assert.assertEquals(200, listPartsResult.httpCode);
        Assert.assertNotNull(listPartsResult.listParts);

        int partNumber = 1;
        String localFilePath = null;
        try {
            localFilePath = createFile(context, partNumber + fileName, 1024 * 1024 * 2);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }


        UploadPartRequest uploadPartRequest1 = new UploadPartRequest(bucketName, fileName, partNumber, localFilePath, uploadId);
        uploadPartRequest1.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, complete + "/" + target);
            }
        });
        UploadPartResult uploadPartResult1 = null;
        try {
            uploadPartResult1 = cosXmlService.uploadPart(uploadPartRequest1);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(uploadPartResult1);
        Assert.assertEquals(200, uploadPartResult1.httpCode);
        Assert.assertNotNull(uploadPartResult1.eTag);

        partNumber = 2;
        String newLocalFilePath = null;
        try {
            newLocalFilePath = createFile(context, partNumber + fileName, 1024 * 512);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        UploadPartRequest uploadPartRequest2 = new UploadPartRequest(bucketName, fileName, partNumber, newLocalFilePath, uploadId);
        uploadPartRequest2.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, complete + "/" + target);
            }
        });
        UploadPartResult uploadPartResult2 = null;
        try {
            uploadPartResult2 = cosXmlService.uploadPart(uploadPartRequest2);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(uploadPartResult2);
        Assert.assertEquals(200, uploadPartResult2.httpCode);
        Assert.assertNotNull(uploadPartResult2.eTag);


        CompleteMultiUploadRequest completeMultiUploadRequest = new CompleteMultiUploadRequest(bucketName, fileName, uploadId, null);
        completeMultiUploadRequest.setPartNumberAndETag(1, uploadPartResult1.eTag);
        completeMultiUploadRequest.setPartNumberAndETag(2, uploadPartResult2.eTag);
        CompleteMultiUploadResult completeMultiUploadResult = null;
        try {
            completeMultiUploadResult = cosXmlService.completeMultiUpload(completeMultiUploadRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(completeMultiUploadResult);
        Assert.assertEquals(completeMultiUploadResult.httpCode, 200);
        Assert.assertNotNull(completeMultiUploadResult.completeMultipartUpload);
    }

    private void deleteAllUploadIdOfBucket() {

        ListMultiUploadsRequest listMultiUploadsRequest = new ListMultiUploadsRequest(bucketName);
        ListMultiUploadsResult listMultiUploadsResult = null;
        try {
            listMultiUploadsResult = cosXmlService.listMultiUploads(listMultiUploadsRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Log.d(TAG, listMultiUploadsResult.printResult());
        List<ListMultipartUploads.Upload> uploadList = listMultiUploadsResult.listMultipartUploads.uploads;
        if( uploadList != null && uploadList.size() > 0){
            for(ListMultipartUploads.Upload upload : uploadList){
                String uploadId = upload.uploadID;
                String key = upload.key;
                AbortMultiUploadRequest abortMultiUploadRequest = new AbortMultiUploadRequest(bucketName, key, uploadId);
                AbortMultiUploadResult abortMultiUploadResult = null;
                try {
                    abortMultiUploadResult = cosXmlService.abortMultiUpload(abortMultiUploadRequest);
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, abortMultiUploadResult.printResult());
            }
        }
        if(listMultiUploadsResult.listMultipartUploads.isTruncated){
            deleteAllUploadIdOfBucket();
        }
    }

    private void deleteObject() {

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, fileName);
        DeleteObjectResult deleteObjectResult = null;
        try {
            deleteObjectResult = cosXmlService.deleteObject(deleteObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(deleteObjectResult);
        Assert.assertTrue(204 == deleteObjectResult.httpCode);
    }

    private void deleteMultiObject() {
        DeleteMultiObjectRequest deleteMultiObjectRequest = new DeleteMultiObjectRequest(bucketName, null);
        deleteMultiObjectRequest.setObjectList(copyFileName);
        deleteMultiObjectRequest.setObjectList(multiFileName);
        deleteMultiObjectRequest.setQuiet(false);
        DeleteMultiObjectResult deleteMultiObjectResult = null;
        try {
            deleteMultiObjectResult = cosXmlService.deleteMultiObject(deleteMultiObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(deleteMultiObjectResult);
        Assert.assertEquals(200, deleteMultiObjectResult.httpCode);
    }

    private void deleteBucketTest() {

        DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
        DeleteBucketResult deleteBucketResult = null;

        try {
            deleteBucketResult = cosXmlService.deleteBucket(deleteBucketRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(deleteBucketResult);
        Assert.assertTrue(deleteBucketResult.httpCode == 204);
    }


    /**
     * 同步简单请求测试
     */
    public void testSimpleSyncMethod() {
        if(!QServer.cspTest)return;
        //putBucketTest();
        //headBucketTest();
        //getBucketTest();
        getServiceTest();

//        putBucketLifecycle();
//        getBucketLifecycle();
//
//        putBucketCORS();
//        getBucketCORS();
//
//
//        putBucketACL();
//        getBucketACL();
//
//        getBucketLocation();
//
//
//        putObject();
//        headObject();
//        optionObject();
//
//        sliceUploadObject();
//        putObjectACL();
//        getObjectACL();
//        copyObject();
//        getObject();
//
//        deleteAllUploadIdOfBucket();
//        deleteObject();
//        deleteMultiObject();
//
//        deleteBucketCORS();
//        deleteBucketLifecycle();
//        deleteBucketTest();

    }


    public void testCspTrunkedDownload() {
        if(!QServer.cspTest)return;
        putBucketTest();
        putObject();
        getObject();
    }



    // TODO: 2018/9/3  tce don't need
    private void postObject() {

        String cosPath = postFileName;
        String srcPath = null;
        try {
            srcPath = QServer.createFile(context, 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[0];
        try {
            data = "this is post object test".getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        PostObjectRequest postObjectRequest = new PostObjectRequest(bucketName, cosPath, data);
        postObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d("XIAO", "progress =" + complete / target);
            }
        });

        PostObjectResult postObjectResult = null;
        try {
            postObjectResult = cosXmlService.postObject(postObjectRequest);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        QServer.deleteLocalFile(srcPath);
    }


//    @Test public void partCopyObject() throws CosXmlServiceException, CosXmlClientException {
//
//        String destCosPath = "part_copy_" + fileName;
//        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
//                QServer.appid, bucketName, QServer.region, fileName);
//        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(bucketName, destCosPath);
//        InitMultipartUploadResult initMultipartUploadResult = cosXmlService.initMultipartUpload(initMultipartUploadRequest);
//        String uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
//        UploadPartCopyRequest uploadPartCopyRequest = new UploadPartCopyRequest(bucketName, destCosPath, 1, uploadId,
//                copySourceStruct);
//        UploadPartCopyResult uploadPartCopyResult = cosXmlService.copyObject(uploadPartCopyRequest);
//        String eTag = uploadPartCopyResult.copyObject.eTag;
//        Map<Integer, String> partNumberAndETag = new HashMap<>();
//        partNumberAndETag.put(1, eTag);
//        CompleteMultiUploadRequest completeMultiUploadRequest = new CompleteMultiUploadRequest(bucketName, destCosPath,
//                uploadId, partNumberAndETag);
//        CompleteMultiUploadResult completeMultiUploadResult = cosXmlService.completeMultiUpload(completeMultiUploadRequest);
//        Log.d(TAG, completeMultiUploadResult.printResult());
//        //QServer.deleteCOSObject(context, bucket, destCosPath);
//    }


    private String createFile(Context context, String fileName, long fileLength) throws IOException {

        String cacheFilePath = context.getExternalCacheDir().getPath() + File.separator
                + fileName;
        RandomAccessFile accessFile = new RandomAccessFile(cacheFilePath, "rws");
        //accessFile.setLength(fileLength);
        byte[] bytes = new byte[(int) fileLength];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i % 100 + 35);
        }
        accessFile.write(bytes);
        accessFile.close();
        return cacheFilePath;
    }
}
