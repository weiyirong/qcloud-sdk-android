package com.tencent.qcloud.core.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by wjielai on 2017/10/16.
 */
public class QCloudHttpUtilsTest {
    @Test
    public void urlEncodeWithSlash() throws Exception {
        String fileId = "/card/a/b/folder/t.txt";
        assertEquals(fileId, QCloudHttpUtils.urlEncodeWithSlash(fileId));

        assertEquals("/sd%3Fcard/no/%E4%B8%BA%E6%9C%AC.txt", QCloudHttpUtils.urlEncodeWithSlash(
                "/sd?card/no/为本.txt"
        ));
    }

    @Test
    public void urlEncodeString() throws Exception {
        assertEquals("%2Fsd%3Fcard%2Fno%2F%E4%B8%BA%E6%9C%AC.txt", QCloudHttpUtils.urlEncodeString(
                "/sd?card/no/为本.txt"
        ));
    }

    @Test
    public void urlDecodeString() throws Exception {
        assertEquals("/sd?card/no/为本.txt", QCloudHttpUtils.urlDecodeString(
                "%2fsd%3Fcard%2fno%2f%E4%B8%BA%E6%9C%AC.txt"
        ));
    }

    @Test
    public void source() throws Exception {
        Map<String, String> keyvalues = new HashMap<>();

        keyvalues.put("key1", "value1");
        keyvalues.put("key2", "value2");

        assertEquals("key2=value2&key1=value1", QCloudHttpUtils.queryParametersString(keyvalues));

        assertNull(QCloudHttpUtils.queryParametersString(null));
    }

    @Test
    public void headersStringForKeys() throws Exception {

    }

}