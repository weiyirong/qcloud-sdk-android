package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudProgressListener;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.Range;
import com.tencent.qcloud.core.network.response.serializer.ResponseFilePartSerializer;
import com.tencent.qcloud.core.network.response.serializer.ResponseFileSerializer;


import java.io.File;
import java.util.Map;

/**
 * <p>
 * Bucket 中将一个文件（Object）下载至本地。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#getObject(GetObjectRequest)
 * @see com.tencent.cos.xml.CosXml#getObjectAsync(GetObjectRequest, CosXmlResultListener)
 */
final public class GetObjectRequest extends CosXmlRequest {

    private String rspContentType;
    private String rspContentLanguage;
    private String rspExpires;
    private String rspCacheControl;
    private String rspContentDisposition;
    private String rspContentEncoding;

    private String cosPath;
    private Range range;
    private QCloudProgressListener progressListener;
    private String savePath; //保留本地文件夹路径

    private String downloadUrl;
    private String host;

    public GetObjectRequest(String bucket, String cosPath, String savePath){
        setBucket(bucket);
        this.cosPath = cosPath;
        contentType = QCloudNetWorkConstants.ContentType.X_WWW_FORM_URLENCODED;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        this.savePath = savePath;
    }

    public GetObjectRequest(String downloadUrl, String savePath){
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        parseDownloadUrl();
        setBucket(null);
    }

