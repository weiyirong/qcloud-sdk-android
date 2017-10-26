package com.tencent.cos.xml.model;

import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.qcloud.core.network.QCloudHttpRequest;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.auth.COSXmlSignSourceProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * COS XML服务请求的基类，主要包含了请求的公共参数和<a
 * href="https://cloud.tencent.com/document/product/436/7751"> 公共HTTP头部文档 </a> 设置。
 * </p>
 *
 * <br>
 * 其中需要您关心的公共请求头部有：
 * <ul>
 * <li>Authorization : {@link CosXmlRequest#setSign(long, Set, Set)}，必须设置 </li>
 * <li>Content-MD5 : {@link CosXmlRequest#setContentMd5(String)}，可选设置，默认为空</li>
 * <li>Expect : {@link CosXmlRequest#setExpect(String)} ，可选设置，默认为空</li>
 * </ul>
 * <br>
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class CosXmlRequest<T extends QCloudResult> extends QCloudHttpRequest<T> {

    /** request method: get or post or put or delete or options or head */
    protected String requestMethod = QCloudNetWorkConstants.RequestMethod.GET;
    /** request path for url*/
    protected String requestPath;
    /** request query parameters for url*/
    protected Map<String,String> requestQueryParams = new LinkedHashMap<String, String>();
    /** request headers */
    protected Map<String,String> requestHeaders = new LinkedHashMap<String, String>();
    /** request parameters for body */
    protected Map<String,String> requestBodyParams = new LinkedHashMap<String, String>();


    /** common headers*/
    protected String contentType = QCloudNetWorkConstants.ContentType.XML;
    protected String expect;
    protected String contentMd5;


    /** parameters for cos  */
    protected String bucket;


    /**
     * 设置Bucket
     *
     * @param bucket, bucket for cos.<a href= "https://www.qcloud.com/document/product/436/6225"></a>
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * 给请求设置签名。
     *
     * @param signDuration 签名有效期，单位 s
     * @param checkHeaderListForSign 需要进行签名的头部
     * @param checkParameterListForSign 需要进行签名的请求参数
     */
    public void setSign(long signDuration, Set<String> checkHeaderListForSign, Set<String> checkParameterListForSign) {

        if(signDuration < 0)signDuration = 600;
        COSXmlSignSourceProvider cosSignSourceProvider = new COSXmlSignSourceProvider(signDuration);
        if(checkHeaderListForSign != null){
            cosSignSourceProvider.headers(checkHeaderListForSign);
        }
        if(checkParameterListForSign != null){
            cosSignSourceProvider.parameters(checkParameterListForSign);
        }
        signSourceProvider = cosSignSourceProvider;
        setSignerType("CosXmlSigner");
    }

    public void setSign(long signDuration){
        if(signDuration < 0)signDuration = 600;
        COSXmlSignSourceProvider cosSignSourceProvider = new COSXmlSignSourceProvider(signDuration);
        signSourceProvider = cosSignSourceProvider;
        setSignerType("CosXmlSigner");
    }

    /**
     * 设置Expect头部。
     *
     * @param expect Expect值
     */
    public void setExpect(String expect) {
        if(expect == null)return;
        this.expect = expect;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.EXPECT,expect);
    }

    /**
     * 设置Content-MD5头部
     *
     * @param contentMd5 Content-MD5值
     */
    public void setContentMd5(String contentMd5) {
        if(contentMd5 == null) return;
        this.contentMd5 = contentMd5;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.MD5, contentMd5);
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getRequestQueryParams() {
        return requestQueryParams;
    }

    public Map<String, String> getRequestBodyParams() {
        return requestBodyParams;
    }

    public String getExpect() {
        return expect;
    }

    public String getContentMd5() {
        return contentMd5;
    }

    public String getRequestPath(){
        return requestPath;
    }

    protected abstract void setRequestMethod();

    protected abstract void setRequestPath();

    protected abstract void setRequestQueryParams();

    @Override
    protected void build() throws CosXmlClientException {
        checkParameters();
        requestOriginBuilder.scheme(CosXmlServiceConfig.getInstance().getScheme());
        requestOriginBuilder.hostAddFront(CosXmlServiceConfig.getInstance().getHttpHost());
        if(signSourceProvider == null){
            setSign(2 * 60);
        }
    }

    protected abstract void checkParameters() throws CosXmlClientException;
}
