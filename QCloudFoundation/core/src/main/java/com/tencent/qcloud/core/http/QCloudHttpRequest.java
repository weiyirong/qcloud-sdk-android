package com.tencent.qcloud.core.http;


import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.auth.QCloudSigner;
import com.tencent.qcloud.core.auth.SignerFactory;
import com.tencent.qcloud.core.common.QCloudClientException;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 * Created by wjielai on 2017/11/29.
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public final class QCloudHttpRequest<T> extends HttpRequest<T> {

    private final QCloudSignSourceProvider signProvider;
    private final String signerType;

    public QCloudHttpRequest(Builder<T> builder) {
        super(builder);

        signerType = builder.signerType;
        signProvider = builder.signProvider;
    }

    public QCloudSignSourceProvider getSignProvider() {
        return signProvider;
    }

    QCloudSigner getQCloudSigner() throws QCloudClientException {
        QCloudSigner signer = null;
        if (signerType != null) {
            signer = SignerFactory.getSigner(signerType);
            if (signer == null) {
                throw new QCloudClientException("can't get signer for type : " + signerType);
            }
        }

        return signer;
    }

    public static class Builder<T> extends HttpRequest.Builder<T> {

        private QCloudSignSourceProvider signProvider;
        private String signerType;

        public Builder<T> signer(String signerType, QCloudSignSourceProvider signProvider) {
            this.signerType = signerType;
            this.signProvider = signProvider;
            return this;
        }

        @Override
        public Builder<T> scheme(String scheme) {
            return (Builder<T>) super.scheme(scheme);
        }

        @Override
        public Builder<T> path(String path) {
            return (Builder<T>) super.path(path);
        }

        @Override
        public Builder<T> host(String host) {
            return (Builder<T>) super.host(host);
        }

        @Override
        public Builder<T> method(String method) {
            return (Builder<T>) super.method(method);
        }

        @Override
        public Builder<T> query(String key, String value) {
            return (Builder<T>) super.query(key, value);
        }

        @Override
        public Builder<T> contentMD5() {
            return (Builder<T>) super.contentMD5();
        }

        @Override
        public Builder<T> addHeader(String name, String value) {
            return (Builder<T>) super.addHeader(name, value);
        }

        @Override
        public Builder<T> removeHeader(String name) {
            return (Builder<T>) super.removeHeader(name);
        }

        @Override
        public Builder<T> encodedQuery(Map<String, String> nameValues) {
            return (Builder<T>) super.encodedQuery(nameValues);
        }

        @Override
        public Builder<T> addHeaders(Map<String, List<String>> headers) {
            return (Builder<T>) super.addHeaders(headers);
        }

        @Override
        public Builder<T> query(Map<String, String> nameValues) {
            return (Builder<T>) super.query(nameValues);
        }

        @Override
        public Builder<T> body(RequestBodySerializer bodySerializer) {
            return (Builder<T>) super.body(bodySerializer);
        }

        @Override
        public Builder<T> tag(Object tag) {
            return (Builder<T>) super.tag(tag);
        }

        @Override
        public Builder<T> converter(ResponseBodyConverter<T> responseBodyConverter) {
            return (Builder<T>) super.converter(responseBodyConverter);
        }

        public QCloudHttpRequest<T> build() {
            prepareBuild();
            return new QCloudHttpRequest<>(this);
        }
    }
}
