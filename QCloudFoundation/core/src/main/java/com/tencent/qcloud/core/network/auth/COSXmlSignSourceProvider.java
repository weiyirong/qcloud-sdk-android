package com.tencent.qcloud.core.network.auth;


import com.tencent.qcloud.core.network.QCloudRealCall;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.util.QCDigestUtils;
import com.tencent.qcloud.core.util.QCHexUtils;
import com.tencent.qcloud.core.util.QCHttpParameterUtils;
import com.tencent.qcloud.core.util.QCCollectionUtils;

import java.io.IOException;
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

import static com.tencent.qcloud.core.network.QCloudNetWorkConstants.HttpHeader.CONTENT_LENGTH;
import static com.tencent.qcloud.core.network.QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE;
import static com.tencent.qcloud.core.network.QCloudNetWorkConstants.HttpHeader.DATE;
import static com.tencent.qcloud.core.network.QCloudNetWorkConstants.HttpHeader.TRANSFER_ENCODING;


/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class COSXmlSignSourceProvider implements QCloudSignSourceProvider {

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

    public COSXmlSignSourceProvider(long duration) {

        this.duration = duration;
        headers = new HashSet<String>();
        paras = new HashSet<String>();
        realSignHeader = new HashSet<String>();
        realSignParas = new HashSet<String>();
    }

    /**
     * 需要加密的parameter
     *
     * @param key
     */
    public void parameter(String key) {
        paras.add(key);
    }

    public void parameters(Set<String> keys) {
        paras.addAll(keys);
    }

    /**
     * 需要加密的header
     *
     * @param key
     */
    public void header(String key) {

        headers.add(key);
    }

    public void headers(Set<String> keys) {
        headers.addAll(keys);
    }

    /**
     * 签名需要的参数
     * <p>
     * 1、q-sign-algorithm : 固定值 sha1
     * <p>
     * 2、q-ak ：
     *
     * @return
     */
    @Override
    public String source(QCloudRealCall realCall) throws QCloudClientException {
        if (realCall == null) {
            return null;
        }

        //QCloudOkHttpRequestBuilder builder = httpRequest.getRequestOriginBuilder();
        //Request request = builder.build();
        Request request = realCall.getHttpRequest();

        if (headers.size() > 0) {

            Set<String> lowerCaseHeaders = QCCollectionUtils.getLowerCase(headers);
            Request.Builder requestBuilder = request.newBuilder();

            RequestBody requestBody = request.body();

            // 1、是否存在Content-Type
            if (lowerCaseHeaders != null && lowerCaseHeaders.contains(CONTENT_TYPE.toLowerCase())) {

                if (requestBody != null) {
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        requestBuilder.header(CONTENT_TYPE, contentType.toString());
                    }
                }
            }

            // 2、是否存在Content-Length
            if (lowerCaseHeaders != null && lowerCaseHeaders.contains(CONTENT_LENGTH.toLowerCase())) {

                if (requestBody != null) {

                    long contentLength = -1;
                    try {
                        contentLength = requestBody.contentLength();

                    } catch (IOException e) {
                        throw new QCloudClientException("read content length fails", e);
                    }
                    if (contentLength != -1) {
                        requestBuilder.header(CONTENT_LENGTH, Long.toString(contentLength));
                        requestBuilder.removeHeader(TRANSFER_ENCODING);
                    } else {
                        requestBuilder.header(TRANSFER_ENCODING, "chunked");
                        requestBuilder.removeHeader(CONTENT_LENGTH);
                    }
                }

            }

            // 3、是否存在Date
            if (lowerCaseHeaders != null && lowerCaseHeaders.contains(DATE.toLowerCase())) {

                Date d = new Date();
                DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                requestBuilder.header(DATE, format.format(d));
            }

            request = requestBuilder.build();
        }
        realCall.setHttpRequest(request);


        // 添加method
        StringBuilder formatString = new StringBuilder(request.method().toLowerCase());
        formatString.append("\n");


        // 添加path
        String path = QCHttpParameterUtils.urlDecodeString(request.url().url().getPath());
        formatString.append(path);
        formatString.append("\n");

        // 添加parameters
        //Map<String, String> encodedParasMap = QCloudMapTools.letterDownSort(builder.getQueries(), paras);
        //String paraString = QCloudMapTools.map2String(encodedParasMap);
        String paraString = QCHttpParameterUtils.queryStringForKeys(request.url(), paras, realSignParas);

        if (paraString == null) {
            paraString = "";
        }
        formatString.append(paraString);
        formatString.append("\n");

        // 添加header，得到最终的formatString
        //Map<String, String> encodedHeaderMap = QCloudMapTools.letterDownSort(builder.getHeaders(), headers);
        //String headerString = QCloudMapTools.map2String(encodedHeaderMap);
        String headerString = "";
        if (request.headers() != null) {
            headerString = QCHttpParameterUtils.headersStringForKeys(request.headers(), headers, realSignHeader);
        }
        formatString.append(headerString);
        formatString.append("\n");


        StringBuilder stringToSign = new StringBuilder();

        // 追加 q-sign-algorithm
        stringToSign.append(CredentialProviderConst.SHA1);
        stringToSign.append("\n");

        // 追加q-sign-time
        long currentTime = System.currentTimeMillis() / 1000;
        long expiredTime = currentTime + duration;
        signTime = currentTime + ";" + expiredTime;
        stringToSign.append(signTime);
        stringToSign.append("\n");

        // 追加 sha1Hash(formatString)
        String formatStringSha1 = new String(QCHexUtils.encodeHex(QCDigestUtils.sha1(formatString.toString())));
        stringToSign.append(formatStringSha1);
        stringToSign.append("\n");

        return stringToSign.toString();
    }


    public String getRealHeaderList() {
        return QCCollectionUtils.joinSemicolon(realSignHeader);
    }

    public String getRealParameterList() {
        return QCCollectionUtils.joinSemicolon(realSignParas);
    }

    public String getSignTime() {
        return signTime;
    }
}
