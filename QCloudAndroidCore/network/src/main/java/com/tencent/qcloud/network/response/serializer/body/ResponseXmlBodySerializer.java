package com.tencent.qcloud.network.response.serializer.body;

import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.annotation.SequenceFieldKeySorter;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Response;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseXmlBodySerializer implements ResponseBodySerializer {

    private Logger logger = LoggerFactory.getLogger(ResponseXmlBodySerializer.class);

    private Class cls;

    public ResponseXmlBodySerializer(Class cls) {

        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudException {

        if (response == null) {

            return null;
        }

        if (response.body() != null) {

            try {
                String xmlString = response.body().string();

                XStream xStream = new XStream(new PureJavaReflectionProvider(new
                        FieldDictionary(new SequenceFieldKeySorter())));
                QCloudLogger.debug(logger, xmlString);

                xStream.processAnnotations(cls);

                QCloudLogger.debug(logger, xStream.fromXML(xmlString).toString());
                return (QCloudResult) xStream.fromXML(xmlString);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ResponseSerializerHelper.noBodyResult(cls, response);
    }
}
