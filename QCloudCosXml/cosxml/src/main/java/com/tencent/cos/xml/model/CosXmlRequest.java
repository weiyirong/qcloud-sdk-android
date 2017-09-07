package com.tencent.cos.xml.model;

import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.auth.BasicCredentialProvider;
import com.tencent.qcloud.network.auth.BasicSignatureSourceSerializer;
import com.tencent.qcloud.network.exception.QCloudException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public abstract class CosXmlRequest extends QCloudHttpRequest{

    /** request method: get or post or put or delete or options or head */
    protected String requestMethod = RequestMethod.GET;
    /** request path for url*/
    protected String requestPath;
    /** request query parameters for url*/
    protected Map<String,String> requestQueryParams = new LinkedHashMap();
    /** request fragment for url*/
    protected String requestFragment;

    /** request headers */
    protected Map<String,String> requestHeaders = new LinkedHashMap<String, String>();
    /** request parameters for body */
    protected Map<String,String> requestBodyParams = new LinkedHashMap<String, String>();


    /** common headers*/
    protected String contentType = RequestContentType.APPLICATION_XML;
    protected String expect;
    protected String xCOSContentSha1;


    /** parameters for cos  */
    protected String bucket;

    /** sign */
    BasicSignatureSourceSerializer cosXmlSignatureSourceSerializer;

    /**
     *  set bucket for request
     * @param bucket, bucket for cos.<a herf https://www.qcloud.com/document/product/436/6225></a>
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
     *  set sign parameters for request.
     * @param signDuration, expect durations for sign, in seconds.
     * @param checkHeaderListForSign, which header need to check for sign, it maybe null.
     * @param checkParameterListForSing, which query parameters need to check for sign,it maybe null.
     */
    public void setSign(long signDuration, Set<String> checkHeaderListForSign, Set<String> checkParameterListForSing) {
        if(signDuration < 0)signDuration = 600;
        cosXmlSignatureSourceSerializer = new BasicSignatureSourceSerializer(signDuration);
        if(checkHeaderListForSign != null){
            cosXmlSignatureSourceSerializer.headers(checkHeaderListForSign);
        }
        if(checkParameterListForSing != null){
            cosXmlSignatureSourceSerializer.parameters(checkParameterListForSing);
        }
        setSourceSerializer(cosXmlSignatureSourceSerializer);
    }

    public void setExpect(String expect) {
        if(expect == null)return;
        this.expect = expect;
        requestHeaders.put(RequestHeader.EXPECT,expect);
    }

    public String getExpect() {
        return expect;
    }

    public void setXCOSContentSha1(String xCOSContentSha1) {
        if(xCOSContentSha1 == null)return;
        this.xCOSContentSha1 = xCOSContentSha1;
        requestHeaders.put(RequestHeader.X_COS_CONTENT_SHA1,xCOSContentSha1);
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

    public String getXCOSContentSha1() {
        return xCOSContentSha1;
    }

    public String getRequestPath(){
        return requestPath;
    }

    protected abstract void setRequestMethod();

    protected abstract void setRequestPath();

    protected abstract void setRequestQueryParams();

    public abstract void checkParameters() throws QCloudException;
}
