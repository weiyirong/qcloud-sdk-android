package com.tencent.cos.xml.model;

import android.text.TextUtils;

import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.utils.URLEncodeUtils;
import com.tencent.qcloud.core.auth.COSXmlSignSourceProvider;
import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.common.QCloudTaskStateListener;
import com.tencent.qcloud.core.http.HttpConstants;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bradyxiao on 2017/11/30.
 * <p>
 *  请求基类
 * </p>
 * @see com.tencent.cos.xml.model.object.PutObjectRequest
 * @see com.tencent.cos.xml.model.object.InitMultipartUploadRequest
 * @see com.tencent.cos.xml.model.object.UploadPartResult
 * @see com.tencent.cos.xml.model.object.CompleteMultiUploadRequest
 * @see com.tencent.cos.xml.model.object.ListPartsRequest
 * @see com.tencent.cos.xml.model.object.AbortMultiUploadRequest
 * ...
 */

public abstract class CosXmlRequest{

    protected Map<String, String> queryParameters = new LinkedHashMap<>();
    protected Map<String, List<String>> requestHeaders = new LinkedHashMap<>();
    private QCloudSignSourceProvider signSourceProvider;
    private HttpTask httpTask;
    private boolean isNeedMD5 = false;
    private boolean isSupportAccelerate = false;
    private String region;
    protected String domainSuffix;
    protected String requestURL;

    protected QCloudTaskStateListener qCloudTaskStateListener;

    public void setRequestURL(String requestURL){
        this.requestURL = requestURL;
    }

    public String getRequestURL(){
        return requestURL;
    }

    public abstract String getMethod();

    protected abstract String getHostPrefix();

    public abstract String getPath(CosXmlServiceConfig config);

