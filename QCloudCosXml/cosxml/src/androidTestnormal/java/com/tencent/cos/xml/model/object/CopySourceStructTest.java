package com.tencent.cos.xml.model.object;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by bradyxiao on 2018/4/4.
 */
@RunWith(AndroidJUnit4.class)
public class CopySourceStructTest {

    @Test
    public void test() throws Exception{
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                "appid", "bucket", "region", "cosPath", "versionId");
        Assert.assertEquals("bucket-appid.cos.region.myqcloud.com/cosPath?versionId=versionId", copySourceStruct.getSource());
    }

}