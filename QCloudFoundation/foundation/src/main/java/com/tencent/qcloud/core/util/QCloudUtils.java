package com.tencent.qcloud.core.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;

/**
 * <p>
 * </p>
 * Created by wjielai on 2018/6/1.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class QCloudUtils {

    public static long getUriContentLength(Uri uri, ContentResolver contentResolver) {
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = contentResolver.query(uri,
                    null, null, null, null);
            if (cursor != null) {
                try {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    cursor.moveToFirst();
                    return cursor.getLong(sizeIndex);
                } finally {
                    cursor.close();
                }
            }
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            File file = new File(uri.getPath());
            return file.length();
        }

        return -1;
    }
}
