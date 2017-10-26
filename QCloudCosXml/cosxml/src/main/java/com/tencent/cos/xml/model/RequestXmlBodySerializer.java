package com.tencent.cos.xml.model;

import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestBodySerializer;
import com.tencent.qcloud.core.network.request.serializer.RequestStringSerializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import okhttp3.RequestBody;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class RequestXmlBodySerializer implements RequestBodySerializer {

    private Object xmlObject;

    public RequestXmlBodySerializer(Object object) {

        xmlObject = object;
    }

    @Override
    public RequestBody serialize() throws QCloudClientException {
        XStream xStream = new XStream(new PureJavaReflectionProvider(new
                FieldDictionary(new SequenceFieldKeySorter())));

        xStream.processAnnotations(xmlObject.getClass());

        return new RequestStringSerializer(xStream.toXML(xmlObject), QCloudNetWorkConstants.ContentType.XML).serialize();
    }
}
