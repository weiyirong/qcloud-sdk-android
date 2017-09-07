package com.tencent.cos.xml;


import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSStorageClass;
import com.tencent.cos.xml.common.Permission;
import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketTaggingResult;
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
import com.tencent.cos.xml.model.bucket.GetBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingResult;
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
import com.tencent.cos.xml.model.bucket.PutBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.AppendObjectResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
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
import com.tencent.cos.xml.model.object.MultipartUploadHelper;
import com.tencent.cos.xml.model.object.OptionObjectRequest;
import com.tencent.cos.xml.model.object.OptionObjectResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;
import com.tencent.cos.xml.model.tag.AbortIncompleteMultiUpload;
import com.tencent.cos.xml.model.tag.CORSRule;
import com.tencent.cos.xml.model.tag.Rule;
import com.tencent.cos.xml.model.tag.Tag;
import com.tencent.cos.xml.sign.CosXmlLocalCredentialProvider;
import com.tencent.cos.xml.utils.FileUtils;
import com.tencent.cos.xml.utils.MD5Utils;
import com.tencent.cos.xml.utils.SHA1Utils;
import com.tencent.cos.xml.utils.StringUtils;
import com.tencent.qcloud.network.QCloudProgressListener;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Created by bradyxiao on 2017/8/23.
 * author bradyxiao
 */
public class CosXmlUnitTest extends ApplicationTestCase<Application> {
    private static final String TAG = "Unit_Test";
    public String bucket = "xy2";
    public String appid = "1253653367";
    public String region = "cn-south";
    public CosXmlService cosXmlService;
    public boolean hasInit = false;

    public CosXmlUnitTest() {
        super(Application.class);
    }

    public void grantPressmit(){
    }
    public void init(){
        if(!hasInit){
            CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig(appid,region);
            cosXmlServiceConfig.setSocketTimeout(450000);
            cosXmlService = new CosXmlService(getContext(),cosXmlServiceConfig,
                    new CosXmlLocalCredentialProvider("AKIDPiqmW3qcgXVSKN8jngPzRhvxzYyDL5qP","EH8oHoLgpmJmBQUM1Uoywjmv7EFzd5OJ",600));
            hasInit = true;
        }
    }

    @Test
    public void testGetHttpHost() {
        String region = "south";
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig(appid, region);
        assertEquals(String.format(Locale.ENGLISH, "-%s.cos.%s.myqcloud.com", appid, region), cosXmlServiceConfig.getHttpHost());
    }

    @Test
    public void testGetHttpHostWithRegionStartCos() {
        String region = "cos.south";
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig(appid, region);
        assertEquals(String.format(Locale.ENGLISH, "-%s.%s.myqcloud.com", appid, region), cosXmlServiceConfig.getHttpHost());
    }

    @Test
    public void testGetHttpHostOldRegion() {
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig(appid, region);
        assertEquals(String.format(Locale.ENGLISH, "-%s.%s.myqcloud.com", appid, region), cosXmlServiceConfig.getHttpHost());
    }

