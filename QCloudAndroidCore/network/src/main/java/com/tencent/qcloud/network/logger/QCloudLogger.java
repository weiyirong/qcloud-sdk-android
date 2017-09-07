package com.tencent.qcloud.network.logger;

import android.content.Context;
import android.text.TextUtils;

import org.slf4j.Logger;

/**
 *
 */
public class QCloudLogger {

    private static boolean enableDebug = true;
    private static boolean enableInfo = true;
    private static boolean enableWarn = true;
    private static boolean enableError = true;

    private static Context context;
    private static String flag = "core";

    public static void setContext(Context context) {
        QCloudLogger.context = context;
    }

    private QCloudLogger() {
    }

    public static void disableDebug() {

        enableDebug = false;
    }

    public static void enableDebug() {
        enableDebug = true;
        enableInfo = true;
        enableWarn = true;
        enableError = true;
    }

    public static void disableInfo() {

        enableInfo = false;
    }


    public static void enableInfo() {

        enableInfo = true;
        enableWarn = true;
        enableError = true;
    }

    public static void disableWarn() {

        enableWarn = false;
    }


    public static void enableWarn() {

        enableWarn = true;
        enableError = true;
    }

    public static void disableError() {
        enableError = false;
    }


    public static void enableError() {

        enableError = true;
    }

    public static void reset() {
        enableDebug();
    }

    public static void debug(Logger logger, String message) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if(enableDebug && enableInfo && enableWarn && enableError) {
            logger.debug(message);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "debug", message, null);
        }
    }

    public static void debug(Logger logger, String message, Object... objects) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if(enableDebug && enableInfo && enableWarn && enableError) {
            logger.debug(message, objects);

        }

        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "debug", message+objects2String(objects), null);
        }
    }

    public static void info(Logger logger, String message) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if (enableInfo && enableWarn && enableError) {
            logger.info(message);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "info", message, null);
        }
    }

    public static void info(Logger logger, String message, Object... objects) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if (enableInfo && enableWarn && enableError) {
            logger.info(message, objects);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "info", message+objects2String(objects), null);
        }
    }

    public static void warn(Logger logger, String message) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if (enableWarn && enableError) {
            logger.warn(message);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "warn", message, null);
        }
    }

    public static void warn(Logger logger, String message, Object... objects) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if (enableWarn && enableError) {
            logger.warn(message, objects);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "warn", message+objects2String(objects), null);
        }
    }

    public static void error(Logger logger, String message) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if (enableError) {
            logger.error(message);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "error", message, null);
        }
    }

    public static void error(Logger logger, String message, Object... objects) {

        if (TextUtils.isEmpty(message)) {
            return ;
        }
        if (enableError) {
            logger.error(message, objects);
        }
        if (enableDebug && context != null) {
            RecordLog recordLog = RecordLog.getInstance(context, flag);
            recordLog.appendRecord(logger.getName(), "error", message+objects2String(objects), null);
        }
    }

    private static String objects2String(Object... objects) {

        if (objects == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (Object object : objects) {
            str.append(object.toString());
            str.append(", ");
        }
        return str.toString();
    }
}
