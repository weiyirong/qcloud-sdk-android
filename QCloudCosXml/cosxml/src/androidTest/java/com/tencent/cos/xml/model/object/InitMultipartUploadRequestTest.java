package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.model.tag.InitMultipartUpload;
import com.tencent.qcloud.network.exception.QCloudException;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class InitMultipartUploadRequestTest {


    @Test public void checkParameters() throws Exception {

        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest();
        try {
            initMultipartUploadRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("bucket must not be null", e.getDetailMessage());
        }
        initMultipartUploadRequest.setBucket("bucket");
        try {
            initMultipartUploadRequest.checkParameters();
        } catch (QCloudException e) {
            assertEquals("cosPath must not be null", e.getDetailMessage());
        }
    }

    @Test public void getterAndSetter() throws Exception {

        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest();
        String cosPath = "cosPath";
        String cacheControl = "no-cache";
        String contentDisposition = "attach";
        String contentEncodeing = "utf-8";
        String expire = "expire";
        initMultipartUploadRequest.setCosPath(cosPath);
        assertEquals(cosPath, initMultipartUploadRequest.getCosPath());
        initMultipartUploadRequest.setCacheControl(cacheControl);
        initMultipartUploadRequest.setContentDisposition(contentDisposition);
        initMultipartUploadRequest.setContentEncodeing(contentEncodeing);
        initMultipartUploadRequest.setExpires(expire);
        initMultipartUploadRequest.setXCOSMeta("key", "value");
        initMultipartUploadRequest.setXCOSACL("acl");
        //putObjectRequest.setXCOSACL(new COSACL(""));
        List<String> list = new LinkedList<>();
        list.add("12");
        initMultipartUploadRequest.setXCOSGrantReadWithUIN(list);
        initMultipartUploadRequest.setXCOSGrantWriteWithUIN(list);
        initMultipartUploadRequest.setXCOSGrantWrite(list);
        initMultipartUploadRequest.setXCOSReadWriteWithUIN(list);
        initMultipartUploadRequest.setXCOSReadWrite(list);
    }
}