    public boolean isSuccess(int httpCode){
        if(httpCode < 300 && httpCode >= 200){
            return true;
        }else {
            return false;
        }
    }

//    //service
//    @Test
//    public void testGetService() throws Exception {
//        init();
//        GetServiceRequest request = new GetServiceRequest();
//        request.setSign(600, null, null);
//        GetServiceResult result = cosXmlService.getService(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        final String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        Log.d(TAG, response);
//        assertEquals(true, isSuccess(result.getHttpCode()));
//    }
//
//    public volatile int hasCompleted = 0;
//    @Test
//    public void testGetService2() throws Exception {
//        init();
//        GetServiceRequest request = new GetServiceRequest();
//        request.setSign(600, null, null);
//       cosXmlService.getServiceAsync(request, new CosXmlResultListener() {
//           @Override
//           public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//               Log.d(TAG, result.printHeaders());
//               hasCompleted = 1;
//           }
//
//           @Override
//           public void onFail(CosXmlRequest request, CosXmlResult result) {
//               Log.d(TAG, result.printHeaders());
//               hasCompleted = 2;
//           }
//       });
//       while(hasCompleted == 0){
//           Thread.sleep(500);
//       }
//        assertEquals(1, hasCompleted);
//    }
//
//    //bucket
//    @Test
//    public void testGetBucketLocationRequest() throws Exception {
//        init();
//        GetBucketLocationRequest request = new GetBucketLocationRequest();
//        request.setBucket(bucket);
//        request.setSign(600, null, null);
//        GetBucketLocationResult result = cosXmlService.getBucketLocation(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(true, isSuccess(result.getHttpCode()));
//
//        GetBucketLocationRequest request2 = new GetBucketLocationRequest();
//        request2.setBucket(bucket);
//        request2.setSign(600, null, null);
//        cosXmlService.getBucketLocationAsync(request2, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(true,isSuccess(result.getHttpCode()));
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(false,isSuccess(result.getHttpCode()));
//            }
//        });
//    }
//
//    @Test
//    public void testListMultiUploadsRequest() throws Exception{
//        init();
//        ListMultiUploadsRequest request = new ListMultiUploadsRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        ListMultiUploadsResult result =  cosXmlService.listMultiUploads(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(true,isSuccess(result.getHttpCode()));
//
//        ListMultiUploadsRequest request2 = new ListMultiUploadsRequest();
//        request2.setBucket(bucket);
//        request2.setSign(600, null, null);
//        cosXmlService.listMultiUploadsAsync(request2, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(true,isSuccess(result.getHttpCode()));
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(false,isSuccess(result.getHttpCode()));
//            }
//        });
//    }
//
//    @Test
//    public void testDeleteBucketLifecycleRequest() throws Exception{
//        init();
//        DeleteBucketLifecycleRequest request = new DeleteBucketLifecycleRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        DeleteBucketLifecycleResult result =  cosXmlService.deleteBucketLifecycle(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(true,isSuccess(result.getHttpCode()));
//
//        DeleteBucketLifecycleRequest request2 = new DeleteBucketLifecycleRequest();
//        request2.setBucket(bucket);
//        request2.setSign(600, null, null);
//        cosXmlService.deleteBucketLifecycleAsync(request2, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(true,isSuccess(result.getHttpCode()));
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(false,isSuccess(result.getHttpCode()));
//            }
//        });
//    }
//
//    @Test
//    public void testGetBucketLifecycleRequest() throws Exception{
//        init();
//        GetBucketLifecycleRequest request = new GetBucketLifecycleRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        GetBucketLifecycleResult result =  cosXmlService.getBucketLifecycle(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(false,isSuccess(result.getHttpCode()));
//
//        GetBucketLifecycleRequest request2 = new GetBucketLifecycleRequest();
//        request2.setBucket(bucket);
//        request2.setSign(600,null,null);
//        cosXmlService.getBucketLifecycleAsync(request2, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlResult result) {
//
//            }
//        });
//
//    }
//
//    @Test
//    public void testPutBucketLifecycleRequest() throws Exception{
//        init();
//        PutBucketLifecycleRequest request = new PutBucketLifecycleRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        Rule rule = new Rule();
//        rule.id = "lifeID";
//        rule.status = "Enabled";
//        //配置未完成分块上传的定期删除规则
//        rule.abortIncompleteMultiUpload = new AbortIncompleteMultiUpload();
//        rule.abortIncompleteMultiUpload.daysAfterInitiation = "1";
//        request.setRuleList(rule);
//        PutBucketLifecycleResult result =  cosXmlService.putBucketLifecycle(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(false,isSuccess(result.getHttpCode()));
//    }
//
//    //acl
//    public boolean testPutBucketACLRequest() throws Exception{
//        init();
//        PutBucketACLRequest request = new PutBucketACLRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setXCOSACL("public-read");
//        List<String> readIdList = new ArrayList<String>();
//        readIdList.add("uin/1278687956:uin/2779643970");
//        request.setXCOSGrantReadWithUIN(readIdList);
//        List<String> writeIdList = new ArrayList<String>();
//        writeIdList.add("uin/1278687956:uin/2779643970");
//        request.setXCOSGrantWriteWithUIN(writeIdList);
//        Set<String> headerSet = new HashSet<String>();
//        headerSet.add("x-cos-acl");
//        headerSet.add("x-cos-grant-read");
//        headerSet.add("x-cos-grant-write");
//        PutBucketACLResult result =  cosXmlService.putBucketACL(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        Log.d("Unit Test", response);
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testGetBucketACLRequest() throws Exception{
//        init();
//        GetBucketACLRequest request = new GetBucketACLRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        GetBucketACLResult result =  cosXmlService.getBucketACL(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        // assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//    @Test
//    public void testACL() throws Exception{
//        Log.d("Unit Test", "test acl");
//        boolean result = testPutBucketACLRequest();
//        if(result){
//            result = testGetBucketACLRequest();
//            assertEquals(true, result);
//        }else{
//            assertEquals(true, result);
//        }
//    }
//
//    //tag
//    public boolean testPutBucketTaggingRequest() throws Exception{
//        init();
//        PutBucketTaggingRequest request = new PutBucketTaggingRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        Tag tag = new Tag();
//        tag.key = "1";
//        tag.value = "value_1";
//        request.setTagList(tag);
//        Tag tag2 = new Tag();
//        tag2.key = "2";
//        tag2.value = "value_2";
//        request.setTagList(tag2);
//        PutBucketTaggingResult result =  cosXmlService.putBucketTagging(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testGetBucketTaggingRequest() throws Exception{
//        init();
//        GetBucketTaggingRequest request = new GetBucketTaggingRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        GetBucketTaggingResult result =  cosXmlService.getBucketTagging(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        // assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testDeleteBucketTaggingRequest() throws Exception{
//        init();
//        DeleteBucketTaggingRequest request = new DeleteBucketTaggingRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        DeleteBucketTaggingResult result =  cosXmlService.deleteBucketTagging(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//    @Test
//    public void testTag() throws Exception{
//        boolean result = testPutBucketTaggingRequest();
//        if(result){
//            result = testGetBucketTaggingRequest();
//            if(result){
//                result = testDeleteBucketTaggingRequest();
//                assertEquals(true, result);
//            }else {
//                assertEquals(true, result);
//            }
//        }else{
//            assertEquals(true, result);
//        }
//    }
//
//
//
//    //bucket
//    public boolean testPutBucketRequest(String bucket) throws Exception{
//        init();
//        PutBucketRequest request = new PutBucketRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        PutBucketResult result =  cosXmlService.putBucket(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(false,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testHeadBucketRequest(String bucket) throws Exception{
//        init();
//        HeadBucketRequest request = new HeadBucketRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        HeadBucketResult result =  cosXmlService.headBucket(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testGetBucketRequest(String bucket) throws Exception{
//        init();
//        GetBucketRequest request = new GetBucketRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        GetBucketResult result =  cosXmlService.getBucket(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testDeleteBucketRequest(String bucket) throws Exception{
//        init();
//        DeleteBucketRequest request = new DeleteBucketRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        DeleteBucketResult result =  cosXmlService.deleteBucket(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(false,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    @Test
//    public void testBucket() throws Exception{
//        String bucket = String.valueOf(System.currentTimeMillis()/1000);
//        boolean result = testPutBucketRequest(bucket);
//        if(result){
//            result = testHeadBucketRequest(bucket);
//            if(result){
//                result = testGetBucketRequest(bucket);
//                if(result){
//                    result = testDeleteBucketRequest(bucket);
//                    assertEquals(true,result);
//                }else{
//                    assertEquals(true,result);
//                }
//            }else{
//                assertEquals(true,result);
//            }
//        }else{
//            assertEquals(true,result);
//        }
//    }
//
//    //cors
//    public boolean testPutBucketCORSRequest() throws Exception{
//        init();
//        PutBucketCORSRequest request = new PutBucketCORSRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        CORSRule corsRule = new CORSRule();
//        corsRule.id = "123";
//        corsRule.allowedOrigin = "http://123.61.25.13";
//        corsRule.maxAgeSeconds = "5000";
//        List<String> methods = new LinkedList<String>();
//        methods.add("put");
//        methods.add("post");
//        methods.add("get");
//        corsRule.allowedMethod = methods;
//
//        List<String> headers = new LinkedList<String>();
//        headers.add("host");
//        headers.add("content-type");
//        headers.add("authorizion");
//        corsRule.allowedHeader = headers;
//
//        List<String> exposeHeaders = new LinkedList<String>();
//        exposeHeaders.add("x-cos-metha-1");
//        exposeHeaders.add("x-cos-metha-2");
//        exposeHeaders.add("x-cos-metha-3");
//        corsRule.exposeHeader = exposeHeaders;
//        request.setCORSRuleList(corsRule);
//        PutBucketCORSResult result =  cosXmlService.putBucketCORS(request);
//        String headers_result = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers_result + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testGetBucketCORSRequest() throws Exception{
//        init();
//        GetBucketCORSRequest request = new GetBucketCORSRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        GetBucketCORSResult result =  cosXmlService.getBucketCORS(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testOptionObjectRequest(String cosPath) throws Exception{
//        init();
//        OptionObjectRequest request = new OptionObjectRequest();
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
//        request.setSign(600,null,null);
//        request.setOrigin("http://123.61.25.13");
//        request.setAccessControlMethod("get");
//        request.setAccessControlHeaders("host");
//        OptionObjectResult result =  cosXmlService.optionObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(false,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testDeleteBucketCORS() throws Exception{
//        init();
//        DeleteBucketCORSRequest request = new DeleteBucketCORSRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        DeleteBucketCORSResult result =  cosXmlService.deleteBucketCORS(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response", response);
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    @Test
//    public void testCORS() throws Exception{
//        boolean result = testPutBucketCORSRequest();
//        if(result){
//            result = testGetBucketCORSRequest();
//            if(result){
//                result = testOptionObjectRequest("/1503563341274.txt");
//                if(result){
//                    testDeleteBucketCORS();
//                    assertEquals(true,result);
//                }else {
//                    assertEquals(true,result);
//                }
//            }else{
//                assertEquals(true,result);
//            }
//        }else{
//            assertEquals(true,result);
//        }
//
//
//
//    }
//
//
//    //object
//    public boolean testPutObjectACLRequest(String cosPath) throws Exception{
//        init();
//        PutObjectACLRequest request = new PutObjectACLRequest();
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
//        request.setXCOSACL("public-read");
//        List<String> readIdList = new ArrayList<String>();
//        readIdList.add("uin/1278687956:uin/1278687956");
//        request.setXCOSGrantReadWithUIN(readIdList);
//        List<String> writeIdList = new ArrayList<String>();
//        writeIdList.add("uin/1278687956:uin/1278687956");
//        request.setXCOSGrantWriteWithUIN(writeIdList);
//        Set<String> header = new HashSet<String>();
//        header.add("content-length");
//        header.add("content-type");
//        header.add("date");
//        request.setSign(600,null,null);
//        PutObjectACLResult result =  cosXmlService.putObjectACL(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    public boolean testGetObjectACLRequest(String cosPath) throws Exception{
//        init();
//        GetObjectACLRequest request = new GetObjectACLRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setCosPath(cosPath);
//        request.getCosPath();
//        GetObjectACLResult result =  cosXmlService.getObjectACL(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    @Test
//    public void testObjectACL() throws Exception{
//        String cosPath = "/1503563341274.txt";
//        boolean result = testPutObjectACLRequest(cosPath);
//        if(result){
//            result = testGetObjectACLRequest(cosPath);
//            assertEquals(true, result);
//        }else{
//            assertEquals(true, result);
//        }
//
//    }
//
//    @Test
//    public void testHeadObjectRequest() throws Exception{
//        init();
//        HeadObjectRequest request = new HeadObjectRequest();
//        request.setBucket(bucket);
//        request.setCosPath("/1503563341274.txt");
//        request.setSign(600,null,null);
//        HeadObjectResult result =  cosXmlService.headObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response",response);
//        assertEquals(true,isSuccess(result.getHttpCode()));
//    }
//
//    @Test
//    public void testDeleteMultiObjectRequest() throws Exception{
//        init();
//        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setQuiet(false);
//        request.setObjectList("/2/1491967729774.jpg");
//        request.setObjectList("2/1491967729775.jpg");
//        List<String> listObject = new ArrayList<String>();
//        listObject.add("/2/1491967730522.jpg");
//        listObject.add("2/1491968133919.jpg");
//        request.setObjectList(listObject);
//        request.getDelete();
//        DeleteMultiObjectResult result =  cosXmlService.deleteMultiObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(true,isSuccess(result.getHttpCode()));
//    }
//
//    @Test
//    public void testDeleteObjectRequest() throws Exception{
//        init();
//        DeleteObjectRequest request = new DeleteObjectRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setCosPath("/appentTest.txt");
//        DeleteObjectResult result =  cosXmlService.deleteObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(true,isSuccess(result.getHttpCode()));
//    }
//
//
//    public String crateFile(long length) throws Exception{
//        String srcPath = Environment.getExternalStorageDirectory().getPath() + "/"
//                + System.currentTimeMillis() + ".txt";
//        File file = new File(srcPath);
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//                RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
//                randomAccessFile.setLength(length);
//                randomAccessFile.close();
//            } catch (IOException e) {
//                throw e;
//            }
//        }
//        return srcPath;
//    }
//
//    @Test
//    public void testAppendObjectRequest() throws Exception{
//        init();
//        AppendObjectRequest request = new AppendObjectRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setCosPath("/" + System.currentTimeMillis() + ".txt");
//        byte[] data = new byte[1024 * 1024 * 2];
//        Arrays.fill(data, (byte)0);
//        request.setData(data);
//        request.setSrcPath(crateFile( 1024 * 1024 * 3));
//        //request.setSrcPath(Environment.getExternalStorageDirectory().getPath() + "/test.jpg");
//        request.setPosition(0);
//        request.setSign(600,null,null);
//        AppendObjectResult result =  cosXmlService.appendObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response", response);
//        assertEquals(true,isSuccess(result.getHttpCode()));
//    }
//
//    public boolean testPutObjectRequest(String cosPath) throws Exception{
//        init();
//        PutObjectRequest request = new PutObjectRequest();
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
////        byte[] data = new byte[1024 * 1024 * 2];
////        Arrays.fill(data, (byte)0);
////        request.setData(data);
//        request.setSrcPath(crateFile(1024 * 1024));
//        request.setSign(600,null,null);
//        PutObjectResult result =  cosXmlService.putObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response", response);
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else{
//            return false;
//        }
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//    }
//
//    public boolean testGetObjectRequest(String cosPath) throws Exception{
//        init();
//        GetObjectRequest request = new GetObjectRequest(Environment.getExternalStorageDirectory().getPath() + "/test");
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
//        request.setSign(600,null,null);
//        request.setRange(1);
//        GetObjectResult result = cosXmlService.getObject(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response", response);
//        assertEquals(true,isSuccess(result.getHttpCode()));
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//    @Test
//    public void testUploadAndGet() throws Exception{
//        String cosPath = "/putobject_" + System.currentTimeMillis() + ".txt";
//        testPutObjectRequest(cosPath);
//        boolean result = testGetObjectRequest(cosPath);
//        assertEquals(true,result);
//    }
//
//
//
//    public String testInitMultipartUploadRequest(String cosPath) throws Exception{
//        init();
//        InitMultipartUploadRequest request = new InitMultipartUploadRequest();
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
//        request.setSign(600,null,null);
//        InitMultipartUploadResult result =  cosXmlService.initMultipartUpload(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        System.out.println(response);
//        // assertEquals("response",response);
//        if(isSuccess(result.getHttpCode())){
//            return result.initMultipartUpload.uploadId;
//        }else {
//            return null;
//        }
//    }
//
//    public void testListPartsRequest(String cosPath, String uploadId) throws Exception{
//        init();
//        ListPartsRequest request = new ListPartsRequest();
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
//        request.setUploadId(uploadId);
//        Set<String> checkHeader = new HashSet<String>();
//        checkHeader.add("host");
//        Set<String> checkParams = new HashSet<String>();
//        checkParams.add("uploadId");
//        request.setSign(600,checkHeader,checkParams);
//        ListPartsResult result =  cosXmlService.listParts(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //Log.i("UnitTest", response);
//        //assertEquals("response", response);
//        //assertEquals(true,isSuccess(result.getHttpCode()));
//
//        cosXmlService.listPartsAsync(request, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(true, isSuccess(result.getHttpCode()));
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlResult result) {
//                assertEquals(false, isSuccess(result.getHttpCode()));
//            }
//        });
//
//    }
//
//
//
//    public String testUploadPartRequest(String cosPath, String uploadId) throws Exception{
//        init();
//        UploadPartRequest request = new UploadPartRequest();
//        request.setBucket(bucket);
//        request.setCosPath(cosPath);
//        request.setUploadId(uploadId);
//        request.setPartNumber(1);
//        byte[] data = new byte[1024 * 1024 * 2];
//        Arrays.fill(data, (byte)0);
//        request.setData(data);
//        request.setSign(600,null,null);
//        UploadPartResult result =  cosXmlService.uploadPart(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response", response);
//        if(isSuccess(result.getHttpCode())){
//            return result.getETag();
//        }else {
//            return null;
//        }
//    }
//
//    public boolean testCompleteMultiUploadRequest(String cosPath, String uploadId, String etag) throws Exception{
//        init();
//        CompleteMultiUploadRequest request = new CompleteMultiUploadRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setCosPath(cosPath);
//        request.setUploadId(uploadId);
//        request.setPartNumberAndETag(1,etag);
//        CompleteMultiUploadResult result =  cosXmlService.completeMultiUpload(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        if(isSuccess(result.getHttpCode())){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    @Test
//    public void testSliceUpload() throws Exception{
//        String cosPath = "/slice_upload.txt";
//        String uploadId  = testInitMultipartUploadRequest(cosPath);
//        testListPartsRequest(cosPath,uploadId);
//        String etag = testUploadPartRequest(cosPath,uploadId);
//        boolean result = testCompleteMultiUploadRequest(cosPath, uploadId, etag);
//        assertEquals(true, result);
//    }
//
//    String cosPath = "/slice_upload.txt";
//    String uploadId = "xxxxxxxxxxxxxxxxxxxxxxxx";
//
//    @Test
//    public void testAbortMultiUploadRequest() throws Exception{
//        init();
//        AbortMultiUploadRequest request = new AbortMultiUploadRequest();
//        request.setBucket(bucket);
//        request.setSign(600,null,null);
//        request.setCosPath(cosPath);
//        request.setUploadId(uploadId);
//        request.getCosPath();
//        request.getUploadId();
//        AbortMultiUploadResult result =  cosXmlService.abortMultiUpload(request);
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        assertEquals(false,isSuccess(result.getHttpCode()));
//    }
//
//    @Test
//    public void testMultiHelper() throws Exception{
//        init();
//        MultipartUploadHelper request = new MultipartUploadHelper(cosXmlService);
//        request.setBucket(bucket);
//        request.setCosPath("/" + System.currentTimeMillis() + ".txt");
//        request.setSrcPath(crateFile(1024 * 1024 * 5));
//        request.setSliceSize(1024 * 1024);
//        request.setProgressListener(new QCloudProgressListener() {
//            @Override
//            public void onProgress(long l, long l1) {
//
//            }
//        });
//        request.setProgressListener(new QCloudProgressListener() {
//            @Override
//            public void onProgress(long l, long l1) {
//
//            }
//        });
//        CosXmlResult result = request.upload();
//        String headers = result.printHeaders();
//        String body = result.printBody();
//        String error = result.printError();
//        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
//        //assertEquals("response",response);
//        assertEquals(true, isSuccess(result.getHttpCode()));
//    }
//
//    @Test
//    public void testUtils() throws Exception{
//        //FileUtils
//        byte[] data = FileUtils.getFileContent(crateFile(1024 * 1024), 0, 1024 );
//        assertEquals(true, data != null);
//        try {
//            FileUtils.getFileContent(null, 0, 1024 );
//        }catch (Exception e){
//            assertEquals(true,true);
//        }
//
//        assertEquals(true, data != null);
//        String result = MD5Utils.getMD5FromBytes(data, 0, data.length);
//        assertEquals(true, result != null);
//        result = MD5Utils.getMD5FromPath(crateFile(1024 * 1024));
//        assertEquals(true, result != null);
//        result = MD5Utils.getMD5FromString("md5utils unit test");
//        assertEquals(true, result != null);
//        result = SHA1Utils.getSHA1FromBytes(data, 0, data.length);
//        assertEquals(true, result != null);
//        result = SHA1Utils.getSHA1FromPath(crateFile(1024 * 1024));
//        assertEquals(true, result != null);
//        result = SHA1Utils.getSHA1FromString("sha1utils unit test");
//        assertEquals(true, result != null);
//        String web = "http://www.qcloud.com:8080/index?sign = a#123";
//        result = StringUtils.getFragment(web);
//        assertEquals("123", result);
//        result = StringUtils.getHost(web);
//        assertEquals("www.qcloud.com", result);
//        result = StringUtils.getPath(web);
//        assertEquals("/index", result);
//        result = StringUtils.getQuery(web);
//        assertEquals("sign = a", result);
//        result = StringUtils.getScheme(web);
//        assertEquals("http", result);
//        long port = StringUtils.getPort(web);
//        assertEquals(8080, port);
//        String web2 = "http://www.qcloud.com/space space";
//        result = StringUtils.encodedUrl(web2);
//        assertEquals("http%3A/www.qcloud.com/space%20space",result);
//    }
//
//    @Test
//    public void testEnum() throws Exception{
//        String result = COSACL.PRIVATE.getACL();
//        assertEquals("private", result);
//        result = COSACL.PUBLIC_READ.getACL();
//        assertEquals("public-read", result);
//        result = COSACL.PUBLIC_READ_WRITE.getACL();
//        assertEquals("public-read-write", result);
//
//        result = COSStorageClass.NEARLINE.getStorageClass();
//        assertEquals("Nearline", result);
//        result = COSStorageClass.STANDARD.getStorageClass();
//        assertEquals("Standard", result);
//        result = COSStorageClass.STANDARD_IA.getStorageClass();
//        assertEquals("Standard_IA", result);
//
//        result = Region.AP_Beijing.getRegion();
//        assertEquals("ap-beijing", result);
//        result = Region.AP_Beijing_1.getRegion();
//        assertEquals("ap-beijing-1", result);
//        result = Region.AP_Shanghai.getRegion();
//        assertEquals("ap-shanghai", result);
//        result = Region.AP_Guangzhou.getRegion();
//        assertEquals("ap-guangzhou", result);
//        result = Region.AP_Guangzhou_2.getRegion();
//        assertEquals("ap-guangzhou-2", result);
//        result = Region.AP_Chengdu.getRegion();
//        assertEquals("ap-chengdu", result);
//        result = Region.AP_Singapore.getRegion();
//        assertEquals("ap-singapore", result);
//        result = Region.AP_Hongkong.getRegion();
//        assertEquals("ap-hongkong", result);
//        result = Region.NA_Toronto.getRegion();
//        assertEquals("na-toronto", result);
//        result = Region.CN_EAST.getRegion();
//        assertEquals("cn-east", result);
//        result = Region.CN_NORTH.getRegion();
//        assertEquals("cn-north", result);
//        result = Region.CN_SOUTH.getRegion();
//        assertEquals("cn-south", result);
//        result = Region.CN_SOUTHWEST.getRegion();
//        assertEquals("cn-southwest", result);
//        result = Region.SG.getRegion();
//        assertEquals("sg", result);
//
//        result = Permission.FULL_CONTROL.getPermission();
//        assertEquals("FULL_CONTROL", result);
//        result = Permission.READ.getPermission();
//        assertEquals("READ", result);
//        result = Permission.WRTIE.getPermission();
//        assertEquals("WRITE", result);
//
//    }

}
