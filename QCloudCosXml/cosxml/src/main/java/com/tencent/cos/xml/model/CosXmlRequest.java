package com.tencent.cos.xml.model;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.utils.URLEncodeUtils;
import com.tencent.qcloud.core.auth.COSXmlSignSourceProvider;
import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;
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

    public abstract String getMethod();

    protected abstract String getHostPrefix();

    public abstract String getPath();

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

    public void setRequestHeaders(String key, String value) throws CosXmlClientException {
        if(key != null && value != null){
            value = URLEncodeUtils.cosPathEncode(value);
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

    public String getHost(String appid, String region){
        String suffix = "myqcloud.com";
        String bucket = getHostPrefix();
        if(!bucket.endsWith("-" + appid)){
            bucket = bucket + "-" + appid;
        }
        return bucket + ".cos." + region + "." + suffix;
    }

    public void setSign(long signDuration){
        signSourceProvider = new COSXmlSignSourceProvider(signDuration);
    }

    public QCloudSignSourceProvider getSignSourceProvider() {
        if(signSourceProvider == null){
            signSourceProvider = new COSXmlSignSourceProvider(600);
        }
        return signSourceProvider;
    }

    public void setSign(long signDuration, Set<String> parameters, Set<String> headers){
        COSXmlSignSourceProvider cosXmlSignSourceProvider = new COSXmlSignSourceProvider(signDuration);
        cosXmlSignSourceProvider.parameters(parameters);
        cosXmlSignSourceProvider.headers(headers);
        signSourceProvider = cosXmlSignSourceProvider;
    }

    public void setTask(HttpTask httpTask){
        this.httpTask = httpTask;
    }

    public HttpTask getHttpTask(){
        return httpTask;
    }
}
