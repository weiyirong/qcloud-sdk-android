package com.tencent.cos.xml.transfer.cos;

import com.tencent.cos.xml.transfer.CosXmlBackgroundService;

/**
 * Created by rickenwang on 2019-11-20.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class CosXmlExtServiceHolder {

    private static volatile CosXmlExtServiceHolder instance;

    private volatile CosXmlBackgroundService cosXmlExtService;

    private CosXmlExtServiceHolder() {}

    public static CosXmlExtServiceHolder getInstance() {

        if (instance == null) {
            synchronized (CosXmlExtServiceHolder.class) {
                if (instance == null) {
                    instance = new CosXmlExtServiceHolder();
                }
            }
        }

        return instance;
    }


    synchronized public void setDefaultService(CosXmlBackgroundService cosXmlExtService) {

        this.cosXmlExtService = cosXmlExtService;
    }

    synchronized public CosXmlBackgroundService getDefaultService() {
        return cosXmlExtService;
    }
}
