package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSStorageClass;
import com.tencent.cos.xml.common.Permission;
import com.tencent.cos.xml.common.Region;
import com.tencent.cos.xml.utils.FileUtils;
import com.tencent.cos.xml.utils.MD5Utils;
import com.tencent.cos.xml.utils.SHA1Utils;
import com.tencent.cos.xml.utils.StringUtils;

import org.junit.Test;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class UtilsTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public UtilsTest() {
        super(Application.class);
    }
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }
    @Test
    public void testUtils() throws Exception{
        init();
        byte[] data = FileUtils.getFileContent(qBaseServe.crateFile(1024 * 1024), 0, 1024 );
        assertEquals(true, data != null);
        try {
            FileUtils.getFileContent(null, 0, 1024 );
        }catch (Exception e){
            assertEquals(true,true);
        }

        assertEquals(true, data != null);
        String result = MD5Utils.getMD5FromBytes(data, 0, data.length);
        assertEquals(true, result != null);
        result = MD5Utils.getMD5FromPath(qBaseServe.crateFile(1024 * 1024));
        assertEquals(true, result != null);
        result = MD5Utils.getMD5FromString("md5utils unit test");
        assertEquals(true, result != null);
        result = SHA1Utils.getSHA1FromBytes(data, 0, data.length);
        assertEquals(true, result != null);
        result = SHA1Utils.getSHA1FromPath(qBaseServe.crateFile(1024 * 1024));
        assertEquals(true, result != null);
        result = SHA1Utils.getSHA1FromString("sha1utils unit test");
        assertEquals(true, result != null);
        String web = "http://www.qcloud.com:8080/index?sign = a#123";
        result = StringUtils.getFragment(web);
        assertEquals("123", result);
        result = StringUtils.getHost(web);
        assertEquals("www.qcloud.com", result);
        result = StringUtils.getPath(web);
        assertEquals("/index", result);
        result = StringUtils.getQuery(web);
        assertEquals("sign = a", result);
        result = StringUtils.getScheme(web);
        assertEquals("http", result);
        long port = StringUtils.getPort(web);
        assertEquals(8080, port);
        String web2 = "http://www.qcloud.com/space space";
        result = StringUtils.encodedUrl(web2);
        assertEquals("http%3A/www.qcloud.com/space%20space",result);
    }

    @Test
    public void testEnum() throws Exception{
        String result = COSACL.PRIVATE.getACL();
        assertEquals("private", result);
        result = COSACL.PUBLIC_READ.getACL();
        assertEquals("public-read", result);
        result = COSACL.PUBLIC_READ_WRITE.getACL();
        assertEquals("public-read-write", result);

        result = COSStorageClass.NEARLINE.getStorageClass();
        assertEquals("Nearline", result);
        result = COSStorageClass.STANDARD.getStorageClass();
        assertEquals("Standard", result);
        result = COSStorageClass.STANDARD_IA.getStorageClass();
        assertEquals("Standard_IA", result);

        result = Region.AP_Beijing.getRegion();
        assertEquals("ap-beijing", result);
        result = Region.AP_Beijing_1.getRegion();
        assertEquals("ap-beijing-1", result);
        result = Region.AP_Shanghai.getRegion();
        assertEquals("ap-shanghai", result);
        result = Region.AP_Guangzhou.getRegion();
        assertEquals("ap-guangzhou", result);
        result = Region.AP_Guangzhou_2.getRegion();
        assertEquals("ap-guangzhou-2", result);
        result = Region.AP_Chengdu.getRegion();
        assertEquals("ap-chengdu", result);
        result = Region.AP_Singapore.getRegion();
        assertEquals("ap-singapore", result);
        result = Region.AP_Hongkong.getRegion();
        assertEquals("ap-hongkong", result);
        result = Region.NA_Toronto.getRegion();
        assertEquals("na-toronto", result);
        result = Region.CN_EAST.getRegion();
        assertEquals("cn-east", result);
        result = Region.CN_NORTH.getRegion();
        assertEquals("cn-north", result);
        result = Region.CN_SOUTH.getRegion();
        assertEquals("cn-south", result);
        result = Region.CN_SOUTHWEST.getRegion();
        assertEquals("cn-southwest", result);
        result = Region.SG.getRegion();
        assertEquals("sg", result);

        result = Permission.FULL_CONTROL.getPermission();
        assertEquals("FULL_CONTROL", result);
        result = Permission.READ.getPermission();
        assertEquals("READ", result);
        result = Permission.WRTIE.getPermission();
        assertEquals("WRITE", result);

    }
}
