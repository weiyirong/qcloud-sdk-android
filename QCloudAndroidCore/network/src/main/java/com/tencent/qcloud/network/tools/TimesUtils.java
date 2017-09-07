package com.tencent.qcloud.network.tools;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class TimesUtils {
    public static  String timeUtils(long seconds, String dateFormat){
        Date dat=new Date(seconds);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(dateFormat, Locale.CHINA);
        return format.format(gc.getTime());
    }
}