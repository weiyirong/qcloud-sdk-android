package com.tencent.qcloud.core.logger;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by wjielai on 2017/10/13.
 */

@RunWith(AndroidJUnit4.class)
public class RecordLogTest {

    private RecordLog recordLog;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getContext();
        recordLog = RecordLog.getInstance(context, "logTest");
    }

    @Test
    public void testAppendLog() throws Exception {
        for (int i = 0 ; i < 200; i++) {
            recordLog.appendRecord("QCloudTag", RecordLevel.VERBOSE, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.DEBUG, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.INFO, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.WARN, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.ERROR, "this is a log message", null);
        }

        TimeUnit.SECONDS.sleep(2);

        for (int i = 0 ; i < 200; i++) {
            recordLog.appendRecord("QCloudTag", RecordLevel.VERBOSE, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.DEBUG, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.INFO, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.WARN, "this is a log message", null);
            recordLog.appendRecord("QCloudTag", RecordLevel.ERROR, "this is a log message", null);
        }

        TimeUnit.SECONDS.sleep(2);

        File logFir = recordLog.getLogFileDir();
        Assert.assertTrue(logFir.list().length > 1);
    }

}
