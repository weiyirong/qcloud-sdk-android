package com.tencent.qcloud.core.cos.object;


import com.tencent.qcloud.core.cos.CosXmlRequest;

import java.io.File;

/**
 * <p>
 * Bucket 中将一个文件（Object）下载至本地。
 * </p>
 *
 */
public class GetObjectRequest extends CosXmlRequest<Void> {

    private String rspContentType;
    private String rspContentLanguage;
    private String rspExpires;
    private String rspCacheControl;
    private String rspContentDisposition;
    private String rspContentEncoding;

    private String cosPath;
    private String savePath; //保留本地文件夹路径

    public GetObjectRequest(String savePath){
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
}
