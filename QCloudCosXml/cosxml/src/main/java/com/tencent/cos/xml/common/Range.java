package com.tencent.cos.xml.common;

/**
 * Created by bradyxiao on 2017/12/5.
 *
 */

public class Range {
    private long start;
    private long end;
    public Range(long start, long end){
        this.start = start;
        this.end = end;
    }

    public Range(long start){
        this(start, -1);
    }

    public long getEnd() {
        return end;
    }

    public long getStart() {
        return start;
    }

    public String getRange(){
        return String.format("bytes=%s-%s", start, (end == -1 ?"": String.valueOf(end)));
    }

}
