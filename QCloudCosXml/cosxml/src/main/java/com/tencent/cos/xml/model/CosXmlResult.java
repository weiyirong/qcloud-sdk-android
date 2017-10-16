package com.tencent.cos.xml.model;


import com.tencent.cos.xml.model.tag.COSXMLError;
import com.tencent.qcloud.core.network.QCloudResult;
import com.thoughtworks.xstream.annotations.XStreamAlias;


import java.util.List;


/**
 * <p>
 * COS XML SDK请求结果基类。
 * </p>
 *
 *
 */
public abstract class CosXmlResult  extends QCloudResult {
    /**
     * 如果请求错误，服务端返回的错误信息
     * @see COSXMLError
     */
    @XStreamAlias("Error")
    public COSXMLError error;

    /**
     * return eTag( such as md5) for object
     */
    private String eTag;

    /**
     *  返回对象的URL
     */
    public String accessUrl;

    /**
     * 返回对象的eTag值
     */
    public String getETag() {
        if(getHeaders() != null){
            List<String> resultHeader = getHeaders().get("ETag");
            if( resultHeader != null && resultHeader.size() > 0){
                eTag = resultHeader.get(0);
            }
        }
        return eTag;
    }

    /**
     *  print response headers for result
     * @return String
     */
    public String printHeaders(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getHttpCode()).append(" ").append(getHttpMessage()).append("\n");
        if(getHeaders() != null){
            List<String> resultHeader;
            resultHeader = getHeaders().get("Content-Type");
            if( resultHeader != null && resultHeader.size() > 0){
                String contentType = resultHeader.get(0);
                stringBuilder.append("Content-Type").append(" ").append(contentType).append("\n");
            }
            resultHeader = getHeaders().get("Content-Length");
            if( resultHeader != null && resultHeader.size() > 0){
                long contentLength = Long.parseLong(resultHeader.get(0));
                stringBuilder.append("Content-Length").append(" ").append(contentLength).append("\n");
            }
            resultHeader = getHeaders().get("Connection");
            if( resultHeader != null && resultHeader.size() > 0){
                String connection = resultHeader.get(0);
                stringBuilder.append("Connection").append(" ").append(connection).append("\n");
            }
            resultHeader = getHeaders().get("Date");
            if( resultHeader != null && resultHeader.size() > 0){
                String date = resultHeader.get(0);
                stringBuilder.append("Date").append(" ").append(date).append("\n");
            }
            resultHeader = getHeaders().get("ETag");
            if( resultHeader != null && resultHeader.size() > 0){
                String eTag = resultHeader.get(0);
                stringBuilder.append("ETag").append(" ").append(eTag).append("\n");
            }
            resultHeader = getHeaders().get("Server");
            if( resultHeader != null && resultHeader.size() > 0){
                String server = resultHeader.get(0);
                stringBuilder.append("Server").append(" ").append(server).append("\n");
            }
            resultHeader = getHeaders().get("x-cos-request-id");
            if( resultHeader != null && resultHeader.size() > 0){
                String xCOSRequestId = resultHeader.get(0);
                stringBuilder.append("x-cos-request-id").append(" ").append(xCOSRequestId).append("\n");
            }
            resultHeader = getHeaders().get("xCOSTraceId");
            if( resultHeader != null && resultHeader.size() > 0){
                String xCOSTraceId = resultHeader.get(0);
                stringBuilder.append("xCOSTraceId").append(" ").append(xCOSTraceId).append("\n");
            }
            resultHeader = getHeaders().get("OkHttp-Sent-Millis");
            if( resultHeader != null && resultHeader.size() > 0){
                String okHttpSentTime = resultHeader.get(0);
                stringBuilder.append("OkHttp-Sent-Millis").append(" ").append(okHttpSentTime).append("\n");
            }
            resultHeader = getHeaders().get("OkHttp-Received-Millis");
            if( resultHeader != null && resultHeader.size() > 0){
                String okHttpReceiveTime = resultHeader.get(0);
                stringBuilder.append("OkHttp-Received-Millis").append(" ").append(okHttpReceiveTime).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     *  print response body for result
     * @return String
     */
    public String printBody(){
        return null;
    }

    /**
     * print error for result, {@link COSXMLError}
     * @return String
     */
    public String printError(){
        return error != null ? error.toString() : null;
    }
}
