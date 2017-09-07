package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.AppendObjectResult;
import com.tencent.qcloud.network.QCloudProgressListener;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class AppendObjectTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public AppendObjectTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }

    @Test
    public void test1() throws Exception{
        init();
        String cosPath = "/" + System.currentTimeMillis() + ".txt";
        String srcPath = qBaseServe.crateFile( 1024 * 1024 * 3);
        AppendObjectRequest request = new AppendObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath(cosPath);
        assertEquals(cosPath, request.getCosPath());
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        request.setData(data);
        assertEquals(true, request.getData() != null);
        request.setSrcPath(srcPath);
        assertEquals(srcPath, request.getSrcPath());
        request.setPosition(0);
        assertEquals(0, request.getPosition());
        request.setSign(600,null,null);
        request.setXCOSACL("public-read");
        List<String> readIdList = new ArrayList<String>();
        readIdList.add("uin/1278687956:uin/1278687956");
        request.setXCOSGrantReadWithUIN(readIdList);
        List<String> writeIdList = new ArrayList<String>();
        writeIdList.add("uin/1278687956:uin/1278687956");
        request.setXCOSGrantWriteWithUIN(writeIdList);
        request.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long l, long l1) {
                Log.d(TAG, String.valueOf(l) +"/"+ String.valueOf(l1));
            }
        });
//        assertEquals(true, request.getProgressListener() != null);
//        request.setCacheControl(null);
//        request.setCacheControl("no-cache");
//        request.setContentDisposition(null);
//        request.setContentDisposition("attach");
//        request.setContentEncodeing(null);
//        request.setContentEncodeing("utf-8");
//        request.setExpires(null);
//        request.setExpires("");
//        request.setXCOSMeta("meta", "meta");
//        request.setXCOSACL(COSACL.PRIVATE);
//        List<String> uinList = new LinkedList<>();
//        uinList.add("105");
//        request.setXCOSGrantRead(uinList);
//        request.setXCOSGrantWrite(uinList);
//        request.setXCOSReadWriteWithUIN(uinList);
//        request.setXCOSReadWriteWithUIN(uinList);

        AppendObjectResult result =  qBaseServe.cosXmlService.appendObject(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG,response);
        assertEquals(false,qBaseServe.isSuccess(result.getHttpCode()));
    }

    @Test
    public void test2() throws Exception{
        init();
        AppendObjectRequest request = new AppendObjectRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        request.setCosPath("/" + System.currentTimeMillis() + ".txt");
        byte[] data = new byte[1024 * 1024 * 2];
        Arrays.fill(data, (byte)0);
        request.setData(data);
        request.setSrcPath(qBaseServe.crateFile( 1024 * 1024 * 3));
        request.setPosition(0);
        request.setSign(600,null,null);
        qBaseServe.cosXmlService.appendObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders() + "|" + result.printError());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        assertEquals(1, hasCompleted);
    }
}
