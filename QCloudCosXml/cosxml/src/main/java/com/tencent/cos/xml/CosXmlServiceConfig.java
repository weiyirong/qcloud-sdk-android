package com.tencent.cos.xml;


import android.net.Uri;
import android.text.TextUtils;

import com.tencent.cos.xml.common.VersionInfo;
import com.tencent.qcloud.core.http.QCloudHttpRetryHandler;
import com.tencent.qcloud.core.task.RetryStrategy;

import java.util.concurrent.Executor;

/**
 * Client configuration options such as timeout settings, protocol string, max
 * retry attempts, etc.
 */

public class CosXmlServiceConfig {

    /**
     * The default protocol to use when connecting to cos Services.
     */
    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";

    public static final String ACCELERATE_ENDPOINT_SUFFIX = "cos-accelerate";

    /**
     * The default user agent header for cos android sdk clients.
     */
    public static final String DEFAULT_USER_AGENT = VersionInfo.getUserAgent();

    private String protocol;
    private String userAgent;

    private String region;
    private String appid;

    private String host;
    private int port;
    private String endpointSuffix;

    private boolean bucketInPath;

    private boolean isDebuggable;

    private RetryStrategy retryStrategy;
    private QCloudHttpRetryHandler qCloudHttpRetryHandler;

    private int connectionTimeout;
    private int socketTimeout;

    private Executor executor;

    private boolean isQuic;

