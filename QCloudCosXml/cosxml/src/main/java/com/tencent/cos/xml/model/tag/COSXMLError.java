package com.tencent.cos.xml.model.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 * <a herf https://www.qcloud.com/document/product/436/7730></a>
 */
@XStreamAlias("Error")
public class COSXMLError {
    /**
     * Error codes are used to locate a unique error condition and determine scenario of the error
     */
    @XStreamAlias("Code")
    public String code;

    /**
     * ontain detailed error information.
     */
    @XStreamAlias("Message")
    public String message;

    /**
     * Resource address: Bucket address or Object address.
     */
    @XStreamAlias("Resource")
    public String resource;

    /**
     * The server will automatically generate a unique ID for the request when the request is sent.
     * When a problem occurs, request-id can help COS locate the problem faster.
     */
    @XStreamAlias("RequestId")
    public String requestId;

    /**
     * When a request encounters an error, the server will automatically generate a unique ID for the error.
     * When a problem occurs, trace-id can help COS locate the problem faster.
     * When a request encounters an error, one trace-id will correspond to one request-id.
     */
    @XStreamAlias("TraceId")
    public String traceId;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Code:").append(code).append("\n")
                .append("Message:").append(message).append("\n")
                .append("Resource:").append(resource).append("\n")
                .append("RequestId:").append(requestId).append("\n")
                .append("TraceId:").append(traceId).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
