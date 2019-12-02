package com.tencent.cos.xml.transfer.constraint;

/**
 * Created by rickenwang on 2019-11-25.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public enum NetworkType {

    NONE(0),
    CELLULAR(1),
    WIFI(2),
    ANY(3);

    private final int value;

    NetworkType(int value) {
        this.value = value;
    }

    public static int serialize(NetworkType networkType) {
        return networkType.value;
    }

    public static NetworkType deserialize(int value) {

        for (NetworkType networkType : NetworkType.values()) {
            if (networkType.value == value) {
                return networkType;
            }
        }
        return null;
    }
}
