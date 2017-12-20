package com.tencent.qcloud.core.network;

import com.tencent.qcloud.core.util.QCStringUtils;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ContentRange {

    private long max;

    private long start;

    private long end;


    public ContentRange(long start, long end, long max) {

        this.start = start;
        this.end = end;
        this.max = max;
    }

    public ContentRange(long start, long end) {

        this.start = start;
        this.end = end;
    }


    public long getEnd() {
        return end;
    }

    public long getMax() {
        return max;
    }

    public long getStart() {
        return start;
    }

    public static ContentRange newContentRange(String contentRange) {
        if (QCStringUtils.isEmpty(contentRange)) {
            return null;
        }
        int lastBlankIndex = contentRange.lastIndexOf(" ");
        int acrossIndex = contentRange.indexOf("-");
        int slashIndex = contentRange.indexOf("/");
        if (lastBlankIndex == -1 || acrossIndex == -1 || slashIndex == -1) {
            return null;
        }

        long start = Long.parseLong(contentRange.substring(lastBlankIndex + 1, acrossIndex));
        long end = Long.parseLong(contentRange.substring(acrossIndex + 1, slashIndex));
        long max = Long.parseLong(contentRange.substring(slashIndex + 1));

        return new ContentRange(start, end, max);
    }

    @Override
    public String toString() {

        return String.format(Locale.ENGLISH, "bytes %d-%d/%d", start, end, max);
    }
}
