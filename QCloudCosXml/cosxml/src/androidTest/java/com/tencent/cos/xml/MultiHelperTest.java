package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.MultipartUploadService;
import com.tencent.qcloud.core.network.QCloudProgressListener;


import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/28.
 * author bradyxiao
 */
public class MultiHelperTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public MultiHelperTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }


    @Test
    public void testMultiHelper() throws Exception{
        init();
        MultipartUploadService request = new MultipartUploadService(qBaseServe.cosXmlService);
        request.setBucket(qBaseServe.bucket);
        //request.setCosPath("/" + System.currentTimeMillis() + ".txt");
        request.setCosPath("/mulituploadHelper.txt");
        request.setSrcPath(qBaseServe.crateFile(1024 * 1024 * 5));
        request.setSliceSize(1024 * 1024);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {

            }
        });
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {

            }
        });
        CosXmlResult result = request.upload();
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d("response",response);
        assertEquals(true, qBaseServe.isSuccess(result.getHttpCode()));
    }

}
