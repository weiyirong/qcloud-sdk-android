package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.common.COSRequestHeaderKey;
import com.tencent.cos.xml.common.Range;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import java.io.File;
import java.util.Map;

/**
 * <p>
 * Bucket 中将一个文件（Object）下载至本地。
 * </p>
 *
 */
final public class GetObjectRequest extends ObjectRequest {

    private String rspContentType;
    private String rspContentLanguage;
    private String rspExpires;
    private String rspCacheControl;
    private String rspContentDisposition;
    private String rspContentEncoding;
    private Range range;

    private CosXmlProgressListener progressListener;
    private String savePath; //保留本地文件夹路径

    private String downloadUrl;
    private String host;

    public GetObjectRequest(String bucket, String cosPath, String savePath){
        super(bucket, cosPath);
        this.savePath = savePath;
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
     * 设置下载的范围
     *
     * @param start 起点
     * @param end 终点
     */
    public void setRange(long start, long end) {
        if(start < 0) start = 0;
        Range range = new Range(start, end);
        addHeader(COSRequestHeaderKey.RANGE,range.getRange());
        this.range = range;
    }

    /**
     * 设置下载的范围
     *
     * @param start 起点
     */
    public void setRange(long start) {
       setRange(start, -1);
    }

    /**
     * 获取设置的下载范围
     *
     * @return 下载范围
     */
    public Range getRange(){
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
            addHeader(COSRequestHeaderKey.IF_MODIFIED_SINCE,ifModifiedSince);
        }
    }

    /**
     * 设置请求进度监听器
     *
     * @param progressListener
     */
    public void setProgressListener(CosXmlProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 获取设置的请求进度监听器
     *
     * @return
     */
    public CosXmlProgressListener getProgressListener() {
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

    public String getDownloadPath(){
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

    @Override
    public String getMethod() {
        return RequestMethod.GET;
    }

    @Override
    public Map<String, String> getQueryString() {
        if(rspContentType != null){
            queryParameters.put("response-content-type=",rspContentType);
        }
        if(rspContentLanguage != null){
            queryParameters.put("response-content-language",rspContentLanguage);
        }
        if(rspExpires != null){
            queryParameters.put("response-expires",rspExpires);
        }
        if(rspCacheControl != null){
            queryParameters.put("response-cache-control",rspCacheControl);
        }
        if(rspContentDisposition != null){
            queryParameters.put("response-content-disposition",rspContentDisposition);
        }
        if(rspContentEncoding != null){
            queryParameters.put("response-content-encoding",rspContentEncoding);
        }
        return super.getQueryString();
    }

    @Override
    public RequestBodySerializer getRequestBody() {
        return null;
    }
}
