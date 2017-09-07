package com.tencent.qcloud.network.assist;

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

    @Override
    public String toString() {

        return String.format(Locale.ENGLISH, "bytes %d-%d/%d", start, end, max);
    }
}
