package com.tencent.cos.xml.transfer;

import com.tencent.qcloud.core.http.QCloudHttpClient;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * </p>
 * Created by wjielai on 2020-01-17.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class COSXMLTaskHelper {

    private AtomicInteger successCount = new AtomicInteger(0);

    private int tolerance;

    private final int initTolerance;
    private final static int MAX_TOLERANCE = 3;
    private final static int INC_STEP = 3;

    COSXMLTaskHelper(int initTolerance) {
        this.tolerance = initTolerance;
        this.initTolerance = initTolerance;
    }

    boolean shouldContinue() {
        synchronized (COSXMLTaskHelper.class) {
            boolean sc = tolerance > 0;
            tolerance--;
            QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, "COSXMLTaskHelper tolerance to  : " + tolerance);
            return sc;
        }
    }

    void markFailStep() {
        successCount.set(0);
        QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, "COSXMLTaskHelper cont-success to  : 0 ");
    }

    void markSuccessStep() {
        int ns = successCount.incrementAndGet();
        QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, "COSXMLTaskHelper cont-success to : " + ns);
        synchronized (COSXMLTaskHelper.class) {
            if (ns%INC_STEP == 0 && tolerance < MAX_TOLERANCE && tolerance + 1 == ns/INC_STEP) {
                tolerance += 1;
                QCloudLogger.i(QCloudHttpClient.HTTP_LOG_TAG, "COSXMLTaskHelper tolerance to  : " + tolerance);
            }
        }
    }

    void reset() {
        successCount.set(0);
        tolerance = initTolerance;
    }
}
