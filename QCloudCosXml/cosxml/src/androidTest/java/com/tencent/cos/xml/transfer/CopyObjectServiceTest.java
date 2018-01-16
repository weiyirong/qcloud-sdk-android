package com.tencent.cos.xml.transfer;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2017/12/13.
 */
public class CopyObjectServiceTest extends ApplicationTestCase {

    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;

    public CopyObjectServiceTest() {
        super(Application.class);
    }

    @Test
    public void copyObject() throws Exception {
    }

    @Test
    public void copyObject1() throws Exception {
    }

    @Test
    public void test() throws IOException, CosXmlServiceException, CosXmlClientException {
       String sourceBucket = "androidtest";
       String sourceAppid = "1253960454";
       String sourceOrigin = Region.AP_Guangzhou.getRegion();
       String sourceCosPath = "upload_service3.txt";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                sourceAppid, sourceBucket, sourceOrigin, sourceCosPath);
        String bucket = "androidtest";
        String cosPath = "copy_test.txt";
        CopyObjectService copyObjectService = new CopyObjectService(QService.getCosXmlClient(getContext()));
        CosXmlResult cosXmlResult = copyObjectService.copyObject(bucket, cosPath, copySourceStruct);
        Log.d(TAG, cosXmlResult.printResult());
        QService.delete(QService.getCosXmlClient(getContext()),bucket, cosPath);
    }

}