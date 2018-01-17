package com.tencent.cos.xml.model.bucket;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.tag.ACLAccount;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by bradyxiao on 2017/12/12.
 */

public class BucketTest extends ApplicationTestCase{

    static final String TAG = "Unit_Test";

    String bucket;


    public BucketTest() {
        super(Application.class);
    }


    //put bucket
    public void putBucketTest() throws CosXmlServiceException, CosXmlClientException {
        PutBucketRequest request = new PutBucketRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        PutBucketResult result =  QService.getCosXmlClient(getContext()).putBucket(request);
        Log.d(TAG, result.printResult());
    }

    public void headBucketTest() throws CosXmlServiceException, CosXmlClientException {
        HeadBucketRequest request = new HeadBucketRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        HeadBucketResult result = QService.getCosXmlClient(getContext()).headBucket(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketLocationTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketLocationRequest request = new GetBucketLocationRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketLocationResult result = QService.getCosXmlClient(getContext()).getBucketLocation(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketRequest request = new GetBucketRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketResult result = QService.getCosXmlClient(getContext()).getBucket(request);
        Log.d(TAG, result.printResult());
    }

    public void listMultiUploadsTest() throws CosXmlServiceException, CosXmlClientException {
        ListMultiUploadsRequest request = new ListMultiUploadsRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        ListMultiUploadsResult result = QService.getCosXmlClient(getContext()).listMultiUploads(request);
        Log.d(TAG, result.printResult());
    }

    public void putBucketCORSTest() throws CosXmlServiceException, CosXmlClientException {
        PutBucketCORSRequest request = new PutBucketCORSRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        CORSConfiguration.CORSRule corsRule = new CORSConfiguration.CORSRule();
        corsRule.allowedOrigin = "http://cloud.tencent.com";
        corsRule.allowedHeader = new ArrayList<>();
        corsRule.allowedHeader.add("Host");
        corsRule.allowedHeader.add("Authorization");
        corsRule.allowedMethod = new ArrayList<>();
        corsRule.allowedMethod.add("PUT");
        corsRule.allowedMethod.add("GET");
        corsRule.exposeHeader = new ArrayList<>();
        corsRule.exposeHeader.add("x-cos-meta");
        corsRule.exposeHeader.add("x-cos-meta-2");
        corsRule.id = "CORSID";
        corsRule.maxAgeSeconds = 5000;
        request.addCORSRule(corsRule);
        PutBucketCORSResult result = QService.getCosXmlClient(getContext()).putBucketCORS(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketCORSTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketCORSRequest request = new GetBucketCORSRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketCORSResult result = QService.getCosXmlClient(getContext()).getBucketCORS(request);
        Log.d(TAG, result.printResult());
    }

    public void deleteBucketCORSTest() throws CosXmlServiceException, CosXmlClientException {
        DeleteBucketCORSRequest request = new DeleteBucketCORSRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        DeleteBucketCORSResult getServiceResult = QService.getCosXmlClient(getContext()).deleteBucketCORS(request);
        Log.d(TAG, getServiceResult.printResult());
    }

    public void putBucketLifecycleTest() throws CosXmlServiceException, CosXmlClientException {
        PutBucketLifecycleRequest request = new PutBucketLifecycleRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        LifecycleConfiguration.Rule rule = new LifecycleConfiguration.Rule();
        rule.id = "LifeID";
        rule.status = "Enabled";
        rule.filter = new LifecycleConfiguration.Filter();
        rule.filter.prefix = "aws";
        rule.expiration = new LifecycleConfiguration.Expiration();
        rule.expiration.days = 1;
        // rule.expiration.date = "Mon, 11 Dec 2017 15:43:39 GMT";
        request.setRuleList(rule);
        PutBucketLifecycleResult result = QService.getCosXmlClient(getContext()).putBucketLifecycle(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketLifecycleTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketLifecycleRequest request = new GetBucketLifecycleRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketLifecycleResult result = QService.getCosXmlClient(getContext()).getBucketLifecycle(request);
        Log.d(TAG, result.printResult());
    }

    public void deleteBucketLifecycleTest() throws CosXmlServiceException, CosXmlClientException {
        DeleteBucketLifecycleRequest request = new DeleteBucketLifecycleRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        DeleteBucketLifecycleResult result = QService.getCosXmlClient(getContext()).deleteBucketLifecycle(request);
        Log.d(TAG, result.printResult());
    }

    public void putBucketVersioningTest() throws CosXmlServiceException, CosXmlClientException {
        PutBucketVersioningRequest request = new PutBucketVersioningRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        request.setEnableVersion(true);
        PutBucketVersioningResult result = QService.getCosXmlClient(getContext()).putBucketVersioning(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketVersioningTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketVersioningRequest request = new GetBucketVersioningRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketVersioningResult result = QService.getCosXmlClient(getContext()).getBucketVersioning(request);
        Log.d(TAG, result.printResult());
    }

    public void putBucketReplicationTest() throws CosXmlServiceException, CosXmlClientException {

        PutBucketReplicationRequest request = new PutBucketReplicationRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        request.setReplicationConfigurationWithRole("2832742109", "2832742109");
        PutBucketReplicationRequest.RuleStruct ruleStruct = new PutBucketReplicationRequest.RuleStruct();
        ruleStruct.id = "replication_id";
        ruleStruct.isEnable = true;
        ruleStruct.appid = "1253960454";
        ruleStruct.bucket = "replicationtest";
        ruleStruct.region = "ap-beijing";
        request.setReplicationConfigurationWithRule(ruleStruct);
        PutBucketReplicationResult result = QService.getCosXmlClient(getContext()).putBucketReplication(request);
        Log.d(TAG, result.printResult());

    }

    public void getBucketReplicationTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketReplicationRequest request = new GetBucketReplicationRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketReplicationResult result = QService.getCosXmlClient(getContext()).getBucketReplication(request);
        Log.d(TAG, result.printResult());
    }

    public void deleteBucketReplicationTest() throws CosXmlServiceException, CosXmlClientException {
        DeleteBucketReplicationRequest request = new DeleteBucketReplicationRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        DeleteBucketReplicationResult result = QService.getCosXmlClient(getContext()).deleteBucketReplication(request);
        Log.d(TAG, result.printResult());
    }

    public void putBucketACLTest() throws CosXmlServiceException, CosXmlClientException {
        PutBucketACLRequest request = new PutBucketACLRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        request.setXCOSACL(COSACL.PRIVATE);
        ACLAccount aclAccount = new ACLAccount();
        aclAccount.addAccount("1131975903", "1131975903");
        request.setXCOSGrantRead(aclAccount);
        request.setXCOSGrantWrite(aclAccount);

        PutBucketACLResult result = QService.getCosXmlClient(getContext()).putBucketACL(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketACLTest() throws CosXmlServiceException, CosXmlClientException {
        GetBucketACLRequest request = new GetBucketACLRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        GetBucketACLResult result = QService.getCosXmlClient(getContext()).getBucketACL(request);
        Log.d(TAG, result.printResult());
    }

    public void deleteBucketTest() throws CosXmlServiceException, CosXmlClientException {
        DeleteBucketRequest request = new DeleteBucketRequest(bucket);
        request.setRequestHeaders("Content-Type", "application/x-shockwave-flash");
        DeleteBucketResult result = QService.getCosXmlClient(getContext()).deleteBucket(request);
        Log.d(TAG, result.printResult());
    }


    @Test
    public void test() throws CosXmlClientException, CosXmlServiceException {
        bucket = "androidtest1";
        putBucketTest();
        headBucketTest();
        getBucketLocationTest();
        getBucketTest();
        listMultiUploadsTest();
        putBucketCORSTest();
        getBucketCORSTest();
        deleteBucketCORSTest();
        putBucketLifecycleTest();
        getBucketLifecycleTest();
        deleteBucketLifecycleTest();
        putBucketVersioningTest();
        getBucketVersioningTest();
        putBucketReplicationTest();
        getBucketReplicationTest();
        deleteBucketReplicationTest();
        putBucketACLTest();
        getBucketACLTest();
        deleteBucketTest();
    }

}
