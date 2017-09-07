package com.tencent.qcloud.network.request.serializer;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.request.serializer.body.RequestBodySerializer;
import com.tencent.qcloud.network.tools.QCloudJsonTools;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 构造一个Http请求需要的参数和方法
 *
 *
 * 一个Http请求构建步骤
 *
 * 1、request中添加和设置参数
 *
 * 2、service中添加参数，并调用request的build方法进行最终的构建
 *
 *
 *
 *
 *
 * 这个类已经被废弃了，改用QCloudOkHttpRequestBuilder替代
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

@Deprecated
public class QCloudHttpRequestOrigin {

    private static Logger logger = LoggerFactory.getLogger(QCloudHttpRequestOrigin.class);

    /**
     * POST、GET
     */
    private String method;

    /**
     *
     */
    private Uri uri;


    private Map<String, String> headers;


    private RequestBody requestBody;

    private QCloudHttpRequestOrigin(String method, Uri uri, Map<String, String> headers, RequestBody requestBody) {

        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public Request build() {

        return new Request.Builder()
                .url(uri.toString())
                .headers(Headers.of(headers))
                .method(method, requestBody)
                .build();
    }

    @Deprecated
    static public class Builder {

        private String scheme;

        private StringBuilder host;

        private StringBuilder path;

        private String method;

        private Map<String, String> headers;


        private Uri.Builder builder;

        /**
         * 需要放在Body中的键值对
         */
        private Map<String, String> bodyKeyValues;


        /**
         * 需要上传的文件
         *
         * key 为文件路径，value 文件标识
         */
        private Map<String, String> uploadFilePaths;

        private byte[] uploadByteArray;


        private Map<String, String> queries;

        private StringBuilder query;

        /**
         * 如果请求是有嵌套的JSON，可以去定义相应的BodyJsonSerializer，
         * 如果嫌麻烦也可以直接给一个JSONObject对象。
         *
         * JSON序列化类会优先序列化这个对象，如果这个对象不存在则使用默认的bodyParameters
         */
        private JSONObject jsonHttpBody;

        public Builder() {

            headers = new HashMap<>();

            bodyKeyValues = new HashMap<>();

            uploadFilePaths = new HashMap<>();

            path = new StringBuilder();

            host = new StringBuilder();

            query = new StringBuilder();

            builder = new Uri.Builder();

            queries = new HashMap<>();
        }

        //
        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

//        public Builder host(String host) {
//            this.host = host;
//            return this;
//        }

        public Builder hostAddFront(String part) {

            host.insert(0, part);
            return this;
        }

        public Builder hostAddRear(String part) {

            host.append(part);
            return this;
        }

        /**
         * 在每次拼接path时，如果串不以'/'开始，会自动在串前添加 '/' ，对于尾部不做处理
         * @param part
         * @return
         */
        public Builder pathAddFront(String part) {
            path.insert(0, part);
            if (!part.startsWith("/")) { // 如果part没有以 / 开始，则自动添加 /
                path.insert(0, "/");
            }
            return this;
        }

        /**
         * 在每次拼接path时，如果串不以'/'开始，会自动在串前添加 '/' ，对于尾部不做处理
         * @param part
         * @return
         */
        public Builder pathAddRear(String part) {


            if (!part.startsWith("/")) { // 如果part没有以 / 开始，则自动添加 /
                path.append("/");
            }
            path.append(part);
            return this;
        }


        public Builder method(String method) {

            this.method = method;
            return this;
        }

        /**
         * JSON/Multipart 均可用
         *
         * @param key
         * @param values
         * @return
         */
        public Builder bodyKeyValue(String key, String values) {
            bodyKeyValues.put(key, values);
            return this;
        }

        /**
         * JSON Body的专属方法，将一个Map转化为字符串后添加到Body中，一般可用于两级的JSON字符串转化为单级JSON处理
         *
         * @param key
         * @param values
         * @return
         */
        public Builder bodyKeyValue(String key, Map<String, String> values) {
            bodyKeyValues.put(key, QCloudJsonTools.Map2JsonString(values));
            return this;
        }

        /**
         * Multipart Body的专属方法，用于上传文件
         *
         * @param filePath
         * @param fileMessage
         * @return
         */
        public Builder uploadFilePath(String filePath, String fileMessage) {
            uploadFilePaths.put(filePath, fileMessage);
            return this;
        }

        public Builder uploadByteArray(byte[] content) {
            this.uploadByteArray = content;
            return this;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder query(String key, String value) {

            queries.put(key, value);

            //builder.appendQueryParameter(key, value);
            if (!TextUtils.isEmpty(query.toString())) {
                query.append("&");
            }
            query.append(Uri.encode(key));
            if (!TextUtils.isEmpty(value)) {
                query.append("=");
                query.append(Uri.encode(value));
            }

            return this;
        }

        public QCloudHttpRequestOrigin build(RequestBodySerializer bodySerializer) {

            QCloudLogger.debug(logger, "QCloudHttpRequestOrigin build");
            RequestBody requestBody = null;

            if (bodySerializer != null) {

                requestBody = bodySerializer.serialize();


            } else {
                QCloudLogger.error(logger, "body serializer is null");
            }

            QCloudLogger.debug(logger, "request body serialize success");
            builder.scheme(scheme)
                    .encodedQuery(query.toString())
                    .authority(host.toString())
                    .path(path.toString());

            QCloudLogger.debug(logger, "real url is {}", builder.build().toString());
            return new QCloudHttpRequestOrigin(method, builder.build(), headers, requestBody);
        }

        public JSONObject getJsonHttpBody() {
            return jsonHttpBody;
        }

        public Map<String, String> getBodyKeyValues() {
            return bodyKeyValues;
        }

        public Map<String, String> getUploadFilePaths() {
            return uploadFilePaths;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * 获得未经 url encode的query map
         *
         * @return
         */
        public Map<String, String> getQueries() {
            return queries;
        }


        public String getPath() {
            return path.toString();
        }

        public String getMethod() {
            return method;
        }

        public String getHost() {
            return host.toString();
        }

        public byte[] getUploadByteArray() {
            return uploadByteArray;
        }
    }

}
