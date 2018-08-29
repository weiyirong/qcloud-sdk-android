package com.tencent.cos.xml;


import com.tencent.cos.xml.common.VersionInfo;

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

    private boolean isDebuggable;

    public CosXmlServiceConfig(Builder builder){
        protocol = builder.protocol;
        userAgent = builder.userAgent;
        appid = builder.appid;
        region = builder.region;
        isDebuggable = builder.isDebuggable;
        this.ip = builder.ip;
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

    public final static class Builder{

        private String protocol;
        private String userAgent;

        private String region;
        private String appid;
        private String ip;

        private boolean isDebuggable;

        public Builder(){
            protocol = DEFAULT_PROTOCOL;
            userAgent =DEFAULT_USER_AGENT;
            isDebuggable = false;
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

        public CosXmlServiceConfig builder(){
            return new CosXmlServiceConfig(this);
        }
    }
}