    public CosXmlServiceConfig(Builder builder) {
        this.protocol = builder.protocol;
        this.userAgent = builder.userAgent;
        this.isDebuggable = builder.isDebuggable;

        this.appid = builder.appid;
        this.region = builder.region;
        this.host = builder.host;
        this.port = builder.port;
        this.endpointSuffix = builder.endpointSuffix;
        this.bucketInPath = builder.bucketInPath;
        if (TextUtils.isEmpty(endpointSuffix) && TextUtils.isEmpty(region) &&
                TextUtils.isEmpty(host)) {
            throw new IllegalArgumentException("please set host or endpointSuffix or region !");
        }

        this.retryStrategy = builder.retryStrategy;
        this.qCloudHttpRetryHandler = builder.qCloudHttpRetryHandler;
        this.socketTimeout = builder.socketTimeout;
        this.connectionTimeout = builder.connectionTimeout;

        this.executor = builder.executor;
        this.isQuic = builder.isQuic;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRegion() {
        return region;
    }

    public String getBucket(String bucket) {
        return getBucket(bucket, appid);
    }

    public String getBucket(String bucket, String appid) {
        String myBucket = bucket;
        if (bucket != null && !bucket.endsWith("-" + appid) && !TextUtils.isEmpty(appid)){
            myBucket = bucket + "-" + appid;
        }
        return myBucket;
    }

    public String getAppid() {
        return appid;
    }

    public String getHost(String bucket,
                          boolean isSupportAccelerate) {
        return getHost(bucket, null, isSupportAccelerate);
    }

    public int getPort(){
        return port;
    }

    @Deprecated
    public String getHost(String bucket, String region,
                          boolean isSupportAccelerate) {
        return getHost(bucket, region, appid, isSupportAccelerate);
    }

    public String getHost(String bucket, String region,
                          boolean isSupportAccelerate, boolean isHeader) {
        return getHost(bucket, region, appid, isSupportAccelerate, isHeader);
    }

    public String getHost(String bucket, String region,
                          String appId, boolean isSupportAccelerate, boolean isHeader){
        if (!isHeader && !TextUtils.isEmpty(host)) {
            return host;
        }

        String myBucket = getBucket(bucket, appId);

        String hostBuilder = "";
        if (!bucketInPath) {
            hostBuilder += myBucket + ".";
        }
        hostBuilder += getEndpointSuffix(region, isSupportAccelerate);
        return hostBuilder;
    }

    @Deprecated
    public String getHost(String bucket, String region,
                          String appId, boolean isSupportAccelerate) {
        return getHost(bucket, region, appId, isSupportAccelerate, false);
    }

    public String getEndpointSuffix() {
        return getEndpointSuffix(region, false);
    }

    public String getEndpointSuffix(String region,
                                    boolean isSupportAccelerate) {
        String myRegion = TextUtils.isEmpty(region) ? getRegion() : region;
        String myEndpointSuffix = endpointSuffix;
        if (endpointSuffix == null && myRegion != null) {
            myEndpointSuffix = "cos." + myRegion + ".myqcloud.com";
        }
        myEndpointSuffix = substituteEndpointSuffix(myEndpointSuffix, myRegion);
        if (myEndpointSuffix != null && isSupportAccelerate) {
            myEndpointSuffix = myEndpointSuffix.replace("cos." + myRegion,
                    ACCELERATE_ENDPOINT_SUFFIX);
        }
        return myEndpointSuffix;
    }

    private String substituteEndpointSuffix(String formatString,
                                            String region) {
        if (!TextUtils.isEmpty(formatString) && region != null) {
            return formatString
                    .replace("${region}", region);
        }
        return formatString;
    }

    public String getUrlPath(String bucket, String cosPath) {
        StringBuilder path = new StringBuilder();

        if (bucketInPath) {
            String myBucket = bucket;
            if (!bucket.endsWith("-" + appid) && !TextUtils.isEmpty(appid)){
                myBucket = bucket + "-" + appid;
            }
            path.append("/").append(myBucket);
        }

        if(cosPath != null && !cosPath.startsWith("/")){
            path.append("/").append(cosPath);
        } else {
            path.append(cosPath);
        }

        return path.toString();
    }

    private boolean isEndWithV4Appid(String bucket) {

        String appid = extractAppidFromBucket(bucket);
        return isCosV4Appid(appid);
    }

    private String extractAppidFromBucket(String bucket) {

        if (bucket == null || !bucket.contains("-") || bucket.endsWith("-")) {
            return "";
        }
        int index = bucket.lastIndexOf("-");
        return bucket.substring(index + 1);
    }

    private boolean isCosV4Appid(String appid) {

        if( appid == null || appid.length() != 8 || !appid.startsWith("100")) {
            return false;
        }

        try {
            Long.valueOf(appid);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }


    public boolean isDebuggable() {
        return isDebuggable;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public QCloudHttpRetryHandler getQCloudHttpRetryHandler(){
        return qCloudHttpRetryHandler;
    }

    public Executor getExecutor(){
        return executor;
    }

    public boolean isEnableQuic(){
        return isQuic;
    }

    public final static class Builder {

        private String protocol;
        private String userAgent;

        private String region;
        private String appid;
        private String host;
        private int port = -1;
        private String endpointSuffix;

        private boolean bucketInPath;

        private boolean isDebuggable;

        private RetryStrategy retryStrategy;
        private QCloudHttpRetryHandler qCloudHttpRetryHandler;

        private int connectionTimeout = 15 * 1000;  //in milliseconds
        private int socketTimeout = 30 * 1000;  //in milliseconds

        private Executor executor;

        private boolean isQuic = false;

        public Builder() {
            protocol = HTTP_PROTOCOL;
            userAgent = DEFAULT_USER_AGENT;
            isDebuggable = false;
            retryStrategy = RetryStrategy.DEFAULT;
            bucketInPath = false;
        }

        public Builder setConnectionTimeout(int connectionTimeoutMills) {
            this.connectionTimeout = connectionTimeoutMills;
            return this;
        }

        public Builder setSocketTimeout(int socketTimeoutMills) {
            this.socketTimeout = socketTimeoutMills;
            return this;
        }

        public Builder isHttps(boolean isHttps) {
            if (isHttps) {
                protocol = HTTPS_PROTOCOL;
            } else {
                protocol = HTTP_PROTOCOL;
            }
            return this;
        }

        /**
         * 设置用户的 appid 和存储桶的地域。
         * <br>
         * COS 服务的 存储桶名称的格式为 bucketName-appid ，如果您调用了这个方法设置了 appid，那么后续
         * 在使用存储桶名称时，只需要填写 bucketName 即可，当 SDK 检测到没有以 -appid 结尾时，会进行自动
         * 拼接。
         *
         * <br>
         *
         * 该方法已不推荐使用。建议使用 {@link CosXmlServiceConfig.Builder#setRegion(String)} 来设置
         * 存储桶的地域，后续的存储桶名称需要严格按照 bucketName-appid 的格式，否则会报存储桶不存在的错误，
         * 这种方式下SDK 不会对您填写的存储桶名称做任何处理。
         *
         * @param appid appid
         * @param region 存储桶地域
         */
        @Deprecated
        public Builder setAppidAndRegion(String appid, String region) {
            this.appid = appid;
            this.region = region;
            return this;
        }

        public Builder setRegion(String region) {
            this.region = region;
            return this;
        }

        public Builder setEndpointSuffix(String endpointSuffix) {
            this.endpointSuffix = endpointSuffix;
            return this;
        }

        public Builder setHost(Uri uri) {
            this.host = uri.getHost();
            if (uri.getPort() != -1) {
//                this.host += ":" + uri.getPort();
                this.port = uri.getPort();
            }
            this.protocol = uri.getScheme();
            return this;
        }

        public Builder setDebuggable(boolean isDebuggable) {
            this.isDebuggable = isDebuggable;
            return this;
        }

        public Builder setRetryStrategy(RetryStrategy retryStrategy) {
            this.retryStrategy = retryStrategy;
            return this;
        }

        public Builder setRetryHandler(QCloudHttpRetryHandler qCloudHttpRetryHandler){
            this.qCloudHttpRetryHandler = qCloudHttpRetryHandler;
            return this;
        }

        public Builder setBucketInPath(boolean bucketInPath) {
            this.bucketInPath = bucketInPath;
            return this;
        }

        public Builder setExecutor(Executor excutor){
            this.executor = excutor;
            return this;
        }

        public Builder enableQuic(boolean isEnable){
            this.isQuic = isEnable;
            return this;
        }

        public CosXmlServiceConfig builder() {
            return new CosXmlServiceConfig(this);
        }
    }
}
