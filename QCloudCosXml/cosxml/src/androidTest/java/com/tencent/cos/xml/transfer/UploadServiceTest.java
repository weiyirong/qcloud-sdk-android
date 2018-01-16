package com.tencent.cos.xml.transfer;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.CosXmlResult;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/7.
 */
public class UploadServiceTest extends ApplicationTestCase {
    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public UploadServiceTest() {
        super(Application.class);
    }

    @Test
    public void upload() throws Exception {

    }

    @Test
    public void cancel() throws Exception {
    }

    @Test
    public void test() throws IOException, CosXmlServiceException, CosXmlClientException {
        UploadService.ResumeData resumeData = new UploadService.ResumeData();
        resumeData.bucket = "androidtest";
        resumeData.cosPath = "upload_service5.txt";
        resumeData.srcPath = QService.createFile( 1 * 1024 * 1024);
        resumeData.sliceSize = 1024 * 1024;
        resumeData.uploadId = null;
        UploadService uploadService = new UploadService(QService.getCosXmlClient(getContext()),
                resumeData);
        uploadService.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "completed: " + complete + "| target: " + target + "| progress =" + (int)((complete * 1.00)/target * 100));
            }
        });

        CosXmlResult cosXmlResult = uploadService.upload();
        Log.d(TAG, cosXmlResult.printResult());
        //QService.delete(QService.getCosXmlClient(getContext()),resumeData.bucket, resumeData.cosPath);
        QService.delete(resumeData.srcPath);
    }

}