    @Override
    protected void build() throws CosXmlClientException {
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_LOW;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        if(downloadUrl != null){
            requestOriginBuilder.host(host);
        }else {
            requestOriginBuilder.hostAddFront(bucket);
        }

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Object object : requestQueryParams.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
            }
        }

        if(requestHeaders.size() > 0){
            for(Object object : requestHeaders.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
            }
        }

        if(range != null){
            ResponseFilePartSerializer responseFileSerializer  = new ResponseFilePartSerializer(getDownloadPath(),
                    range, GetObjectResult.class);
            responseFileSerializer.setProgressListener(progressListener);
            responseBodySerializer = responseFileSerializer;
        }else{
            ResponseFileSerializer responseFileSerializer = new ResponseFileSerializer(getDownloadPath(),GetObjectResult.class);
            responseFileSerializer.setProgressListener(progressListener);
            responseBodySerializer = responseFileSerializer;
        }

    }

    @Override
    public void setBucket(String bucket) {
       if(downloadUrl != null)return;
       else super.setBucket(bucket);
    }

    @Override
    protected void setRequestQueryParams() {
        if(rspContentType != null){
            requestQueryParams.put("response-content-type",rspContentType);
        }
        if(rspContentLanguage != null){
            requestQueryParams.put("response-content-language",rspContentLanguage);
        }
        if(rspExpires != null){
            requestQueryParams.put("response-expires",rspExpires);
        }
        if(rspCacheControl != null){
            requestQueryParams.put("response-cache-control",rspCacheControl);
        }
        if(rspContentDisposition != null){
            requestQueryParams.put("response-content-dispositio",rspContentDisposition);
        }
        if(rspContentEncoding != null){
            requestQueryParams.put("response-content-encoding", rspContentEncoding);
        }
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null && downloadUrl== null){
            throw new CosXmlClientException("bucket or url must not be null");
        }
        if(cosPath == null){
            throw new CosXmlClientException("cosPath must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.GET;
    }

    @Override
    protected void setRequestPath() {
        if(cosPath != null){
            if(!cosPath.startsWith("/")){
                requestPath = "/" + cosPath;
            }else{
                requestPath = cosPath;
            }
        }
    }

    /**
     * <p>
     * 设置响应头部中的 Content-Type 参数。
     * </p>
     *
     * @param rspContentType Content-Type 参数。
     */
    public void setRspContentType(String rspContentType) {
        this.rspContentType = rspContentType;

    }

    /**
     * 获取设置的响应头Content-Type 参数。
     *
     * @return Content-Type 参数。
     */
    public String getRspContentType() {
        return rspContentType;
    }

    /**
     * 设置响应头部中的 Content-Language 参数。
     *
     * @param rspContentLanguage Content-Language 参数。
     */
    public void setRspContentLanguage(String rspContentLanguage) {
        this.rspContentLanguage = rspContentLanguage;

    }

    /**
     * 获取用户设置的Content-Language 参数。
     *
     * @return Content-Language 参数。
     */
    public String getRspContentLanguage() {
        return rspContentLanguage;
    }

    /**
     * 设置响应头部中的 Content-Expires 参数。
     *
     * @param rspExpires Content-Expires 参数。
     */
    public void setRspExpires(String rspExpires) {
        this.rspExpires = rspExpires;

    }

    /**
     * 获取用户设置的Content-Expires 参数。
     *
     * @return Content-Expires 参数。
     */
    public String getRspExpires() {
        return rspExpires;
    }

    /**
     * 设置响应头部中的 Cache-Control 参数。
     *
     * @param rspCacheControl Cache-Control 参数。
     */
    public void setRspCacheControl(String rspCacheControl) {
        this.rspCacheControl = rspCacheControl;

    }

    /**
     * 获取用户设置的Cache-Control 参数。
     *
     * @return Cache-Control 参数。
     */
    public String getRspCacheControl() {
        return rspCacheControl;
    }

    /**
     * 设置响应头部中的 Content-Disposition 参数。
     *
     * @param rspContentDispositon Content-Disposition 参数。
     */
    public void setRspContentDispositon(String rspContentDispositon) {
        this.rspContentDisposition = rspContentDispositon;

    }

    /**
     * 获取用户设置的Content-Disposition 参数。
     *
     * @return Content-Disposition 参数。
     */
    public String getRspContentDispositon() {
        return rspContentDisposition;
    }

    /**
     * 设置响应头部中的 Content-Encoding 参数。
     *
     * @param rspContentEncoding Content-Encoding 参数。
     */
    public void setRspContentEncoding(String rspContentEncoding) {
        this.rspContentEncoding = rspContentEncoding;

    }

    /**
     * 获取用户设置的 Content-Encoding 参数。
     *
     * @return Content-Encoding 参数。
     */
    public String getRspContentEncoding() {
        return rspContentEncoding;
    }

    /**
     * 设置下载的Object路径
     *
     * @param cosPath Object路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取设置的Object路径
     *
     * @return
     */
    public String getCosPath() {
        return cosPath;
    }

    /**
     * 设置下载的范围
     *
     * @param start 起点
     * @param end 终点
     */
    public void setRange(long start, long end) {
        if(start < 0) start = 0;
        Range range = new Range(start, end);
        requestHeaders.put("Range",range.toString());
        this.range = range;
    }

    /**
     * 设置下载的范围
     *
     * @param start 起点
     */
    public void setRange(long start) {
        if(start < 0) start = 0;
        Range range = new Range(start);
        requestHeaders.put("Range",range.toString());
        this.range = range;
    }

    /**
     * 获取设置的下载范围
     *
     * @return 下载范围
     */
    public Range getRange() {
        return range;
    }

    /**
     * <p>
     * 设置下载请求的 If-Modified-Since 头部。
     * </p>
     * <p>
     * 如果文件修改时间早于或等于指定时间，才返回文件内容。否则返回 412 (precondition failed)
     * </p>
     *
     * @param ifModifiedSince
     */
    public void setIfModifiedSince(String ifModifiedSince){
        if(ifModifiedSince != null){
            requestHeaders.put("If-Modified-Since",ifModifiedSince);
        }
    }

    /**
     * 设置请求进度监听器
     *
     * @param progressListener
     */
    public void setProgressListener(QCloudProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 获取设置的请求进度监听器
     *
     * @return
     */
    public QCloudProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * 设置文件的本地保存路径
     *
     * @param savePath
     */
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * 获取设置的文件本地保存路径
     *
     * @return
     */
    public String getSavePath() {
        return savePath;
    }

    protected String getDownloadPath(){
        String path  = null;
        if(savePath != null && cosPath != null){
            if(!savePath.endsWith("/")){
                path = savePath + "/";
            }else{
                path = savePath;
            }
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            int separator = cosPath.lastIndexOf("/");
            if(separator >= 0){
                path = path + cosPath.substring(separator + 1);
            }else{
                path = path + cosPath;
            }
        }
        return path;
    }

    private void parseDownloadUrl(){
        if(downloadUrl == null)return;
        int index = downloadUrl.indexOf("://");
        int last = downloadUrl.indexOf("/", index + 3);
        host = downloadUrl.substring(index + 3, last);
        index = last;
        last = downloadUrl.indexOf("?", index);
        cosPath = downloadUrl.substring(index, last == -1? downloadUrl.length() : last);
        if(last > 0){
            String[] queryArray = downloadUrl.substring(last + 1).split("&");
            for(String str : queryArray){
                String[] keyAndValue = str.split("=");
                if(keyAndValue.length == 2){
                    requestQueryParams.put(keyAndValue[0], keyAndValue[1]);
                }else if(keyAndValue[0].length() > 0){
                    requestQueryParams.put(keyAndValue[0], null);
                }
            }
        }
    }

}
