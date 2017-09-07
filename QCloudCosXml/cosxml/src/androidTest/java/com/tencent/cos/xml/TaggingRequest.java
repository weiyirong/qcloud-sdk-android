package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketTaggingResult;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingResult;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingResult;
import com.tencent.cos.xml.model.tag.Tag;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class TaggingRequest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public TaggingRequest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    public boolean testPutBucketTaggingRequest() throws Exception{
        PutBucketTaggingRequest request = new PutBucketTaggingRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        Tag tag = new Tag();
        tag.key = "1";
        tag.value = "value_1";
        request.setTagList(tag);
        List<Tag> list = new ArrayList<Tag>();
        Tag tag2 = new Tag();
        tag2.key = "2";
        tag2.value = "value_2";
        list.add(tag2);
        request.setTagList(list);
        PutBucketTaggingResult result =  qBaseServe.cosXmlService.putBucketTagging(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testPutBucketTaggingRequest2() throws Exception{
        hasCompleted = 0;
        PutBucketTaggingRequest request = new PutBucketTaggingRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        Tag tag = new Tag();
        tag.key = "1";
        tag.value = "value_1";
        request.setTagList(tag);
        List<Tag> list = new ArrayList<Tag>();
        Tag tag2 = new Tag();
        tag2.key = "2";
        tag2.value = "value_2";
        list.add(tag2);
        request.setTagList(list);
        qBaseServe.cosXmlService.putBucketTaggingAsync(request, new CosXmlResultListener() {
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

    public boolean testGetBucketTaggingRequest() throws Exception{
        GetBucketTaggingRequest request = new GetBucketTaggingRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        GetBucketTaggingResult result =  qBaseServe.cosXmlService.getBucketTagging(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testGetBucketTaggingRequest2() throws Exception{
        hasCompleted = 0;
        GetBucketTaggingRequest request = new GetBucketTaggingRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.getBucketTaggingAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
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

    public boolean testDeleteBucketTaggingRequest() throws Exception{
        DeleteBucketTaggingRequest request = new DeleteBucketTaggingRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        DeleteBucketTaggingResult result =  qBaseServe.cosXmlService.deleteBucketTagging(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testDeleteBucketTaggingRequest2() throws Exception{
        hasCompleted = 0;
        DeleteBucketTaggingRequest request = new DeleteBucketTaggingRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.deleteBucketTaggingAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
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
        assertEquals(true,testPutBucketTaggingRequest());
        assertEquals(true, testGetBucketTaggingRequest());
        assertEquals(true, testDeleteBucketTaggingRequest());
    }

    @Test
    public void test2() throws Exception{
        init();
        assertEquals(true,testPutBucketTaggingRequest2());
        assertEquals(true, testGetBucketTaggingRequest2());
        assertEquals(true, testDeleteBucketTaggingRequest2());
    }
}
