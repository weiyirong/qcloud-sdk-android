package com.tencent.cos.xml;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.utils.GenerateGetObjectURLUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradyxiao on 2018/4/8.
 */

@RunWith(AndroidJUnit4.class)
public class WithUrlOperatorTest {

    @Test
    public void test() throws Exception{
//        String appid = "1251668577";
//        String bucket = "zuhaotestnorth";
//        String region =  Region.AP_Beijing_1.getRegion();;
//        String cosPath = "test.txt";
//        String url = GenerateGetObjectURLUtils.getRequestUrlWithSign(false,  "put", null, null, appid,
//                bucket, region, cosPath, 600, new GenerateGetObjectURLUtils.QCloudAPI() {
//                    @Override
//                    public String getSecretKey() {
//                        return "XX";
//                    }
//
//                    @Override
//                    public String getSecretId() {
//                        return "XX";
//                    }
//
//                    @Override
//                    public long getKeyDuration() {
//                        return 600;
//                    }
//
//                    @Override
//                    public String getSessionToken() {
//                        return null;
//                    }
//                });
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .followRedirects(true)
//                .followSslRedirects(true)
//                .retryOnConnectionFailure(true)
//                .connectTimeout(15000, TimeUnit.MILLISECONDS)
//                .writeTimeout(30000, TimeUnit.MILLISECONDS)
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })
//                .build();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .put(RequestBody.create(null, "this is  a test"))
//                .build();
//        Call call = okHttpClient.newCall(request);
//        Response response = call.execute();
//        Log.d("XIAO", "Http code =" + response.code());
    }
}
