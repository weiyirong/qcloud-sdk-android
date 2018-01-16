package com.tencent.cos.xml.utils;

import android.test.AndroidTestCase;
import android.util.Log;

import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.auth.BasicQCloudCredentials;

import com.tencent.qcloud.core.common.QCloudClientException;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/1/2.
 */
public class GenerateGetObjectURLUtilsTest extends AndroidTestCase {

    static final String TAG = "Unit_Test";

    @Test
    public void testGetObjectUrl() throws Exception {
        String url = GenerateGetObjectURLUtils.getObjectUrl(true, "1253960454",
                "androidtest",Region.AP_Guangzhou.getRegion(), "append4.txt");
        Log.d(TAG, url);
        assertEquals("https://xy2-1253960454.cos.ap-guangzhou.myqcloud.com/append4.txt", url);
    }

    @Test
    public void testGetObjectUrlWithSign() throws CosXmlClientException {

        GenerateGetObjectURLUtils.QCloudAPI qCloudAPI = new GenerateGetObjectURLUtils.QCloudAPI() {
            @Override
            public String getSecretKey() {
                String secretKey = "rWyGVcXHpCjDOSMaheQSNGyMfstiOAqu";
                return secretKey;
            }

            @Override
            public String getSecretId() {
                String secretId = "AKIDtgHguxSsaEykZHoIfqtlT1NY0MWTn4B5";
                return secretId;
            }

            @Override
            public long getKeyDuration() {
                return 0;
            }

            @Override
            public String getSessionToken() {
                return null;
            }
        };
        String url = GenerateGetObjectURLUtils.getObjectUrlWithSign(true, "1253960454",
                "androidtest",Region.AP_Guangzhou.getRegion(), "append4.txt", 60 * 60, qCloudAPI);

        Log.d(TAG, url);
    }
}