    public void setQueryParameters(java.util.Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public void setRequestHeaders(Map<String, List<String>> headers){
        if(headers != null){
            this.requestHeaders.putAll(headers);
        }
    }

    public Map<String, String> getQueryString(){
        return queryParameters;
    }

    public Map<String, List<String>> getRequestHeaders(){
        return requestHeaders;
    }

    public abstract RequestBodySerializer getRequestBody() throws CosXmlClientException;

    /**
     * sdk 参数校验
     * @throws CosXmlClientException
     */
    public abstract void checkParameters() throws CosXmlClientException;

    public boolean isNeedMD5(){
        return isNeedMD5;
    }

    public void setNeedMD5(boolean isNeedMD5){
        this.isNeedMD5 = isNeedMD5;
    }

    public void setTaskStateListener(QCloudTaskStateListener qCloudTaskStateListener){
        this.qCloudTaskStateListener = qCloudTaskStateListener;
    }

    public  QCloudTaskStateListener getTaskStateListener(){
        return qCloudTaskStateListener;
    }

    /**
     *  @see CosXmlRequest#setRequestHeaders(String, String, boolean)
     * @param key
     * @param value
     * @throws CosXmlClientException
     */
    @Deprecated
    public void setRequestHeaders(String key, String value) throws CosXmlClientException {
        if(key != null && value != null){
            value = URLEncodeUtils.cosPathEncode(value);
            addHeader(key, value);
        }
    }

    /**
     * set header value, and need to urlEncoder or not.
     * @param key
     * @param value
     * @param isUrlEncoder true, it need url encoder,otherwise, do not need
     */
    public void setRequestHeaders(String key, String value, boolean isUrlEncoder) throws CosXmlClientException {
        if(key != null && value != null){
            if(isUrlEncoder){
                value = URLEncodeUtils.cosPathEncode(value);
            }
            addHeader(key, value);
        }
    }

    protected void addHeader(String key, String value){
        List<String> values;
        if(requestHeaders.containsKey(key)){
            values = requestHeaders.get(key);
        }else {
            values = new ArrayList<String>();
        }
        values.add(value);
        requestHeaders.put(key, values);
    }

    /**
     * 获取实际的 host
     *
     * 默认域名：
     *
     * bucket-appid.cos.region.myqcloud.com
     *
     * 加速域名：
     *
     * bucket-appid.cos-accelerate.myqcloud.com
     *
     * CSP 域名：共四种
     *
     *   cos.region.domainSuffix/bucket-appid
     *   bucket-appid.cos.region.domainSuffix
     *
     *   cos.domainSuffix/bucket-appid
     *   bucket-appid.cos.domainSuffix
     *
     * @return host
     */
    private String getHost(String appid, String region, String domainSuffix, boolean isBucketInPath, boolean isSupportAccelerate) throws CosXmlClientException {

        this.domainSuffix = domainSuffix;
        String bucket = getHostPrefix();
        String realRegion = getRegion() == null ? region : getRegion();

        if(!bucket.endsWith("-" + appid) && !TextUtils.isEmpty(appid)){
            bucket = bucket + "-" + appid;
        }

        StringBuilder host = new StringBuilder();
        if (!isBucketInPath) {
            host.append(bucket);
            host.append(".");
        }

        if (isSupportAccelerate) {
            host.append("cos-accelerate");
        } else {
            host.append("cos");
            if (!TextUtils.isEmpty(realRegion)) {
                host.append(".");
                host.append(realRegion);
            }
        }
        host.append(".").append(domainSuffix);

        return host.toString();
    }

    public String getHost(CosXmlServiceConfig config, boolean isSupportAccelerate) throws CosXmlClientException {
        return getHost(config.getAppid(), config.getRegion(), config.getDomainSuffix(), config.isBucketInPath(), isSupportAccelerate);
    }

    public void isSupportAccelerate(boolean isSupportAccelerate){
        this.isSupportAccelerate = isSupportAccelerate;
    }

    public boolean isSupportAccelerate() {
        return isSupportAccelerate;
    }

    public void setSign(String sign){
        addHeader(HttpConstants.Header.AUTHORIZATION, sign);
    }

    public void setSign(long signDuration){
        signSourceProvider = new COSXmlSignSourceProvider().setDuration(signDuration);
    }

    public void setSign(long startTime, long endTime){
        signSourceProvider = new COSXmlSignSourceProvider().setSignBeginTime(startTime)
                .setSignExpiredTime(endTime);
    }

    public QCloudSignSourceProvider getSignSourceProvider() {
        if(signSourceProvider == null){
            signSourceProvider = new COSXmlSignSourceProvider().setDuration(600);
        }
        return signSourceProvider;
    }

    public void setSign(long signDuration, Set<String> parameters, Set<String> headers){
        COSXmlSignSourceProvider cosXmlSignSourceProvider = new COSXmlSignSourceProvider().setDuration(signDuration);
        cosXmlSignSourceProvider.parameters(parameters);
        cosXmlSignSourceProvider.headers(headers);
        signSourceProvider = cosXmlSignSourceProvider;
    }

    public void setSign(long startTime, long endTime, Set<String> parameters, Set<String> headers){
        COSXmlSignSourceProvider cosXmlSignSourceProvider = new COSXmlSignSourceProvider().setSignBeginTime(startTime)
                .setSignExpiredTime(endTime);
        cosXmlSignSourceProvider.parameters(parameters);
        cosXmlSignSourceProvider.headers(headers);
        signSourceProvider = cosXmlSignSourceProvider;
    }

    public void setSignSourceProvider(QCloudSignSourceProvider cosXmlSignSourceProvider){
        this.signSourceProvider = cosXmlSignSourceProvider;
    }

    public void setRegion(String region){
        if(!TextUtils.isEmpty(region)){
            this.region = region;
        }
    }

    public String getRegion(){
        return region;
    }

    public void setTask(HttpTask httpTask){
        this.httpTask = httpTask;
    }

    public HttpTask getHttpTask(){
        return httpTask;
    }
}
