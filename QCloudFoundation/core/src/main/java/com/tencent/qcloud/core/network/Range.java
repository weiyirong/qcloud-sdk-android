package com.tencent.qcloud.core.network;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class Range {

    private long start;

    private long end;

    public Range(long start, long end) {

        this.start = start;
        this.end = end;
    }

    public Range(long start) {
        this.start = start;
        this.end = -1;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    @Override
    public String toString() {

        String endString = end == -1 ? "" : String.valueOf(end);

        return String.format(Locale.ENGLISH, "bytes=%d-%s", start, endString);
    }
}
