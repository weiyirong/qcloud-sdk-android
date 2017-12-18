package com.tencent.cos.xml.model.bucket;

import android.text.TextUtils;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;


import java.util.Map;


/**
 * <p>
 * 查询正在进行中的分块上传。
 * </p>
 * <p>
 * 单次请求操作最多列出 1000 个正在进行中的分块上传。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#listMultiUploads(ListMultiUploadsRequest)
 * @see com.tencent.cos.xml.CosXml#listMultiUploadsAsync(ListMultiUploadsRequest, CosXmlResultListener)
 */
final public class ListMultiUploadsRequest extends CosXmlRequest {

    /**Delimiter is a sign.
     * If Prefix exists, the same paths between Prefix and delimiter will be grouped as the same
     * type and defined Common Prefix, then all Common Prefixes will be listed.
     * If Prefix doesn't exist, the listing process will start from the beginning of the path*/
    private String delimiter;
    /**Indicate the encoding method of the returned value*/
    private String encodingType;
    /**Prefix match, used to specify the prefix address of the returned file*/
    private String prefix;
    /**Max number of entries returned each time, default is 1000*/
    private String maxUploads;
    /**Used together with upload-id-marker
     If upload-id-marker is not specified, entries whose ObjectNames are in front of key-marker
     (according to alphabetical order) will be listed.If upload-id-marker is specified, besides
     the above entries, those whose ObjectNames are equal to key-marker and UploadIDs are in front
     of upload-id-marker (according to alphabetical order) will also be listed.*/
    private String keyMarker;
    /**Used together with key-marker
     If key-marker is not specified, upload-id-marker will be ignored
     If key-marker is specified, entries whose ObjectNames are in front of key-marker
     (according to alphabetical order) will be listed, and entries whose ObjectNames
     are equal to key-marker and UploadIDs are in front of upload-id-marker
     (according to alphabetical order) will also be listed.*/
    private String uploadIdMarker;

    public ListMultiUploadsRequest(String bucket){
        setBucket(bucket);
        contentType = QCloudNetWorkConstants.ContentType.X_WWW_FORM_URLENCODED;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
    }

    @Override
    protected void build() throws CosXmlClientException {
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

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

        responseBodySerializer = new ResponseXmlS3BodySerializer(ListMultiUploadsResult.class);

    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("uploads",null);
        if(!TextUtils.isEmpty(delimiter)){
            requestQueryParams .put("delimiter",delimiter);
        }
        if(!TextUtils.isEmpty(encodingType)){
            requestQueryParams.put("Encoding-type",encodingType);
        }
        if(!TextUtils.isEmpty(prefix)){
            requestQueryParams.put("Prefix",prefix);
        }
        if(!TextUtils.isEmpty(maxUploads)){
            requestQueryParams .put("max-uploads",maxUploads);
        }
        if(!TextUtils.isEmpty(keyMarker)){
            requestQueryParams.put("key-marker",keyMarker);
        }
        if(!TextUtils.isEmpty(uploadIdMarker)){
            requestQueryParams.put("upload-id-marker",uploadIdMarker);
        }
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.GET;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    /**
     * <p>
     * 设置请求的delimiter
     * </p>
     * <p>
     * 定界符为一个符号，对 Object 名字包含指定前缀且第一次出现 delimiter 字符之间的 Object 作为一组元素：common prefix。
     * 如果没有 prefix，则从路径起点开始
     * </p>
     *
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * 获取请求的delimiter
     *
     * @return
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * <p>
     * 设置返回值的编码格式。
     * </p>
     * <p>
     * 合法值：url
     * </p>
     *
     * @param encodingType
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * 获取返回值的编码格式
     *
     * @return
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * <p>
     * 设置请求的key-marker
     * </p>
     *
     * <p>
     * 与 upload-id-marker 一起使用
     * 当 upload-id-marker 未被指定时，ObjectName 字母顺序大于 key-marker 的条目将被列出
     * 当upload-id-marker被指定时，ObjectName 字母顺序大于key-marker的条目被列出，
     * ObjectName 字母顺序等于 key-marker 同时 UploadID 大于 upload-id-marker 的条目将被列出。
     * </p>
     *
     * @param keyMarker
     */
    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    /**
     * 获取请求的key-marker
     *
     * @return
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * 设置最大返回的 multipart 数量，合法取值从1到1000，默认1000
     *
     * @param maxUploads
     */
    public void setMaxUploads(String maxUploads) {
        this.maxUploads = maxUploads;
    }

    /**
     * 获取由用户设置的maxUploads值
     *
     * @return
     */
    public String getMaxUploads() {
        return maxUploads;
    }

    /**
     * <p>
     * 设置限定返回的 Object 的前缀
     * </p>
     *
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取用户设置的前缀prefix
     *
     * @return
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>
     * 设置请求的Upload-id-marker
     * </p>
     * <p>
     * 与 key-marker 一起使用
     * 当 key-marker 未被指定时，upload-id-marker 将被忽略
     * 当 key-marker 被指定时，ObjectName字母顺序大于 key-marker 的条目被列出，
     * ObjectName 字母顺序等于 key-marker 同时 UploadID 大于 upload-id-marker 的条目将被列出。
     * </p>
     * @param uploadIdMarker
     */
    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    /**
     * 获取设置的upload-id-marker
     *
     * @return
     */
    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

}
