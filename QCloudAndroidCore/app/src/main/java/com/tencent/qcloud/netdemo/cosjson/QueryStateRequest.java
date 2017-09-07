package com.tencent.qcloud.netdemo.cosjson;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.response.serializer.body.ResponseJsonBodyLowestSerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpOkSerializer;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class QueryStateRequest extends QCloudHttpRequest{


    /**
     * 业务bucket，未提供setter方法
     */
    private final String bucket;

    /**
     * 业务path，未提供setter方法
     */
    private final String path;


    public QueryStateRequest(String bucket, String path) {

        // 初始化bucket和path
        this.bucket = bucket;
        this.path = path;
    }

    @Override
    public void build() {
        // 请求类型：
        // Constance.REQUEST_TYPE_CMD : 命令型请求  默认值
        // Constance.REQUEST_TYPE_LOAD : 上传下载式请求
        // requestType = QCloudRequestConst.QCLOUD_REQUEST_TYPE_CMD;
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_HIGH;

        // HTTP请求的method
        // Constance.HTTP_METHOD_POST  POST 默认值
        // Constance.HTTP_METHOD_GET   GET
        requestOriginBuilder.method(QCloudNetWorkConst.HTTP_METHOD_GET);

        // 请求的header

        // 请求的路径
        requestOriginBuilder.pathAddRear(bucket);
        requestOriginBuilder.pathAddRear(path);
        requestOriginBuilder.query("op", "stat");

        // 请求Body
        //requestOriginBuilder.bodyKeyValue(ModelConstance.OP, ModelConstance.OP_CREATE);
        //requestOriginBuilder.bodyKeyValue(ModelConstance.BIZ_ATTR, "");

        //requestOriginBuilder.query(key, value);

        // HTTP请求的Header
        //Map<String, String> headers = requestData.getHeaderKeyValues();

        // HTTP请求的body


        // 签名需要的参数
        getSignKeyValues().put("b", bucket);
        getSignKeyValues().put("f", path);

        // 请求序列化过程
        responseSerializer = new HttpOkSerializer();

        //requestBodySerializer = new RequestJsonBodySerializer();

        // 相应反序列化过程
        responseBodySerializer = new ResponseJsonBodyLowestSerializer(QueryStateResult.class);
    }

    public String getBucket() {
        return bucket;
    }

    public String getPath() {
        return path;
    }
}
