package com.tencent.qcloud.core.cos;

import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class SequenceFieldKeySorter implements FieldKeySorter{

    @Override
    public Map sort(Class type, Map keyedByFieldKey) {

        Annotation sequence = type.getAnnotation(XmlSequence.class);
        if (sequence != null) {
            final String[] fieldsOrder = ((XmlSequence) sequence).value();
            Map result = new OrderRetainingMap();
            Set<Map.Entry<FieldKey, Field>> fields = keyedByFieldKey.entrySet();
            for (String fieldName : fieldsOrder) {
                if (fieldName != null) {
                    for (Map.Entry<FieldKey, Field> fieldEntry : fields) {
                        if (fieldName.equals(fieldEntry.getKey().getFieldName())) {
                            result.put(fieldEntry.getKey(),
                                    fieldEntry.getValue());
                        }
                    }
                }
            }
            return result;
        } else {
            return keyedByFieldKey;
        }


    }
}
