package com.tencent.cos.xml.transfer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.QServer;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
@RunWith(AndroidJUnit4.class)
public class TransferTaskTest {

    @BeforeClass public static void init() {

        Context appContext = InstrumentationRegistry.getContext();
        QServer.init(appContext);
    }


    private File createTempFile(String pre, String suf, long size) throws IOException {

        File file = File.createTempFile(pre, suf);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 32; j++) {
                fileOutputStream.write(new byte[1024 * 1024 / 32]);
            }
        }
        return file;
    }



    private File get100MFile() throws IOException{

        return createTempFile("100MFile", ".txt", 100);
    }
}
