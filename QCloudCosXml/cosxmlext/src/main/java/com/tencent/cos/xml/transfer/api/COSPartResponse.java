package com.tencent.cos.xml.transfer.api;

/**
 * Created by rickenwang on 2019-11-21.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class COSPartResponse<T> extends COSResponse<T> {

    private T data;

    public COSPartResponse(T data) {

        this.data = data;
    }

    public T getData() {
        return data;
    }
}
