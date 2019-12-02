package com.tencent.cos.xml.transfer.api;

/**
 * Created by rickenwang on 2019-11-21.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class COSSucessResponse<T> extends COSResponse<T> {


    private T data;

    public COSSucessResponse(T data) {

        this.data = data;
    }

    public T getData() {
        return data;
    }
}
