package com.tencent.qcloud.network.auth;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.tencent.qcloud.network.tools.DigestUtils;
import com.tencent.qcloud.network.tools.HexUtils;
import com.tencent.qcloud.network.tools.QCloudSetTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.tencent.qcloud.network.common.QCloudNetWorkConst.HTTP_HEADER_CONTENT_LENGTH;
import static com.tencent.qcloud.network.common.QCloudNetWorkConst.HTTP_HEADER_CONTENT_TYPE;
import static com.tencent.qcloud.network.common.QCloudNetWorkConst.HTTP_HEADER_DATE;
import static com.tencent.qcloud.network.common.QCloudNetWorkConst.HTTP_HEADER_TRANSFER_ENCODING;


/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class BasicSignatureSourceSerializer implements QCloudSignatureSourceSerializer {

    private Logger logger = LoggerFactory.getLogger(BasicSignatureSourceSerializer.class);

    private QCloudHttpRequest httpRequest;

    private Set<String> paras;

    /**
     * 真正用来签名的参数
     */
    private Set<String> realSignParas;

    /**
     * 真正用来签名的头部
     */
    private Set<String> realSignHeader;

    private Set<String> headers;

    private long duration;

    private String signTime;

    public BasicSignatureSourceSerializer(long duration) {

        this.duration = duration;
        headers = new HashSet<String>();
        paras = new HashSet<String>();
        realSignHeader = new HashSet<String>();
        realSignParas = new HashSet<String>();
    }

    /**
     * 需要加密的parameter
     * @param key
     */
    public void parameter(String key) {
        paras.add(key);
    }

    public void parameters(Set<String> keys) {paras.addAll(keys);}

    /**
     * 需要加密的header
     * @param key
     */
    public void header(String key) {

        headers.add(key);
        QCloudLogger.debug(logger, "headers now is {}", headers);
    }

    public void headers(Set<String> keys) {
        headers.addAll(keys);
    }

    /**
     * 签名需要的参数
     *
     * 1、q-sign-algorithm : 固定值 sha1
     *
     * 2、q-ak ：
     *
     *
     *
     *
     * @return
     */
    public String source() {

        if (httpRequest == null) {
            return null;
        }
        QCloudLogger.debug(logger, "source serializer is {}", this);

        //QCloudOkHttpRequestBuilder builder = httpRequest.getRequestOriginBuilder();
        //Request request = builder.build();
        Request request = httpRequest.getHttpRequest();

        if (headers != null && headers.size() > 0) {

            Set<String> lowerCaseHeaders = QCloudSetTools.getLowerCase(headers);
            Request.Builder requestBuilder = request.newBuilder();

            RequestBody requestBody = request.body();

            // 1、是否存在Content-Type
            if (lowerCaseHeaders.contains(HTTP_HEADER_CONTENT_TYPE.toLowerCase())) {

                if (requestBody != null) {
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        requestBuilder.header(HTTP_HEADER_CONTENT_TYPE, contentType.toString());
                    }
                }
            }

            // 2、是否存在Content-Length
            if (lowerCaseHeaders.contains(HTTP_HEADER_CONTENT_LENGTH.toLowerCase())) {

                if (requestBody != null) {

                    long contentLength = -1;
                    try {
                        contentLength = requestBody.contentLength();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (contentLength != -1) {
                        requestBuilder.header(HTTP_HEADER_CONTENT_LENGTH, Long.toString(contentLength));
                        requestBuilder.removeHeader(HTTP_HEADER_TRANSFER_ENCODING);
                    } else {
                        requestBuilder.header(HTTP_HEADER_TRANSFER_ENCODING, "chunked");
                        requestBuilder.removeHeader(HTTP_HEADER_CONTENT_LENGTH);
                    }
                }

            }

            // 3、是否存在Date
            if (lowerCaseHeaders.contains(HTTP_HEADER_DATE.toLowerCase())) {

                Date d=new Date();
                DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                System.out.println(format.format(d));
                requestBuilder.header(HTTP_HEADER_DATE, format.format(d));
            }

            request = requestBuilder.build();
        }
        httpRequest.setHttpRequest(request);


        // 添加method
        QCloudLogger.debug(logger, "method is {}", request.method());
        StringBuilder formatString = new StringBuilder(request.method().toLowerCase());
        formatString.append("\n");


        // 添加path
        String path = null;
        try {
            path = URLDecoder.decode(request.url().url().getPath(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        QCloudLogger.debug(logger, "path is {}", path);
        formatString.append(path);
        formatString.append("\n");

        // 添加parameters
        QCloudLogger.debug(logger, "query parameter is {}", request.url().query());
        QCloudLogger.debug(logger, "sign parameter is {}", paras.toString());
        //Map<String, String> encodedParasMap = QCloudMapTools.letterDownSort(builder.getQueries(), paras);
        //String paraString = QCloudMapTools.map2String(encodedParasMap);
        String paraString = KeyValuesHelper.queryStringForKeys(request.url(), paras, realSignParas);

        if (paraString == null) {
            paraString = "";
        }
        formatString.append(paraString);
        formatString.append("\n");
        QCloudLogger.debug(logger, "parameter string is {}", paraString);

        // 添加header，得到最终的formatString
        QCloudLogger.debug(logger, "header is {}", request.headers().toString());
        QCloudLogger.debug(logger, "sign header is {}", headers.toString());
        //Map<String, String> encodedHeaderMap = QCloudMapTools.letterDownSort(builder.getHeaders(), headers);
        //String headerString = QCloudMapTools.map2String(encodedHeaderMap);
        String headerString = KeyValuesHelper.headersStringForKeys(request.headers(), headers, realSignHeader);
        if (headerString == null) {
            headerString = "";
        }
        formatString.append(headerString);
        formatString.append("\n");
        QCloudLogger.debug(logger, "header string is {}", headerString);
        QCloudLogger.debug(logger, "final formatString is {}", formatString);


        StringBuilder stringToSign = new StringBuilder();

        // 追加 q-sign-algorithm
        stringToSign.append(CredentialProviderConst.SHA1);
        stringToSign.append("\n");
        QCloudLogger.debug(logger, "StringToSign is {}", stringToSign.toString());

        // 追加q-sign-time
        long currentTime = System.currentTimeMillis() / 1000;
        long expiredTime = currentTime + duration;
        signTime = currentTime+";"+expiredTime;
        stringToSign.append(signTime);
        stringToSign.append("\n");
        QCloudLogger.debug(logger, "StringToSign is {}", stringToSign.toString());

        // 追加 sha1Hash(formatString)
        String formatStringSha1 = new String(HexUtils.encodeHex(DigestUtils.sha1(formatString.toString())));
        stringToSign.append(formatStringSha1);
        stringToSign.append("\n");

        QCloudLogger.debug(logger, "StringToSign is {}", stringToSign.toString());

        return stringToSign.toString();
    }

    public void setHttpRequest(QCloudHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }


    public String getRealHeaderList() {
        return QCloudSetTools.joinSemicolon(realSignHeader);
    }

    public String getRealParameterList() {
        return QCloudSetTools.joinSemicolon(realSignParas);
    }

    public String getSignTime() {
        return signTime;
    }
}
