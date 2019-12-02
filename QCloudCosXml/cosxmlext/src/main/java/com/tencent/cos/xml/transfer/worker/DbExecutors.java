package com.tencent.cos.xml.transfer.worker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class DbExecutors {

    public static final ThreadPoolExecutor DB_EXECUTOR;

    static {

        DB_EXECUTOR = new ThreadPoolExecutor(2, 2, 2, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
    }

}
