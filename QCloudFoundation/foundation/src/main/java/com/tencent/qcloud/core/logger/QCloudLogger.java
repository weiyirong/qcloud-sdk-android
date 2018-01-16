package com.tencent.qcloud.core.logger;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 *
 */
public final class QCloudLogger {

    private static RecordLog recordLog;

    /**
     * Priority constant for the println method; use LogUtils.v.
     */
    private static final int VERBOSE = Log.VERBOSE;

    /**
     * Priority constant for the println method; use LogUtils.d.
     */
    private static final int DEBUG = Log.DEBUG;

    /**
     * Priority constant for the println method; use LogUtils.i.
     */
    private static final int INFO = Log.INFO;

    /**
     * Priority constant for the println method; use LogUtils.w.
     */
    private static final int WARN = Log.WARN;

    /**
     * Priority constant for the println method; use LogUtils.e.
     */
    private static final int ERROR = Log.ERROR;

    public static void setUp(Context context) {
        if(context == null) {
            return;
        }
        recordLog = RecordLog.getInstance(context, "cloud");
    }

    private QCloudLogger() {
    }

    public static int v(String tag, String format, Object... args) {
        if (isLoggable(tag, VERBOSE)) {
            try {
                return Log.v(tag, args.length > 0 ? String.format(format, args) : format);
            } catch (Exception e) {
                return Log.v(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int v(String tag, Throwable tr, String format, Object... args) {
        if (isLoggable(tag, VERBOSE)) {
            try {
                return Log.v(tag, args.length > 0 ? String.format(format, args) : format, tr);
            } catch (Exception e) {
                return Log.v(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int d(String tag, String format, Object... args) {
        if (isLoggable(tag, DEBUG)) {
            try {
                return Log.d(tag, args.length > 0 ? String.format(format, args) : format);
            } catch (Exception e) {
                return Log.d(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int d(String tag, Throwable tr, String format, Object... args) {
        if (isLoggable(tag, DEBUG)) {
            try {
                return Log.d(tag, args.length > 0 ? String.format(format, args) : format, tr);
            } catch (Exception e) {
                return Log.d(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int i(String tag, String format, Object... args) {
        if (isLoggable(tag, INFO)) {
            try {
                String message = args.length > 0 ? String.format(format, args) : format;
                int r = Log.i(tag, message);
                flush(tag, RecordLevel.INFO, message, null);
                return r;
            } catch (Exception e) {
                return Log.i(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int i(String tag, Throwable tr, String format, Object... args) {
        if (isLoggable(tag, INFO)) {
            try {
                String message = args.length > 0 ? String.format(format, args) : format;
                int r = Log.i(tag, message, tr);
                flush(tag, RecordLevel.INFO, message, tr);
                return r;
            } catch (Exception e) {
                return Log.i(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int w(String tag, String format, Object... args) {
        if (isLoggable(tag, WARN)) {
            try {
                String message = args.length > 0 ? String.format(format, args) : format;
                int r = Log.w(tag, message);
                flush(tag, RecordLevel.INFO, message, null);
                return r;
            } catch (Exception e) {
                return Log.w(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int w(String tag, Throwable tr, String format, Object... args) {
        if (isLoggable(tag, WARN)) {
            try {
                String message = args.length > 0 ? String.format(format, args) : format;
                int r = Log.w(tag, message, tr);
                flush(tag, RecordLevel.INFO, message, tr);
                return r;
            } catch (Exception e) {
                return Log.w(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int e(String tag, String format, Object... args) {
        if (isLoggable(tag, ERROR)) {
            try {
                String message = args.length > 0 ? String.format(format, args) : format;
                int r = Log.e(tag, message);
                flush(tag, RecordLevel.INFO, message, null);
                return r;
            } catch (Exception e) {
                return Log.e(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static int e(String tag, Throwable tr, String format, Object... args) {
        if (isLoggable(tag, ERROR)) {
            try {
                String message = args.length > 0 ? String.format(format, args) : format;
                int r = Log.e(tag, message, tr);
                flush(tag, RecordLevel.INFO, message, tr);
                return r;
            } catch (Exception e) {
                return Log.e(tag, format + ": !!!! Log format exception: ", e);
            }
        }
        return 0;
    }

    public static boolean isTagLoggable(String tag) {
        return isLoggable(tag, Log.DEBUG);
    }

    /**
     * Checks to see whether or not a log for the specified tag is loggable at the specified level.
     */
    private static boolean isLoggable(String tag, int level) {
        if (TextUtils.isEmpty(tag) || tag.length() >= 23) {
            return false;
        }

        // default level : INFO
        // use "adb shell setprop log.tag.[tag] DEBUG" to enable DEBUG level for specific tag
        return Log.isLoggable(tag, level);
    }

    private static void flush(String tag, RecordLevel level, String msg, Throwable t) {
        // info级别以上的写入文件
        if (level.ordinal() >= RecordLevel.INFO.ordinal()) {
            if (recordLog != null) {
                recordLog.appendRecord(tag, level, msg, t);
            }
        }
    }
}
