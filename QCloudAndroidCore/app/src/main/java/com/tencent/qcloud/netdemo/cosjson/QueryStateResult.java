package com.tencent.qcloud.netdemo.cosjson;

import com.alibaba.fastjson.annotation.JSONField;
import com.tencent.qcloud.network.QCloudRequest;
import com.tencent.qcloud.network.QCloudResult;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QueryStateResult extends QCloudResult{

    private int code;

    private String message;

    @JSONField(name = "biz_attr")
    private int bizAttr;

    @JSONField(name = "filesize")
    private int fileSize;

    private String sha;

    @JSONField(name = "ctime")
    private String createTime;

    @JSONField(name = "mtime")
    private String modifyTime;

    @JSONField(name = "access_url")
    private String accessUrl;

    @JSONField(name = "source_url")
    private String sourceUrl;

    private String authority;

    @JSONField(name = "Cache-Control")
    private String cacheControl;

    @JSONField(name = "Content-Type")
    private String contentType;

    @JSONField(name = "Content-Disposition")
    private String contentDisposition;

    @JSONField(name = "Content-Language")
    private String contentLanguage;

    @JSONField(name = "Content-Encoding")
    private String contentEncoding;

    @JSONField(name = "x-cos-meta")
    private String xCosMeta;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getBizAttr() {
        return bizAttr;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public String getAuthority() {
        return authority;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public String getSha() {
        return sha;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getxCosMeta() {
        return xCosMeta;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setBizAttr(int bizAttr) {
        this.bizAttr = bizAttr;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setxCosMeta(String xCosMeta) {
        this.xCosMeta = xCosMeta;
    }
}


