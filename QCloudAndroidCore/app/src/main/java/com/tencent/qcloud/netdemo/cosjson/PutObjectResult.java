package com.tencent.qcloud.netdemo.cosjson;

import com.alibaba.fastjson.annotation.JSONField;
import com.tencent.qcloud.network.QCloudResult;

import java.util.Locale;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class PutObjectResult extends QCloudResult {

    private int code;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JSONField(name = "access_url")
    private String accessUrl;

    @JSONField(name = "preview_url")
    private String previewUrl;

    @JSONField(name = "resource_path")
    private String resourcePath;

    @JSONField(name = "source_url")
    private String sourceUrl;

    @JSONField(name = "url")
    private String url;

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "code = %d, message = %s" + System.getProperty("line.separator")+
                "access url = %s" + System.getProperty("line.separator") +
                "preview url = %s" + System.getProperty("line.separator") +
                "resource path = %s" + System.getProperty("line.separator") +
                "source url = %s" + System.getProperty("line.separator") +
                "url = %s" + System.getProperty("line.separator"), code, message,
                accessUrl, previewUrl, resourcePath, sourceUrl, url);
    }
}
