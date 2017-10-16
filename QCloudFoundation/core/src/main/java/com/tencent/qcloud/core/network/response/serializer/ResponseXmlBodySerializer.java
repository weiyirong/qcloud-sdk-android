package com.tencent.qcloud.core.network.response.serializer;

import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.annotation.SequenceFieldKeySorter;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import java.io.IOException;

import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseXmlBodySerializer implements ResponseBodySerializer {

    private Class cls;

    public ResponseXmlBodySerializer(Class cls) {

        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudClientException {

        if (response == null) {
            return null;
        }
        if (response.body() != null) {
            try {
                String xmlString = response.body().string();

                XStream xStream = new XStream(new PureJavaReflectionProvider(new
                        FieldDictionary(new SequenceFieldKeySorter())));

                xStream.processAnnotations(cls);

                return (QCloudResult) xStream.fromXML(xmlString);

            } catch (IOException e) {
                throw new QCloudClientException("parse http xml body error", e);
            }
        } else {
            return ResponseHelper.noBodyResult(cls, response);
        }
    }

}
