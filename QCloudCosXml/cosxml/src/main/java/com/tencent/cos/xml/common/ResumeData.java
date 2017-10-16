package com.tencent.cos.xml.common;

/**
 * <p>
 * 分片上传续传时需要信息。
 * </p>
 */
public class ResumeData {
    public String uploadId;
    public String bucket;
    public String srcPath;
    public String cosPath;
    public int sliceSize;
    public ResumeData(){}
}
