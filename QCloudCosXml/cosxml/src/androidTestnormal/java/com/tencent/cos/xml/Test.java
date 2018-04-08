package com.tencent.cos.xml;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteMultiObjectResult;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradyxiao on 2018/3/6.
 */
//@RunWith(AndroidJUnit4.class)
//public class Test {
//
//    /** 腾讯云 cos 服务的 appid */
//    private final String appid = "1253960454";
//
//    /** appid 对应的 秘钥 */
//    private final String secretId = "XXX";
//
//    /** appid 对应的 秘钥 */
//    private final String secretKey = "XXX";
//
//    /** bucketForObjectAPITest 所处在的地域 */
//    private String region = Region.AP_Guangzhou.getRegion();
//
//    private Context context;
//
//    CosXml cosXml;
//
//    @org.junit.Test
//    public void testSDK() throws Exception{
//        context = InstrumentationRegistry.getContext();
//        if(cosXml == null){
//            CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
//                    .isHttps(false)
//                    .setAppidAndRegion(appid, region)
//                    .setDebuggable(true)
//                    .builder();
//            cosXml = new CosXmlService(context, cosXmlServiceConfig,
//                    new ShortTimeCredentialProvider(secretId,secretKey,600) );
//        }
//        String bucket = "androidtest-1253960454";
//        GetBucketRequest getBucketRequest = new GetBucketRequest(bucket);
//        GetBucketResult getBucketResult = cosXml.getBucket(getBucketRequest);
//        List<ListBucket.Contents> objects = getBucketResult.listBucket.contentsList;
//        DeleteMultiObjectRequest deleteMultiObjectRequest = new DeleteMultiObjectRequest(bucket, null);
//        deleteMultiObjectRequest.setQuiet(true);
//        for(ListBucket.Contents object : objects){
//            deleteMultiObjectRequest.setObjectList(object.key);
//        }
//        DeleteMultiObjectResult deleteMultiObjectResult = cosXml.deleteMultiObject(deleteMultiObjectRequest);
//        Log.d("UnitTest", deleteMultiObjectResult.printResult());
//
//    }
//
//}
