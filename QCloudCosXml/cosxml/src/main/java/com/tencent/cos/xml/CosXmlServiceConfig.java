package com.tencent.cos.xml;


import com.tencent.cos.xml.common.VersionInfo;
import com.tencent.qcloud.core.task.RetryStrategy;

/**
 *
 * Client configuration options such as timeout settings, protocol string, max
 * retry attempts, etc.
 */

public class CosXmlServiceConfig {

    /** The default protocol to use when connecting to cos Services.*/
    public static final String DEFAULT_PROTOCOL = "http";

    /** The default user agent header for cos android sdk clients. */
    public static final String DEFAULT_USER_AGENT = VersionInfo.getUserAgent();

    private String protocol;
    private String userAgent;

    private String region;
    private String appid;
    private String ip;

    private String domainSuffix;

    private boolean bucketInPath;

    private boolean isDebuggable;

    private RetryStrategy retryStrategy;

    public CosXmlServiceConfig(Builder builder){
        protocol = builder.protocol;
        userAgent = builder.userAgent;
        appid = builder.appid;
        region = builder.region;
        isDebuggable = builder.isDebuggable;
        this.ip = builder.ip;
        this.domainSuffix = builder.domainSuffix;
        this.retryStrategy = builder.retryStrategy;
        this.bucketInPath = builder.bucketInPath;
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

    public String getAppid() {
        return appid;
    }

    public String getIp(){
        return ip;
    }

    public boolean isDebuggable(){
        return isDebuggable;
    }

    public String getDomainSuffix() {
        return domainSuffix;
    }


    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public boolean isBucketInPath() {
        return bucketInPath;
    }

    public final static class Builder{

        private String protocol;
        private String userAgent;

        private String region;
        private String appid;
        private String ip;

        private String domainSuffix;
        private boolean bucketInPath;
        private boolean isDebuggable;

        private RetryStrategy retryStrategy;

        public Builder(){
            protocol = DEFAULT_PROTOCOL;
            userAgent =DEFAULT_USER_AGENT;
            isDebuggable = false;
            domainSuffix = "myqcloud.com";
            retryStrategy = RetryStrategy.DEFAULT;
            bucketInPath = false;
        }

        public Builder isHttps(boolean isHttps){
            if(isHttps){
                protocol = "https";
            }else {
                protocol = "http";
            }
            return this;
        }

        public Builder setAppidAndRegion(String appid, String region){
            this.appid = appid;
            this.region = region;
            return this;
        }

        @Deprecated
        public Builder setHost(String ip){
            this.ip = ip;
            return this;
        }

        public Builder setDebuggable(boolean isDebuggable){
            this.isDebuggable = isDebuggable;
            return this;
        }

        public Builder setDomainSuffix(String domainSuffix) {
            this.domainSuffix = domainSuffix;
            return this;
        }

        public Builder setRetryStrategy(RetryStrategy retryStrategy) {
            this.retryStrategy = retryStrategy;
            return this;
        }

        public Builder setBucketInPath(boolean bucketInPath) {
            this.bucketInPath = bucketInPath;
            return this;
        }

        public CosXmlServiceConfig builder(){
            return new CosXmlServiceConfig(this);
        }
    }
}
