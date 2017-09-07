package com.tencent.qcloud.network.request.serializer.body;

import android.util.Log;

import com.tencent.qcloud.network.annotation.SequenceFieldKeySorter;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
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
    public RequestBody serialize() {


        XStream xStream = new XStream(new PureJavaReflectionProvider(new
                FieldDictionary(new SequenceFieldKeySorter())));

        xStream.processAnnotations(xmlObject.getClass());

        return new RequestStringSerializer(xStream.toXML(xmlObject), QCloudNetWorkConst.HTTP_CONTENT_TYPE_XML).serialize();
    }
}
