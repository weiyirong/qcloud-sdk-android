package com.tencent.cos.xml.model.bucket;

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

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/1/11.
 */
public class ListBucketVersionsRequestTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public ListBucketVersionsRequestTest() {
        super(Application.class);
    }

    @Test
    public void getMethod() throws Exception {
    }

    @Test
    public void getRequestBody() throws Exception {
    }

    @Test
    public void setPrefix() throws Exception {
    }

    @Test
    public void setKeyMarker() throws Exception {
    }

    @Test
    public void setVersionIdMarker() throws Exception {
    }

    @Test
    public void getQueryString() throws Exception {
    }

    @Test
    public void test1() throws CosXmlServiceException, CosXmlClientException {
        String bucket = "androidtest";
        ListBucketVersionsRequest request = new ListBucketVersionsRequest(bucket);
        ListBucketVersionsResult result = QService.getCosXmlClient(getContext()).listBucketVersions(request);
        Log.d(TAG, result.printResult());
    }

    @Test
    public void test2(){
        String bucket = "androidtest";
        ListBucketVersionsRequest request = new ListBucketVersionsRequest(bucket);
        QService.getCosXmlClient(getContext()).listBucketVersionsAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}